package com.namastey.adapter

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.namastey.R
import com.namastey.customViews.CustomTextView
import com.namastey.model.MembershipBean
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import java.util.*
import java.util.concurrent.TimeUnit

class SliderAdapter(
    private val context: Context,
    private val membershipList: ArrayList<MembershipBean>
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
            inflater.inflate(R.layout.row_membership_message, null)

        val tvMemberTitle =
            view.findViewById<View>(R.id.tvMemberTitle) as CustomTextView
        val tvMemberDesc = view.findViewById<View>(R.id.tvMemberDesc) as CustomTextView

        tvMemberTitle.text = membershipList[position].name
        tvMemberDesc.text = membershipList[position].description

        //todo: Start timer According to condition
        if (SessionManager(context).getBooleanValue(Constants.KEY_MAX_USER_LIKE)) {
            if (position == 1) {
                val c = Calendar.getInstance()
                c.add(Calendar.DAY_OF_MONTH, 1)
                c[Calendar.HOUR_OF_DAY] = 0
                c[Calendar.MINUTE] = 0
                c[Calendar.SECOND] = 0
                c[Calendar.MILLISECOND] = 0
                val millis = c.timeInMillis - System.currentTimeMillis()
                val interval = 1000L

                val t: CountDownTimer
                t = object : CountDownTimer(millis, interval) {
                    override fun onTick(millisUntilFinished: Long) {
                        val timer = String.format(
                            "%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(
                                    millisUntilFinished
                                )
                            ), // The change is in this line
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                        )
                        tvMemberDesc.text = timer.plus(membershipList[position].description)
                    }

                    override fun onFinish() {
                        cancel()
                    }
                }.start()

            }
        } else {
            tvMemberDesc.text = membershipList[position].description
        }

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