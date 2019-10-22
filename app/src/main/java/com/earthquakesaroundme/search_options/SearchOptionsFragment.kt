package com.earthquakesaroundme.search_options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.earthquakesaroundme.R
import com.earthquakesaroundme.databinding.FragmentSearchOptionsBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import java.text.SimpleDateFormat
import java.util.*


class SearchOptionsFragment: Fragment() {

    private lateinit var interstitialAd: InterstitialAd
    private lateinit var binding: FragmentSearchOptionsBinding
    private lateinit var viewModel: SearchOptionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application

        interstitialAd = InterstitialAd(context!!)
        interstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        if (!interstitialAd.isLoaded) {
            interstitialAd.loadAd(AdRequest.Builder().build())
        }
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        binding = DataBindingUtil.inflate<FragmentSearchOptionsBinding>(inflater,
            R.layout.fragment_search_options, container, false)

        val viewModelFactory = SearchOptionsViewModelFactory(application)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SearchOptionsViewModel::class.java)

        /*
        The search options for a parameter will appear only if the user wants to set that
        specific search parameter
         */
        showInputFields()

        //Initializing the number pickers
        binding.numberPickerMinMagnitude.initializeNumberPicker()
        binding.numberPickerMaxMagnitude.initializeNumberPicker()
        if (viewModel.minMag.value != null) {
            binding.numberPickerMinMagnitude.value = viewModel.minMag.value!!
        }
        if (viewModel.maxMag.value != null) {
            binding.numberPickerMaxMagnitude.value = viewModel.maxMag.value!!
        }




        binding.buttonSearch.setOnClickListener { view ->
            val searchOptions = getSearchOptions(binding)
            if (inputDataIsCorrect(searchOptions.maxRadiusKm,
                    searchOptions.startTime,
                    searchOptions.endTime)) {
//                if (interstitialAd.isLoaded) {
//                    interstitialAd.show()
//                } else {
//                    Log.d("onCreateView", "Ad was not loaded.")
//                }
                view.findNavController()
                    .navigate(
                        SearchOptionsFragmentDirections
                            .actionSearchOptionsFragmentToOverviewFragment(searchOptions)
                    )
            }
        }


        return binding.root
    }

    private fun NumberPicker.initializeNumberPicker() {
        minValue = SearchOptionsUtils.EARTHQUAKE_MAGNITUDE_RANGE.MIN_MAGNITUDE
        maxValue = SearchOptionsUtils.EARTHQUAKE_MAGNITUDE_RANGE.MAX_MAGNITUDE
        wrapSelectorWheel = true
    }

    private fun getSearchOptions(
        binding: FragmentSearchOptionsBinding) : SearchOptions {

        val minMag = if (binding.checkBoxMinMag.isChecked)
            binding.numberPickerMinMagnitude.value else null
        viewModel.setMinMag(minMag)
        val maxMag = if (binding.checkBoxMaxMag.isChecked)
            binding.numberPickerMaxMagnitude.value else null
        viewModel.setMaxMag(maxMag)
        val orderBy = if (binding.checkBoxOrderBy.isChecked)
            getOrderBySearchOption(binding) else null
        val maxRadiusKm = if (binding.checkBoxMaxRadiusKm.isChecked)
            Integer.parseInt(binding.editTextMaxRadiusKm.text.toString()) else null
        val startTime = if (binding.checkBoxStartDate.isChecked)
            binding.editTextStartDate.text.toString().trim() else null
        val endTime = if (binding.checkBoxEndDate.isChecked)
            binding.editTextEndDate.text.toString().trim() else null
        return SearchOptions(minMag, maxMag, orderBy, maxRadiusKm, startTime, endTime)

    }

    private fun getOrderBySearchOption(binding: FragmentSearchOptionsBinding) =
        when(binding.orderBySearchOptionsRadioGroup.checkedRadioButtonId) {
            binding.timeAscRadioButton.id -> SearchOptionsUtils.UsgsApiOrderByValues.TIME_ASC
            binding.magDescRadioButton.id -> SearchOptionsUtils.UsgsApiOrderByValues.MAG
            binding.magAscRadioButton.id -> SearchOptionsUtils.UsgsApiOrderByValues.MAG_ASC
            else -> null
        }


    private fun inputDataIsCorrect(maxRadiusKm: Int?, startTime: String?, endTime: String?): Boolean {
        if (maxRadiusKm != null) {
            if (maxRadiusKm !in 0..20_000) {
                Toast.makeText(this.context, getString(R.string.radius_incorrect), Toast.LENGTH_SHORT).show()
                return false
            }
        }
        if (startTime != null) {
            if (!isDateCorrect(startTime)) {
                Toast.makeText(this.context, getString(R.string.date_invalid, "Start"),
                    Toast.LENGTH_SHORT).show()
                return false
            }
        }
        if (endTime != null) {
            if (!isDateCorrect(endTime)) {
                Toast.makeText(this.context, getString(R.string.date_invalid, "End"),
                    Toast.LENGTH_SHORT).show()
                return false
            }
            val endDate = toDate(endTime)
            val startDate = if (startTime != null) toDate(startTime) else
                Date(Date().time - 30L * 24 * 3600 * 1000)
            if (!endDate.after(startDate)) {
                Toast.makeText(this.context, getString(R.string.end_date_after_start_date), Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun isDateCorrect(dateString: String): Boolean {

        return try {
            toDate(dateString)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun toDate(dateString: String) : Date {
        try {
            return SimpleDateFormat("yyyy-MM-dd").apply {
                isLenient = false
            }.parse(dateString)
        } catch (e: Exception) {
            throw IllegalArgumentException()
        }
    }

    private fun showInputFields() {
        setViewVisibility(binding.checkBoxMinMag, viewModel.setIsCheckBoxMinMagChecked,
            viewModel.isCheckBoxMinMagChecked, binding.numberPickerMinMagnitude)
        setViewVisibility(binding.checkBoxMaxMag, viewModel.setIsCheckBoxMaxMagChecked,
            viewModel.isCheckBoxMaxMagChecked, binding.numberPickerMaxMagnitude)
        setViewVisibility(binding.checkBoxOrderBy, viewModel.setIsCheckBoxOrderByChecked,
            viewModel.isCheckBoxOrderByChecked, binding.orderBySearchOptionsRadioGroup)
        setViewVisibility(binding.checkBoxMaxRadiusKm, viewModel.setIsCheckBoxRadiusChecked,
            viewModel.isCheckBoxRadiusChecked, binding.editTextMaxRadiusKm)
        setViewVisibility(binding.checkBoxStartDate, viewModel.setIsCheckBoxStartDateChecked,
            viewModel.isCheckBoxStartDateChecked, binding.editTextStartDate)
        setViewVisibility(binding.checkBoxEndDate, viewModel.setIsCheckBoxEndDateChecked,
            viewModel.isCheckBoxEndDateChecked, binding.editTextEndDate)

    }

    fun setViewVisibility(checkBox: CheckBox, setValue: (Boolean) -> Unit,
                          isCheckBoxChecked: LiveData<Boolean>, viewToMakeVisible: View) {
        setClickListener(checkBox, setValue)
        isCheckBoxChecked.observe(this, androidx.lifecycle.Observer {
                isChecked -> viewToMakeVisible.visibility = if (isChecked) View.VISIBLE else View.GONE
        })
    }

    private fun setClickListener(checkBox: CheckBox, setValue: (Boolean) -> Unit) {
        checkBox.setOnClickListener {
            setValue(checkBox.isChecked)
        }
    }

}