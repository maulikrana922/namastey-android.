package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.roomDB.entity.Country
import kotlinx.android.synthetic.main.row_country.view.*

class CountryAdapter(
    var listOfCountry: ArrayList<Country>,
    var activity: Context,
    var onItemClick: OnItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_country, parent, false
        )
    )

    override fun getItemCount() = listOfCountry.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvCountryName.text = listOfCountry.get(position).name
            tvCountryCode.text = "+" + listOfCountry.get(position).phonecode.toString()

            countryMain.setOnClickListener { v ->
                onItemClick.onCountryItemClick(listOfCountry.get(position))
            }
        }

    }

    interface OnItemClick {
        fun onCountryItemClick(country: Country)
    }
}