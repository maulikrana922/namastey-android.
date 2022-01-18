package com.namastey.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputFilter
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.ContactsAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityInviteBinding
import com.namastey.listeners.OnInviteClick
import com.namastey.model.Contact
import com.namastey.uiView.InviteView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.InviteViewModel
import kotlinx.android.synthetic.main.activity_invite.*
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.row_contact_list.*
import javax.inject.Inject

class InviteActivity : BaseActivity<ActivityInviteBinding>(), InviteView, OnInviteClick {

    private lateinit var inviteViewModel: InviteViewModel
    private lateinit var activityInviteBinding: ActivityInviteBinding;
    private lateinit var adapter: ContactsAdapter

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun getViewModel() = inviteViewModel

    override fun getLayoutId() = R.layout.activity_invite

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        inviteViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(InviteViewModel::class.java)
        activityInviteBinding = bindViewData()
        activityInviteBinding.viewModel = inviteViewModel

        initData()
    }

    private fun initData() {

        tvInviteRemaining.text = String.format(
            getString(R.string.invite_count),
            sessionManager.getIntegerValue(Constants.KEY_INVITE_COUNT)
        )
        val et: TextView = searchContact.findViewById(R.id.search_src_text) as TextView
        et.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15))
        adapter = ContactsAdapter(this, this)
        rvContact.adapter = adapter
        inviteViewModel.contactsLiveData.observe(this, Observer {
            rvContact.visibility = View.VISIBLE
            cvCenter.visibility = View.GONE
            adapter.contacts = it
        })

        searchContact.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    var numeric = true
                    numeric = newText.matches("-?\\d+(\\.\\d+)?".toRegex())
                    if (numeric) {
                        viewPhone.visibility = View.VISIBLE
                        tvContactName.text = "+91 ".plus(newText)
                    } else
                        viewPhone.visibility = View.GONE
                    adapter.filter(newText)
                } else {
                    viewPhone.visibility = View.GONE
                    if (ActivityCompat.checkSelfPermission(
                            this@InviteActivity,
                            android.Manifest.permission.READ_CONTACTS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        inviteViewModel.fetchContacts()
                    }
                }
                return true
            }

        })

        tvInvite.setOnClickListener {
            if (sessionManager.getIntegerValue(Constants.KEY_INVITE_COUNT) > 0) {
                val sendIntent = Intent(Intent.ACTION_VIEW)
                val number = "+91".plus(searchContact.query)
                sendIntent.data = Uri.parse("sms:")
                sendIntent.putExtra("address", number)
                val message = String.format(
                    getString(R.string.invite_sms_send),
                    "",
                    "+91".plus(searchContact.query)
                ).plus(" \n").plus(
                    getString(R.string.namastey_link)
                )

                inviteUser(number)
                sendIntent.putExtra("sms_body", message)
                startActivity(sendIntent)
            }else{
                object : CustomAlertDialog(
                    this@InviteActivity,
                    resources.getString(R.string.msg_out_of_invite),
                    getString(R.string.okay),
                    getString(R.string.cancel)
                ) {
                    override fun onBtnClick(id: Int) {
                        when (id) {
                            btnPos.id -> {
                                dismiss()
                            }
                            btnNeg.id -> {
                                dismiss()
                            }
                        }
                    }
                }.show()
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            inviteViewModel.fetchContacts()
        }
    }

    fun onClickSearchContacts(view: View) {
        if (isPermissionGrantedForContact()) {
            inviteViewModel.fetchContacts()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.PERMISSION_CONTACTS -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                inviteViewModel.fetchContacts()
            } else {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission
                        val permission: String = permissions[0]

                        val showRationale = shouldShowRequestPermissionRationale(permission)
                        if (!showRationale) {
                            val builder = AlertDialog.Builder(this)
                            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                                builder.setMessage(getString(R.string.permission_denied_contact_message))

                            builder.setPositiveButton(
                                getString(R.string.go_to_settings)
                            ) { dialog, id ->
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null)
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }

                            val dialog = builder.create()
                            dialog.setCanceledOnTouchOutside(false)
                            dialog.show()
                        }
                    } else {
                        if (isPermissionGrantedForContact()) {
                            inviteViewModel.fetchContacts()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        inviteViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickInviteBack(view: View) {
        onBackPressed()
    }

    override fun onClickInvite(contact: Contact) {

        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.data = Uri.parse("sms:${contact.number}")
        if (sessionManager.getIntegerValue(Constants.KEY_INVITE_COUNT) > 0) {
            if (contact.number.isNotEmpty()) {
                //sendIntent.putExtra("address", contact.number)
                var number = contact.number.replace("-", "")

                if (!number.startsWith("+"))        // Manually added +91 country code if number not saved with country code
                    number = "+91 ".plus(number)

                inviteUser(number)

                val message = String.format(
                    getString(R.string.invite_sms_send),
                    contact.name,
                    number.plus(" ")
                ).plus(" \n").plus(
                    getString(R.string.namastey_link)
                )
                sendIntent.putExtra("sms_body", message)
                startActivity(sendIntent)
            } else {
                Toast.makeText(
                    this@InviteActivity,
                    getString(R.string.msg_phone_number),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            object : CustomAlertDialog(
                this@InviteActivity,
                resources.getString(R.string.msg_out_of_invite),
                getString(R.string.okay),
                getString(R.string.cancel)
            ) {
                override fun onBtnClick(id: Int) {
                    when (id) {
                        btnPos.id -> {
                            dismiss()
                        }
                        btnNeg.id -> {
                            dismiss()
                        }
                    }
                }
            }.show()
        }
    }

    private fun inviteUser(number: String) {
        inviteViewModel.sendInvitation(number.filter { !it.isWhitespace() })
    }

    override fun onSuccess(msg: String) {
        val count = sessionManager.getIntegerValue(Constants.KEY_INVITE_COUNT)
        if (count > 0) {
            sessionManager.setIntegerValue(count - 1, Constants.KEY_INVITE_COUNT)
            tvInviteRemaining.text = String.format(
                getString(R.string.invite_count),
                sessionManager.getIntegerValue(Constants.KEY_INVITE_COUNT)
            )

        }
    }
}