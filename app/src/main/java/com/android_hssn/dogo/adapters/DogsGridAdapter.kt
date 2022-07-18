package com.android_hssn.dogo.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android_hssn.dogo.R
import com.android_hssn.dogo.activities.DogDetailActivity
import com.android_hssn.dogo.models.DogCharacteristics


class DogsGridAdapter(
    c: Context, list: ArrayList<DogCharacteristics?>
) : RecyclerView.Adapter<BaseViewHolder>() {


    var mContext: Context
    private var mlist: ArrayList<DogCharacteristics?>? = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return DogCharacteristicsViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.dogs_grid_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.itemView.setOnClickListener {
            mContext.startActivity(Intent(mContext, DogDetailActivity::class.java).apply {
                putExtra("data", mlist!![position])
            })
        }
        holder.onBind(position)

    }

    override fun getItemCount(): Int {
        return mlist?.size ?: 0
    }

    fun clearAll() {
        mlist!!.clear()
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        holder.setIsRecyclable(false)
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        (holder as? DogCharacteristicsViewHolder)?.setIsRecyclable(false)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        (holder as? DogCharacteristicsViewHolder)?.setIsRecyclable(true)
        super.onViewDetachedFromWindow(holder)
    }

    inner class DogCharacteristicsViewHolder internal

    constructor(var parent: View) : BaseViewHolder(parent) {

        var image: ImageView
        var title: TextView
        var layout: CardView

        override fun clear() {}

        override fun onBind(position: Int) {
            super.onBind(position)
            var item = mlist!!.get(position)
            title.setText(item!!.name)
            setImageIntoImageView(item.name.toLowerCase().trim().replace(" ", "_"));

            layout.setOnClickListener {
                mContext.startActivity(Intent(mContext, DogDetailActivity::class.java).apply {
                    putExtra("data", mlist!![position])
                })
            }
            when (position % 5) {
                0 -> {
                    title.setBackgroundColor(mContext.resources.getColor(R.color.color1))
                }
                1 -> {
                    title.setBackgroundColor(mContext.resources.getColor(R.color.color2))
                }
                2 -> {
                    title.setBackgroundColor(mContext.resources.getColor(R.color.color3))
                }
                3 -> {
                    title.setBackgroundColor(mContext.resources.getColor(R.color.color4))
                }
                4 -> {
                    title.setBackgroundColor(mContext.resources.getColor(R.color.color5))
                }
            }


        }

        fun setImageIntoImageView(imageName: String) {
            if (imageName.startsWith("a") || imageName.startsWith("b")) {
                val uri = "@drawable/$imageName"
                val imageResource: Int =
                    mContext.getResources().getIdentifier(uri, null, mContext.getPackageName())
                val res: Drawable = mContext.getResources().getDrawable(imageResource)
                image.setImageDrawable(res)
            }
        }

        init {

            image = parent.findViewById(R.id.iv_item)
            title = parent.findViewById(R.id.tv_item_name)
            layout = parent.findViewById(R.id.cardview)

        }
    }

    init {
        mlist = list
        mContext = c
    }

}