package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.CountryAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentCountryBinding
import com.namastey.roomDB.entity.Country
import com.namastey.uiView.CountryView
import com.namastey.viewModel.CountryViewModel
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import kotlinx.android.synthetic.main.fragment_country.*
import javax.inject.Inject


class CountryFragment : BaseFragment<FragmentCountryBinding>(), CountryView,
    CountryAdapter.OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentCountryBinding: FragmentCountryBinding
    private lateinit var countryViewModel: CountryViewModel
    private lateinit var layoutView: View
    private lateinit var countryAdapter: CountryAdapter
    private var listOfCountry = ArrayList<Country>()
    private lateinit var onCountrySelectionListener: OnCountrySelectionListener

    override fun onClose() {
        fragmentManager!!.popBackStack()
    }

    override fun onSuccess(country: Country) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getViewModel() = countryViewModel

    override fun getLayoutId() = R.layout.fragment_country

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(listOfCountry: ArrayList<Country>) =
            CountryFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("listOfCountry", listOfCountry)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()
        initUI()
    }

    private fun initUI() {

        countryViewModel.getVideoLanguage()

        if (arguments!!.containsKey("listOfCountry")) {
            listOfCountry = arguments!!.getSerializable("listOfCountry") as ArrayList<Country>

            countryAdapter = CountryAdapter(listOfCountry, activity!!, this)
            rvCountry.adapter = countryAdapter

            fsvCountry.setupWithRecyclerView(
                rvCountry,
                { position ->
                    val item = listOfCountry[position]
                    FastScrollItemIndicator.Text(
                        item.name.substring(0, 1).toUpperCase()
                    )
                }
            )
            fastscroller_thumb.setupWithFastScroller(fsvCountry)
            onAttachToParentFragment(parentFragment)
//            parentFragment?.let { onAttachToParentFragment(it) }
        }
    }

    private fun setupViewModel() {
        countryViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            CountryViewModel::class.java
        )
        countryViewModel.setViewInterface(this)

        fragmentCountryBinding = getViewBinding()
        fragmentCountryBinding.viewModel = countryViewModel
    }


    override fun onCountryItemClick(country: Country) {
        if (onCountrySelectionListener != null)
        {
            onCountrySelectionListener.onCountrySelectionSet(country);
        }
    }

    interface OnCountrySelectionListener {
        fun onCountrySelectionSet(country: Country)
    }

    private fun onAttachToParentFragment(fragment: Fragment?) {
        try {
            onCountrySelectionListener = fragment as OnCountrySelectionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(fragment.toString().toString() + " must implement OnCountrySelectionListener")
        }
    }
}