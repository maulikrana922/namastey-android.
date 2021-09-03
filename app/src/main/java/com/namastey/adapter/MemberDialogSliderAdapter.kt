package com.namastey.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.namastey.R
import com.namastey.model.MembershipSlide
import kotlinx.android.synthetic.main.dialog_member.view.*
import kotlinx.android.synthetic.main.row_dialog_member.view.*
import java.util.ArrayList

class MemberDialogSliderAdapter(
    private val context: Context,
    private val membershipList: ArrayList<MembershipSlide>
) : PagerAdapter() {

    override fun getCount(): Int {
        return membershipList.size
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object`
    }

    override fun instantiateItem(
        container: ViewGroup,
        position: Int
    ): Any {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view =
            inflater.inflate(R.layout.row_dialog_member, null)
        view.tv_Text1.text = membershipList[position].title
        view.tv_Text2.text = membershipList[position].description
        view.tv_Text3.text = membershipList[position].count
        view.iv_slider_Icon.setImageResource(membershipList[position].profile_url)

        if(membershipList[position].count.equals("")){
            view.tv_Text3.visibility = View.GONE
        }else{
            view.tv_Text3.visibility = View.VISIBLE
        }

        val paint = view.tv_slider_title.paint
        val width = paint.measureText(view.tv_slider_title.text.toString())
        val textShader: Shader = LinearGradient(0f, 0f, width, view.tv_slider_title.textSize, intArrayOf(
            Color.parseColor("#D07AE2"),
            Color.parseColor("#F9245A")
        ), floatArrayOf(-2f, 3f), Shader.TileMode.REPEAT)
        view.tv_slider_title.paint.setShader(textShader)

//        val html = "<b>"+R.string.month_price_12+"</b>/mo"
//        view.tvTextHighEachBoost.text = Html.fromHtml(html)

        val viewPager = container as ViewPager

        viewPager.addView(view)
        return view
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }
}

