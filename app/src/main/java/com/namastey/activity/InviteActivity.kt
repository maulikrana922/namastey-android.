package com.namastey.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.SearchView
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
import com.namastey.uiView.InviteView
import com.namastey.utils.Constants
import com.namastey.viewModel.InviteViewModel
import kotlinx.android.synthetic.main.activity_invite.*
import javax.inject.Inject

class InviteActivity : BaseActivity<ActivityInviteBinding>(), InviteView {

    private lateinit var inviteViewModel: InviteViewModel
    private lateinit var activityInviteBinding: ActivityInviteBinding;
    private lateinit var adapter: ContactsAdapter

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

        adapter = ContactsAdapter(this)
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
                    adapter.filter(newText)
                } else {
                    inviteViewModel.fetchContacts()
                }
                return true
            }

        })
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
}