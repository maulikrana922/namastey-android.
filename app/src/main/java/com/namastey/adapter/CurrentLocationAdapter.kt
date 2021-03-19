package com.namastey.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnRecentLocationClick
import com.namastey.roomDB.entity.RecentLocations
import kotlinx.android.synthetic.main.row_current_location.view.*
import java.util.*

class CurrentLocationAdapter(
    var activity: Activity,
    var locationBeanList: ArrayList<RecentLocations>,
    var onRecentLocationClick: OnRecentLocationClick
) : RecyclerView.Adapter<CurrentLocationAdapter.ViewHolder>() {
    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_current_location, parent, false
        )
    )

    override fun getItemCount() = locationBeanList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) = with(itemView) {
            val locationBean = locationBeanList[position]

            tvMyCurrentLocation.text = locationBean.knownName
                .plus(" ")
                .plus(locationBean.city)
                .plus(" ")
                .plus(locationBean.state)


            tvMyCurrentAddress.text = locationBean.country
                .plus(" ")
                .plus(locationBean.postalCode)

            Log.e("CurrentLocationAdapter", "isSelected: \t ${locationBean.isSelected}")

            if (locationBean.isSelected) {
                ivAddressSelected.visibility = View.VISIBLE
                ivCurrentLocation.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlueLight
                    )
                )
                locationBean.isSelected = false
            }

            if (position == checkedPosition) {
                locationBean.isSelected = true
                ivAddressSelected.visibility = View.VISIBLE
                ivCurrentLocation.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlueLight
                    )
                )
            } else {
                locationBean.isSelected = false
                ivAddressSelected.visibility = View.GONE
                ivCurrentLocation.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.color_chip_gray
                    )
                )
            }


            llLocationView.setOnClickListener {
                locationBean.isSelected = true
                ivAddressSelected.visibility = View.VISIBLE
                ivCurrentLocation.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlueLight
                    )
                )

                checkedPosition = adapterPosition
                notifyDataSetChanged()

                onRecentLocationClick.onRecentLocationItemClick(locationBean, locationBeanList)
            }
        }
    }
}