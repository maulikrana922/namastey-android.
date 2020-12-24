package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.Nullable
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.model.LatLngBounds
import com.namastey.R

class CustomPlaceAutoCompleteFragment : PlaceAutocompleteFragment() {
    private var editSearch: EditText? = null
    private var zzaRh: View? = null
    private var zzaRi: View? = null
    private val zzaRj: EditText? = null

    @Nullable
    private var zzaRk: LatLngBounds? = null

    @Nullable
    private var zzaRl: AutocompleteFilter? = null

    @Nullable
    private var zzaRm: PlaceSelectionListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle
    ): View? {
        val var4: View =
            inflater.inflate(R.layout.fragment_custom_place_auto_complete, container, false)
        editSearch =
            var4.findViewById<View>(R.id.place_autocomplete_search_input) as EditText
        editSearch!!.setOnClickListener { zzzG() }
        return var4
    }

    override fun onDestroyView() {
        zzaRh = null
        zzaRi = null
        editSearch = null
        super.onDestroyView()
    }

    override fun setBoundsBias(@Nullable bounds: LatLngBounds?) {
        zzaRk = bounds
    }

    override fun setFilter(@Nullable filter: AutocompleteFilter?) {
        zzaRl = filter
    }

    override fun setText(text: CharSequence) {
        editSearch!!.setText(text)
        //this.zzzF();
    }

    override fun setHint(hint: CharSequence) {
        editSearch!!.hint = hint
        zzaRh!!.contentDescription = hint
    }

    override fun setOnPlaceSelectedListener(listener: PlaceSelectionListener) {
        zzaRm = listener
    }

    private fun zzzF() {
        val var1 = !editSearch!!.text.toString().isEmpty()
        //this.zzaRi.setVisibility(var1?0:8);
    }

    private fun zzzG() {
        var var1 = -1
        try {
            val var2: Intent =
                PlaceAutocomplete.IntentBuilder(2)
                    .setBoundsBias(zzaRk)
                    .setFilter(zzaRl)
                    .zzg(editSearch!!.text.toString())
                    .zzd(1)
                    .build(this.activity)
            this.startActivityForResult(var2, 1)
        } catch (var3: GooglePlayServicesRepairableException) {
            var1 = var3.connectionStatusCode
            Log.e("Places", "Could not open autocomplete activity", var3)
        } catch (var4: GooglePlayServicesNotAvailableException) {
            var1 = var4.errorCode
            Log.e("Places", "Could not open autocomplete activity", var4)
        }
        if (var1 != -1) {
            val var5 = GoogleApiAvailability.getInstance()
            var5.showErrorDialogFragment(this.activity, var1, 2)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {
        if (requestCode == 1) {
            if (resultCode == -1) {
                val var4: Place = PlaceAutocomplete.getPlace(this.activity, data)
                if (zzaRm != null) {
                    zzaRm!!.onPlaceSelected(var4)
                }
                setText(var4.getName().toString())
            } else if (resultCode == 2) {
                val var5: Status = PlaceAutocomplete.getStatus(this.activity, data)
                if (zzaRm != null) {
                    zzaRm!!.onError(var5)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

/*@TargetApi(12)
@Deprecated("")
class CustomPlaceAutoCompleteFragment : PlaceAutocompleteFragment() {
    private var zzde: View? = null
    private var zzdf: View? = null
    private var zzdg: EditText? = null
    private var zzdh = false
    private var zzdi: LatLngBounds? = null
    private var zzdj: AutocompleteFilter? = null
    private var zzdk: PlaceSelectionListener? = null
    override fun onCreateView(
        var1: LayoutInflater,
        var2: ViewGroup?,
        var3: Bundle
    ): View? {
        val var4 = var1.inflate(R.layout.fragment_custom_place_auto_complete, var2, false)
        zzde = var4.findViewById(R.id.place_autocomplete_search_button)
        zzdf = var4.findViewById(R.id.place_autocomplete_clear_button)
        zzdg = var4.findViewById<View>(R.id.place_autocomplete_search_input) as EditText
        val var5 = zze(this)
        zzde.setOnClickListener(var5)
        zzdg!!.setOnClickListener(var5)
        zzdf.setOnClickListener(zzd(this))
        zzm()
        return var4
    }

    override fun onDestroyView() {
        zzde = null
        zzdf = null
        zzdg = null
        super.onDestroyView()
    }

    override fun setBoundsBias(var1: LatLngBounds?) {
        zzdi = var1
    }

    override fun setFilter(var1: AutocompleteFilter?) {
        zzdj = var1
    }

    override fun setText(var1: CharSequence?) {
        zzdg!!.setText(var1)
        zzm()
    }

    override fun setHint(var1: CharSequence?) {
        zzdg!!.hint = var1
        zzde!!.contentDescription = var1
    }

    override fun setOnPlaceSelectedListener(var1: PlaceSelectionListener?) {
        zzdk = var1
    }

    private fun zzm() {
        val var1 = !zzdg!!.text.toString().isEmpty()
        zzdf!!.visibility = if (var1) View.VISIBLE else View.GONE
    }

    private fun zzn() {
        var var1 = -1
        try {
            val var2 =
                PlaceAutocomplete.IntentBuilder(2).setBoundsBias(zzdi).setFilter(zzdj)
                    .zzg(zzdg!!.text.toString()).zzd(1).build(this.activity)
            zzdh = true
            this.startActivityForResult(var2, 30421)
        } catch (var3: GooglePlayServicesRepairableException) {
            var1 = var3.connectionStatusCode
            Log.e("Places", "Could not open autocomplete activity", var3)
        } catch (var4: GooglePlayServicesNotAvailableException) {
            var1 = var4.errorCode
            Log.e("Places", "Could not open autocomplete activity", var4)
        }
        if (var1 != -1) {
            GoogleApiAvailability.getInstance()
                .showErrorDialogFragment(this.activity, var1, 30422)
        }
    }

    override fun onActivityResult(
        var1: Int,
        var2: Int,
        var3: Intent
    ) {
        zzdh = false
        if (var1 == 30421) {
            if (var2 == -1) {
                val var4 =
                    PlaceAutocomplete.getPlace(this.activity, var3)
                if (zzdk != null) {
                    zzdk!!.onPlaceSelected(var4)
                }
                setText(var4.name.toString())
            } else if (var2 == 2) {
                val var5 =
                    PlaceAutocomplete.getStatus(this.activity, var3)
                if (zzdk != null) {
                    zzdk!!.onError(var5)
                }
            }
        }
        super.onActivityResult(var1, var2, var3)
    }
}*/
