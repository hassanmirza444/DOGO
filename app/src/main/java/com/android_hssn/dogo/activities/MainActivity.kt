package com.android_hssn.dogo.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.android_hssn.dogo.R
import com.android_hssn.dogo.databinding.ActivityMainBinding
import com.android_hssn.dogo.helpers.CheckPermissionUtil
import com.android_hssn.dogo.helpers.HelperFunctions
import com.android_hssn.dogo.helpers.TabEntity
import com.cocosw.bottomsheet.BottomSheet
import com.explore.pakistan.tourism.fragments.FavouritesFragment
import com.explore.pakistan.tourism.fragments.HomeFragment
import com.explore.pakistan.tourism.fragments.ScanFragment
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {


    private var fileName = System.currentTimeMillis().toString()
    private val SELECT_FILE = 200
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private var outputFileUri: Uri? = null
    lateinit var rootFolder: File


    private val mTitles = arrayOf("Home", "Scan", "Likes")
    private val mIconUnselectIds = intArrayOf(
        R.drawable.ic_home_grey, R.drawable.ic_camera_grey, R.drawable.ic_like_grey
    )
    private val mIconSelectIds =
        intArrayOf(R.drawable.ic_home_white, R.drawable.ic_camera_white, R.drawable.ic_like_white)
    private val mTabEntities = ArrayList<CustomTabEntity>()

    private var homeFragment: HomeFragment? = null
    private var scanFragment: ScanFragment? = null
    private var favouritesFragment: FavouritesFragment? = null
    lateinit var mContext: Context
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainContent.ivOpenMenu.setOnClickListener(this)
        mContext = this
        for (i in mTitles.indices) {
            mTabEntities.add(TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]))
        }
        binding.mainContent.tl1.setTabData(mTabEntities)
        setFragment()
        binding.mainContent.tl1.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                hideAllTabs()
                if (position == 0) {
                    binding.mainContent.fragment1.visibility = View.VISIBLE
                    binding.mainContent.title.setText("All Dogs")
                } else if (position == 1) {
                    binding.mainContent.fragment2.visibility = View.VISIBLE
                    binding.mainContent.title.setText("Scan Dogs")
                    fileName = System.currentTimeMillis().toString()
                    rootFolder = File(getExternalFilesDir("/Data/"), "images")
                    showPopupMenu()

                } else if (position == 2) {
                    binding.mainContent.title.setText("My Favourites")
                    binding.mainContent.fragment3.visibility = View.VISIBLE
                }
            }

            override fun onTabReselect(position: Int) {
                if (position == 1) {
                    binding.mainContent.fragment2.visibility = View.VISIBLE
                    fileName = System.currentTimeMillis().toString()
                    rootFolder = File(getExternalFilesDir("/Data/"), "images")
                    showPopupMenu()
                }
            }

        })
        binding.mainContent.tl1.currentTab = 0
        binding.mainContent.title.setText("All Dogs")
    }


    fun hideAllTabs() {
        binding.mainContent.fragment1.visibility = View.GONE
        binding.mainContent.fragment2.visibility = View.GONE
        binding.mainContent.fragment3.visibility = View.GONE
    }

    protected fun setFragment() {
        try {

            homeFragment = HomeFragment.newInstance() as HomeFragment
            scanFragment = ScanFragment.newInstance() as ScanFragment
            favouritesFragment = FavouritesFragment.newInstance() as FavouritesFragment

            binding.mainContent.fragment1.visibility = View.VISIBLE
            binding.mainContent.fragment2.visibility = View.GONE
            binding.mainContent.fragment3.visibility = View.GONE


            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.add(R.id.fragment1, homeFragment!!)
            ft.add(R.id.fragment2, scanFragment!!)
            ft.add(R.id.fragment3, favouritesFragment!!)
            ft.commitAllowingStateLoss()
        } catch (ex: IllegalStateException) {

        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.iv_open_menu -> {
                binding.drawerLayout.openDrawer(binding.drawerView)
            }

        }
    }


    /////////////////////////////////////////////Scan Fragmet settings


    fun showPopupMenu() {
        BottomSheet.Builder(this)
            .sheet(R.menu.select_id_options_bs_list)
            .listener { dialog, which ->
                when (which) {
                    R.id.camera -> OpenCamera()
                    R.id.library -> CheckPermissionUtil.checkWriteSd(
                        this@MainActivity
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
            this@MainActivity
        ) { success: Boolean ->
            if (success) {
                CheckPermissionUtil.checkCamera(
                    this@MainActivity
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
                scanFragment!!.setImage(outputFileUri!!)
                scanFragment!!.classifyLoadedImage(outputFileUri!!)
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


}
