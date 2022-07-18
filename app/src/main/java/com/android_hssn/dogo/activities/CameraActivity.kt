package com.android_hssn.dogo.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.content.Intent
import android.widget.Toast
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_camera.*
import android.view.View
import android.widget.Button
import androidx.core.content.FileProvider
import com.android_hssn.dogo.*
import com.android_hssn.dogo.helpers.CheckPermissionUtil
import com.android_hssn.dogo.helpers.HelperFunctions
import com.android_hssn.dogo.tensor.Classifier
import com.android_hssn.dogo.tensor.TensorFlowImageClassifier
import com.cocosw.bottomsheet.BottomSheet
import com.google.android.gms.ads.MobileAds
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*


class CameraActivity : AppCompatActivity() {


    private val INPUT_SIZE = 299
    private val IMAGE_MEAN = 128
    private val IMAGE_STD = 128f
    private val INPUT_NAME = "Mul"
    private val OUTPUT_NAME = "final_result"
    private val MODEL_FILE = "file:///android_asset/stripped.pb"


    private var fileName = System.currentTimeMillis().toString()
    private val SELECT_FILE = 200
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private var outputFileUri: Uri? = null
    lateinit var rootFolder: File
    private var classifier: Classifier? = null
    protected var inferenceTask: InferenceTask? = null

    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        mContext = this

        MobileAds.initialize(mContext)
        val photoButton: Button = findViewById<View>(R.id.button1) as Button
        photoButton.setOnClickListener {
            fileName = System.currentTimeMillis().toString()
            rootFolder = File(getExternalFilesDir("/Data/"), "images")
            showPopupMenu()

        }
    }

    @Synchronized
    public fun initClassifier() {
        if (classifier == null) try {
            classifier = TensorFlowImageClassifier.create(
                assets,
                MODEL_FILE,
                resources.getStringArray(R.array.breeds_array),
                INPUT_SIZE,
                IMAGE_MEAN,
                IMAGE_STD,
                INPUT_NAME,
                OUTPUT_NAME
            )
        } catch (e: OutOfMemoryError) {
            runOnUiThread {
                //    cameraButton.setEnabled(true)
                //     continuousInferenceButton.setChecked(false)
                Toast.makeText(
                    applicationContext,
                    R.string.error_tf_init,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun showPopupMenu() {
        BottomSheet.Builder(this)
            .sheet(R.menu.select_id_options_bs_list)
            .listener { dialog, which ->
                when (which) {
                    R.id.camera -> OpenCamera()
                    R.id.library -> CheckPermissionUtil.checkWriteSd(
                        this@CameraActivity
                    ) { success: Boolean ->
                        if (success) {
                            galleryIntent()
                        } else {
                            HelperFunctions.showMessage(
                                mContext,
                                resources.getString(R.string.storage_permission_required)
                            )
                        }
                    }
                }
            }.show()
    }

    private fun OpenCamera() {
        CheckPermissionUtil.checkWriteSd(
            this@CameraActivity
        ) { success: Boolean ->
            if (success) {
                CheckPermissionUtil.checkCamera(
                    this@CameraActivity
                ) { success1: Boolean ->
                    if (success1) {
                        takePicFromCamera()
                    } else {
                        HelperFunctions.showMessage(
                            mContext,
                            resources.getString(R.string.camera_permission_required)
                        )
                    }
                }
            } else {
                HelperFunctions.showMessage(
                    mContext,
                    resources.getString(R.string.storage_permission_required)
                )
            }
        }
    }

    private fun galleryIntent() {

        if (!rootFolder.exists()) {
            rootFolder.mkdirs()
        }
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_file)),
            SELECT_FILE
        )
    }

    private fun takePicFromCamera() {
        try {
            if (!rootFolder.exists()) {
                rootFolder.mkdirs()
            }
            val newFile = File(rootFolder, "overview_doc_$fileName.jpg")
            newFile.createNewFile()

            outputFileUri = FileProvider.getUriForFile(
                mContext, mContext.applicationContext.packageName + ".fileprovider", newFile
            )
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            startActivityForResult(
                cameraIntent,
                CAMERA_CAPTURE_IMAGE_REQUEST_CODE
            )
        } catch (e: Exception) {
            HelperFunctions.showMessage(mContext, getString(R.string.something_went_wrong))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    performCrop(outputFileUri)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (resultCode == RESULT_CANCELED) {
                HelperFunctions.showMessage(
                    mContext,
                    getString(R.string.user_cancelled_image_capture)
                )
            } else {
                HelperFunctions.showMessage(mContext, getString(R.string.failed_to_capture_image))
            }
        }
        if (requestCode == SELECT_FILE) {
            if (resultCode == RESULT_OK) {
                val selectedImage = data!!.data
                try {
                    performCrop(selectedImage)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val destinationFilename =

                    HelperFunctions.saveArtworkDocumentFile(
                        result.uri, mContext, rootFolder.absolutePath,
                        "overview_doc_$fileName.jpg"
                    )
                outputFileUri = Uri.fromFile(File(destinationFilename))
                imageView1.setImageURI(outputFileUri)
                classifyLoadedImage(outputFileUri!!)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun performCrop(picUri: Uri?) {
        CropImage.activity(picUri!!)
            .setGuidelines(CropImageView.Guidelines.ON).setMinCropResultSize(100, 100)
            .setMaxCropResultSize(5000, 5000).setAllowRotation(false).setAspectRatio(1, 1)
            .setAutoZoomEnabled(false).start(this)
    }

    private fun classifyLoadedImage(imageUri: Uri) {
        val orientation = getOrientation(applicationContext, imageUri)
        val contentResolver = this.contentResolver
        try {
            val croppedFromGallery: Bitmap
            croppedFromGallery = resizeCropAndRotate(
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri),
                orientation
            )
            runOnUiThread {
                //setImage(croppedFromGallery)
                inferenceTask = InferenceTask()
                inferenceTask!!.execute(croppedFromGallery)
            }
        } catch (e: IOException) {
            Toast.makeText(applicationContext, "Unable to load image", Toast.LENGTH_LONG).show()
        }
    }

    // get orientation of picture
    fun getOrientation(context: Context, photoUri: Uri?): Int {
        /* it's on the external media. */
        try {
            context.contentResolver.query(
                photoUri!!,
                arrayOf(MediaStore.Images.ImageColumns.ORIENTATION),
                null,
                null,
                null
            ).use { cursor ->
                if (cursor!!.count != 1) {
                    cursor.close()
                    return -1
                }
                if (cursor != null && cursor.moveToFirst()) {
                    val r = cursor.getInt(0)
                    cursor.close()
                    return r
                }
            }
        } catch (e: Exception) {
            return -1
        }
        return -1
    }

    private fun resizeCropAndRotate(originalImage: Bitmap, orientation: Int): Bitmap {
        var result = Bitmap.createBitmap(
            INPUT_SIZE,
            INPUT_SIZE,
            Bitmap.Config.ARGB_8888
        )
        val originalWidth = originalImage.width.toFloat()
        val originalHeight = originalImage.height.toFloat()
        val canvas = Canvas(result)
        val scale: Float = INPUT_SIZE / originalWidth
        val xTranslation = 0.0f
        val yTranslation: Float =
            (INPUT_SIZE - originalHeight * scale) / 2.0f
        val transformation = Matrix()
        transformation.postTranslate(xTranslation, yTranslation)
        transformation.preScale(scale, scale)
        val paint = Paint()
        paint.isFilterBitmap = true
        canvas.drawBitmap(originalImage, transformation, paint)

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */if (orientation > 0) {
            val matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            result = Bitmap.createBitmap(
                result, 0, 0, INPUT_SIZE,
                INPUT_SIZE, matrix, true
            )
        }
        return result
    }

    fun updateResultsView(results: List<Classifier.Recognition?>?) {
        val sb = StringBuilder()
        if (results != null) {
            if (results.size > 0) {
                for (recog in results) {
                    val text = java.lang.String.format(
                        Locale.getDefault(), "%s: %d %%\n",
                        recog!!.getTitle(), Math.round(recog.getConfidence() * 100)
                    )
                    sb.append(text)
                }
            } else {
                sb.append(getString(R.string.no_detection))
            }
        }
        results_view.setText(sb.toString())
    }

    inner class InferenceTask : AsyncTask<Bitmap?, Void?, List<Classifier.Recognition?>?>() {
        /*  override fun onPreExecute() {
              if (!continuousInference) progressBar.setVisibility(View.VISIBLE)
          }

          protected override fun onPostExecute(recognitions: List<Classifier.Recognition?>) {
              progressBar.setVisibility(View.GONE)
              if (!isCancelled) updateResults(recognitions)
              readyForNextImage()
          }*/

        override fun onPostExecute(result: List<Classifier.Recognition?>?) {
            super.onPostExecute(result)
            updateResultsView(result)

        }

        override fun doInBackground(vararg p0: Bitmap?): List<Classifier.Recognition?>? {
            initClassifier()
            return if (!isCancelled && classifier != null) {
                classifier!!.recognizeImage(p0[0])
            } else null
        }
    }


}