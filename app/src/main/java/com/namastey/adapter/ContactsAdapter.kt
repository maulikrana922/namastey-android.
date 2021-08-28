package com.namastey.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnInviteClick
import com.namastey.model.Contact
import kotlinx.android.synthetic.main.row_contact_list.view.*

class ContactsAdapter(context: Context,var onInviteClick: OnInviteClick) : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {
    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var contactsCopy = ArrayList<Contact>()
    var contacts = ArrayList<Contact>()
        set(value) {
            field = value
            contactsCopy.clear()
            contactsCopy.addAll(contacts)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(layoutInflater.inflate(R.layout.row_contact_list, parent, false))
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = contacts[position]
        with(holder.itemView) {
            tvContactName.text = contact.name

            tvInvite.setOnClickListener{
                onInviteClick.onClickInvite(contact)
            }
        }
    }

    fun filter(text: String) {
        var text = text
        contacts.clear()
        if (text.isEmpty()) {
            contacts.addAll(contactsCopy)
        } else {
            text = text.toLowerCase()
            for (item in contactsCopy) {
                if (item.numbers.size != 0) {
                    if (item.name.toLowerCase().contains(text) || item.numbers[0].contains(text)) {
                        contacts.add(item)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}