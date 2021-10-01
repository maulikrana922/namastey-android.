package com.namastey.viewModel

import android.R
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.namastey.application.NamasteyApplication
import com.namastey.model.Contact
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.InviteView
import com.namastey.utils.Constants
import kotlinx.coroutines.*


class InviteViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var inviteView: InviteView = baseView as InviteView
    private lateinit var job: Job
    private var numbers = ""
    private val _contactsLiveData = MutableLiveData<ArrayList<Contact>>()
    val contactsLiveData: LiveData<ArrayList<Contact>> = _contactsLiveData

    fun fetchContacts() {
        viewModelScope.launch {
            val contactsListAsync = async { getPhoneContacts() }
            val contactNumbersAsync = async { getContactNumbers() }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()

            contacts.forEach {
                contactNumbers[it.id]?.let { numbers ->
                    it.numbers = numbers
                }
            }
            _contactsLiveData.postValue(contacts)
        }
    }


/*    fun fetchContacts() {
            ContactService().setCallbacks(this)
        viewModelScope.launch {
            val contactsListAsync = async { ContactService().arrPhoneContactData }
            val contactNumbersAsync = async { getContactNumbers() }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()

            *//*contacts.forEach {
                contactNumbers[it.id]?.let { numbers ->
                    it.number = numbers
                }
            }*//*
            _contactsLiveData.postValue(contacts)
        }
    }*/


    private fun getAllContactsList(context: Context): ArrayList<Contact> {
        val arrPhoneContactData = ArrayList<Contact>()
        val cr = context.contentResolver
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cur != null && cur.count > 0) {
            while (cur.moveToNext()) {
                var name = ""
                var number = ""
                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val cur1Phone = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf<String>(id), null
                )
                val strNumber = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    arrayOf<String>(id), null
                )
                if (cur1Phone != null) {
                    while (cur1Phone.moveToNext()) {
                        //to get the contact name
                        name =
                            cur1Phone.getString(cur1Phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    }
                    cur1Phone.close()
                }
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur!!.moveToNext()) {
                        number = pCur!!.getString(
                            pCur!!.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        Log.i(TAG, "Name: " + R.attr.name)
                        Log.i(TAG, "Phone Number: $number")
                        arrPhoneContactData.add(Contact(id, number, name))
                    }
                    pCur!!.close()
                }
            }
            cur.close()
        }
        return arrPhoneContactData
    }

    private fun getPhoneContacts(): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val contactsCursor = NamasteyApplication.instance.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                val number = contactsCursor.getString(
                    contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
                if (numbers != name) {
                    numbers = name
                    if (name != null && number != null) {
                        contactsList.add(Contact(id, name, number))
                    }
                }
            }
            contactsCursor.close()
        }
        val hashSet: Set<Contact> = LinkedHashSet(contactsList)
        val removedDuplicates = ArrayList(hashSet)
        Log.e("ArraySize", contactsList.size.toString())
        Log.e("ArraySize", removedDuplicates.size.toString())
        return contactsList
    }

    fun <T> removeDuplicates(list: ArrayList<T>?): ArrayList<T>? {
        val set: Set<T> = LinkedHashSet(list)
        return ArrayList(set)
    }

    private fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        val contactsNumberMap = HashMap<String, ArrayList<String>>()
        val phoneCursor: Cursor? = NamasteyApplication.instance.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex =
                phoneCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                //check if the map contains key or not, if not then create a new array list with number
                if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(number)
                } else {
                    contactsNumberMap[contactId] = arrayListOf(number)
                }
            }
            //contact contains all the number of a particular contact
            phoneCursor.close()
        }
        return contactsNumberMap
    }

    fun sendInvitation(number: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToSendInvitation(number).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK) {
                        inviteView.onSuccess(appResponse.message)
                    }
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                inviteView.onHandleException(t)
            }
        }

    }


    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }

}