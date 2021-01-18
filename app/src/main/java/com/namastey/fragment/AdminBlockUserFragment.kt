package com.namastey.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.namastey.R

open class AdminBlockUserFragment : Fragment() {

    private lateinit var layoutView: View

    companion object {
        fun getInstance() =
            AdminBlockUserFragment().apply {
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutView = inflater.inflate(R.layout.fragment_admin_block_user, container, false)
        val tvProfileBan = layoutView.findViewById<TextView>(R.id.tvProfileBan)
          tvProfileBan.linksClickable = true
          tvProfileBan.movementMethod = LinkMovementMethod.getInstance()
        return layoutView
    }

}