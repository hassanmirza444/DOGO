package com.android_hssn.dogo.activities

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.android_hssn.dogo.R
import com.android_hssn.dogo.database.repositories.DogCharacteristicsRepository
import com.android_hssn.dogo.databinding.ActivityDogDetailBinding
import com.android_hssn.dogo.managers.AdsManager
import com.android_hssn.dogo.models.DogCharacteristics
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DogDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDogDetailBinding
    lateinit var chartersitics: DogCharacteristics
    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dog_detail)
        mContext = this
        if (intent.hasExtra("data")) {
            chartersitics = intent.getSerializableExtra("data") as DogCharacteristics
        }
        setImageIntoImageView(chartersitics.name.toLowerCase().trim().replace(" ", "_"));
        binding.tvPhysicalCharacteristics.setText(
            chartersitics.physical.replace("2222 ", "\n\n")
        )
        binding.tvAddToFavourites.setOnClickListener {
            chartersitics.isFavourite = true
            CoroutineScope(Dispatchers.IO).launch {
                DogCharacteristicsRepository.getInstance()!!.updateDog(chartersitics)
            }
            binding.tvAddToFavourites.visibility = View.GONE
        }
        binding.tvGoBack.setOnClickListener {
            AdsManager.getInstance().showInterstitial(this)
            finish()
        }
        if (chartersitics.isFavourite) {
            binding.tvAddToFavourites.visibility = View.GONE
            binding.tvRemoveFromFavourites.visibility = View.VISIBLE
        } else {
            binding.tvAddToFavourites.visibility = View.VISIBLE
            binding.tvRemoveFromFavourites.visibility = View.GONE
        }


        AdsManager.getInstance().loadBanner(this, fl_banner)
    }

    fun setImageIntoImageView(imageName: String) {
        if (imageName.startsWith("a") || imageName.startsWith("b")) {
            val uri = "@drawable/$imageName"
            val imageResource: Int =
                mContext.getResources().getIdentifier(uri, null, mContext.getPackageName())
            val res: Drawable = mContext.getResources().getDrawable(imageResource)
            binding.ivDog.setImageDrawable(res)
        }
    }
}