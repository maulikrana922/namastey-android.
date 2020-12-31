package com.namastey.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnRecentLocationClick
import com.namastey.roomDB.entity.RecentLocations
import kotlinx.android.synthetic.main.row_current_location.view.*

class CurrentLocationAdapter(
    var activity: Activity,
    var locationBeanList: ArrayList<RecentLocations>,
    var onRecentLocationClick: OnRecentLocationClick
) : RecyclerView.Adapter<CurrentLocationAdapter.ViewHolder>() {
    private var checkedPosition = 0

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

            /*if (selectedLocationList.contains(locationBeanList[position].id)) {
                ivAddressSelected.visibility = View.VISIBLE
                ivCurrentLocation.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlueLight
                    )
                )
            }*/

            if (checkedPosition == -1) {
                ivAddressSelected.visibility = View.GONE
            } else {
                if (checkedPosition == adapterPosition) {
                    ivAddressSelected.visibility = View.VISIBLE
                    ivCurrentLocation.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.colorBlueLight
                        )
                    )
                } else {
                    ivAddressSelected.visibility = View.GONE
                    ivCurrentLocation.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.color_chip_gray
                        )
                    )
                }
            }

            llLocationView.setOnClickListener {
                ivAddressSelected.visibility = View.VISIBLE
                ivCurrentLocation.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlueLight
                    )
                )
                onRecentLocationClick.onRecentLocationItemClick(locationBean)
                if (checkedPosition != adapterPosition) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
            }

            /* llLocationView.setOnClickListener { v ->
                 if (ivAddressSelected.visibility == View.VISIBLE) {
                     ivAddressSelected.visibility = View.GONE
                     ivCurrentLocation.setColorFilter(
                         ContextCompat.getColor(
                             context,
                             R.color.color_chip_gray
                         )
                     )
                 } else {
                     ivAddressSelected.visibility = View.VISIBLE
                     ivCurrentLocation.setColorFilter(
                         ContextCompat.getColor(
                             context,
                             R.color.colorBlueLight
                         )
                     )
                 }
                 onItemClick.onLocationClick(locationBeanList[position])
             }*/
        }

    }

    fun getSelected(): RecentLocations? {
        return if (checkedPosition != -1) {
            locationBeanList[checkedPosition]
        } else null
    }
}