package com.namastey.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.namastey.R
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.CommentBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_comment.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CommentAdapter(
    var commentList: ArrayList<CommentBean>,
    var activity: Context,
    var onSelectUserItemClick: OnSelectUserItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_comment, parent, false
        )
    )

    override fun getItemCount() = commentList.size

    fun addCommentLastPosition(commentBean: CommentBean) {
        commentList.add(commentBean)
        notifyItemInserted(itemCount)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            val commentBean = commentList[position]
            tvUsername.text = commentBean.username
            tvComment.text = commentBean.comment
            tvComment.mentionColor = ContextCompat.getColor(context, R.color.colorBlueLight)
            GlideLib.loadImageUrlRoundCorner(activity, ivCommentUser, commentBean.profile_pic)

            val simpleDateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")

           /* try {
                //val date1 = simpleDateFormat.parse("10/10/2013 11:30:10")
                //  val date2 = simpleDateFormat.parse("13/10/2013 20:35:55")
                val date1 = simpleDateFormat.parse(commentBean.updated_at)
                val date2 = simpleDateFormat.parse(
                    SimpleDateFormat(
                        "yyyy-MM-dd hh:mm:ss",
                        Locale.getDefault()
                    ).format(Date())
                )
                Log.e("CommentAdapter", "date1: $date1")
                Log.e("CommentAdapter", "date2: $date2")
                printDifference(date1, date2)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            try {
                val dateString = commentBean.updated_at
                val sdf = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
                val date = sdf.parse(dateString)
                val startDate = date.time
                val timeNow = Date().time

                Log.e("CommentAdapter", "startDate: $startDate")
                Log.e("CommentAdapter", "timeNow: $timeNow")
                Log.e(
                    "CommentAdapter",
                    "convertLongDateToAgoString: ${convertLongDateToAgoString(startDate, timeNow)}"
                )
            } catch (e: ParseException) {
                e.printStackTrace()
            }*/

            Log.e("CommentAdapter", "covertTimeToText: ${covertTimeToText(commentBean.updated_at)}")

            tvDuration.text = covertTimeToText(commentBean.updated_at)

            holderComment.setOnClickListener {
                onSelectUserItemClick.onSelectItemClick(
                    commentBean.user_id,
                    position,
                    commentBean.user_profile_type
                )
            }
        }
    }

    fun printDifference(startDate: Date, endDate: Date) {
        //milliseconds
        var different = endDate.time - startDate.time
        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different = different % daysInMilli
        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli
        val elapsedSeconds = different / secondsInMilli
        System.out.printf(
            "%d days, %d hours, %d minutes, %d seconds%n",
            elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds
        )

        Log.e(
            "CommentAdapter",
            "Difference: $elapsedDays $elapsedHours $elapsedMinutes $elapsedSeconds"
        )

    }

    fun convertLongDateToAgoString(
        createdDate: Long,
        timeNow: Long
    ): String? {
        val timeElapsed = timeNow - createdDate
        Log.e("CommentAdapter", "timeElapsed: $timeElapsed")

        // For logging in Android for testing purposes
        /*
        Date dateCreatedFriendly = new Date(createdDate);
        Log.d("MicroR", "dateCreatedFriendly: " + dateCreatedFriendly.toString());
        Log.d("MicroR", "timeNow: " + timeNow.toString());
        Log.d("MicroR", "timeElapsed: " + timeElapsed.toString());*/

        // Lengths of respective time durations in Long format.
        val oneMin = 60000L
        val oneHour = 3600000L
        val oneDay = 86400000L
        val oneWeek = 604800000L
        var finalString = "0sec"
        val unit: String
        if (timeElapsed < oneMin) {
            // Convert milliseconds to seconds.
            var seconds = (timeElapsed / 1000).toDouble()
            seconds = Math.round(seconds).toDouble()
            unit = if (seconds == 1.0) {
                "sec"
            } else {
                "secs"
            }
            finalString = String.format("%.0f", seconds) + unit
        } else if (timeElapsed < oneHour) {
            var minutes = (timeElapsed / 1000 / 60).toDouble()
            minutes = Math.round(minutes).toDouble()
            unit = if (minutes == 1.0) {
                "min"
            } else {
                "mins"
            }
            finalString = String.format("%.0f", minutes) + unit
        } else if (timeElapsed < oneDay) {
            var hours = (timeElapsed / 1000 / 60 / 60).toDouble()
            hours = Math.round(hours).toDouble()
            unit = if (hours == 1.0) {
                "hr"
            } else {
                "hrs"
            }
            finalString = String.format("%.0f", hours) + unit
        } else if (timeElapsed < oneWeek) {
            var days = (timeElapsed / 1000 / 60 / 60 / 24).toDouble()
            days = Math.round(days).toDouble()
            unit = if (days == 1.0) {
                "day"
            } else {
                "days"
            }
            finalString = String.format("%.0f", days) + unit
        } else if (timeElapsed > oneWeek) {
            var weeks = (timeElapsed / 1000 / 60 / 60 / 24 / 7).toDouble()
            weeks = Math.round(weeks).toDouble()
            unit = if (weeks == 1.0) {
                "week"
            } else {
                "weeks"
            }
            finalString = String.format("%.0f", weeks) + unit
        }
        return finalString
    }

    fun covertTimeToText(dataDate: String?): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = ""
        try {
            val dateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
            val pasTime = dateFormat.parse(dataDate)
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
            when {
                second < 60 -> {
                    convTime = "$second sec $suffix"
                }
                minute < 60 -> {
                    convTime = "$minute min $suffix"
                }
                hour < 24 -> {
                    convTime = "$hour hr $suffix"
                }
                day >= 7 -> {
                    convTime = when {
                        day > 360 -> {
                            (day / 360).toString() + " y " + suffix
                        }
                        day > 30 -> {
                            (day / 30).toString() + " m " + suffix
                        }
                        else -> {
                            (day / 7).toString() + " w " + suffix
                        }
                    }
                }
                day < 7 -> {
                    convTime = "$day d $suffix"
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message!!)
        }
        return convTime
    }

}