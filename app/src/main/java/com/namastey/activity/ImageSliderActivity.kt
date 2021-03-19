package com.namastey.activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import com.namastey.R
import com.namastey.adapter.ImageSliderAdapter
import com.namastey.databinding.ActivityImageSliderBinding
import com.namastey.utils.Utils
import com.namastey.viewModel.BaseViewModel
import kotlinx.android.synthetic.main.activity_image_slider.*
import java.util.*

class ImageSliderActivity : BaseActivity<ActivityImageSliderBinding>() {


    private var imageUrlList = ArrayList<Uri>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
        setContentView(R.layout.activity_image_slider)

        initData()
    }

    private fun initData() {

        if (intent.hasExtra("imageUri")){
            val imageUri: Uri = intent.getParcelableExtra<Uri>("imageUri")!!
            imageUrlList.add(imageUri)
        }else {
            val data = intent.clipData
            if (data != null) {
                val count = data.itemCount
                for (i in 0 until count) {
                    val imageUri: Uri = data.getItemAt(i)!!.uri
                    imageUrlList.add(imageUri)
                }
            }
        }
        viewpagerImage.adapter = ImageSliderAdapter(this@ImageSliderActivity, imageUrlList)
        inflateThumbnails()
    }

    private fun inflateThumbnails() {
        for (i in 0 until imageUrlList.size) {
            val imageLayout: View = layoutInflater.inflate(R.layout.row_image_slider_thubnail, null)
            val imageView: ImageView = imageLayout.findViewById(R.id.img_thumb) as ImageView
            imageView.setOnClickListener(onChagePageClickListener(i))
            val options = BitmapFactory.Options()
            options.inSampleSize = 3
//            options.inDither = false

            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                Utils.scaleBitmapDown(
                    MediaStore.Images.Media.getBitmap(contentResolver, imageUrlList[i]),
                    1200
                )!!
            } else {
                val source = ImageDecoder.createSource(this.contentResolver, imageUrlList[i])
                ImageDecoder.decodeBitmap(source)
            }

            imageView.setImageBitmap(bitmap)
            //set to image view
            imageView.setImageBitmap(bitmap)
            //add imageview
            container.addView(imageLayout)
        }
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun onChagePageClickListener(i: Int): View.OnClickListener? {
        return View.OnClickListener { viewpagerImage.currentItem = i }
    }

    override fun getViewModel(): BaseViewModel {
        TODO("Not yet implemented")
    }

    override fun getLayoutId(): Int {
        TODO("Not yet implemented")
    }

    override fun getBindingVariable(): Int {
        TODO("Not yet implemented")
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    fun onClickSend(view: View) {
        val intent = Intent()
        if (getIntent().hasExtra("imageUri"))
            intent.putExtra("imageUri", getIntent().getParcelableExtra<Uri>("imageUri"))
        else
            intent.clipData = getIntent().clipData

        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }
}