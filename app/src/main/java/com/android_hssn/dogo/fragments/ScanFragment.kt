package com.explore.pakistan.tourism.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android_hssn.dogo.R
import com.android_hssn.dogo.activities.MainActivity
import com.android_hssn.dogo.tensor.Classifier
import com.android_hssn.dogo.tensor.TensorFlowImageClassifier
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.results_view
import kotlinx.android.synthetic.main.fragment_scan.*
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

class ScanFragment : Fragment() {


    private val INPUT_SIZE = 299
    private val IMAGE_MEAN = 128
    private val IMAGE_STD = 128f
    private val INPUT_NAME = "Mul"
    private val OUTPUT_NAME = "final_result"
    private val MODEL_FILE = "file:///android_asset/stripped.pb"

    private var classifier: Classifier? = null
    protected var inferenceTask: InferenceTask? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    fun setImage(imageUri: Uri) {
        iv_image_to_scan.setImageURI(imageUri)
    }

    public fun classifyLoadedImage(imageUri: Uri) {
        val orientation = getOrientation(requireContext(), imageUri)
        val contentResolver = requireContext().contentResolver
        try {
            val croppedFromGallery: Bitmap
            croppedFromGallery = resizeCropAndRotate(
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri),
                orientation
            )
            requireActivity().runOnUiThread {
                //setImage(croppedFromGallery)
                inferenceTask = InferenceTask()
                inferenceTask!!.execute(croppedFromGallery)
            }
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Unable to load image", Toast.LENGTH_LONG).show()
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

    @Synchronized
    public fun initClassifier() {
        if (classifier == null) try {
            classifier = TensorFlowImageClassifier.create(
                requireActivity().assets,
                MODEL_FILE,
                resources.getStringArray(R.array.breeds_array),
                INPUT_SIZE,
                IMAGE_MEAN,
                IMAGE_STD,
                INPUT_NAME,
                OUTPUT_NAME
            )
        } catch (e: OutOfMemoryError) {
            requireActivity().runOnUiThread {
                //    cameraButton.setEnabled(true)
                //     continuousInferenceButton.setChecked(false)
                Toast.makeText(
                    requireContext(),
                    R.string.error_tf_init,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = ScanFragment()
    }
}