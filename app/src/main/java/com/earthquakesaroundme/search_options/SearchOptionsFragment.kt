package com.earthquakesaroundme.search_options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.text.isDigitsOnly
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
import androidx.lifecycle.Observer


class SearchOptionsFragment: Fragment() {

    private lateinit var interstitialAd: InterstitialAd
    private lateinit var binding: FragmentSearchOptionsBinding
    private lateinit var viewModel: SearchOptionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

//        initInterstitialAd()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_options,
            container, false)
        binding.lifecycleOwner = this

        val viewModelFactory = SearchOptionsViewModelFactory()

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SearchOptionsViewModel::class.java)


        initNumberPickers()

        showInputFields()

        initAboutTextView()

        binding.buttonSearch.setOnClickListener { view ->
            val searchOptions = getSearchOptions(binding)
            if (inputDataIsCorrect(searchOptions.maxRadiusKm,
                    searchOptions.startTime,
                    searchOptions.endTime)) {
//                if (interstitialAd.isLoaded) {
//                    interstitialAd.show()
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

    private fun initAboutTextView() {
        binding.aboutOptionsFragment.maxLines = 7
        binding.textViewReadMore.setOnClickListener {
            viewModel.setExpandDescriptionText(viewModel.expandDescriptionText.value!!.not())
        }
        viewModel.expandDescriptionText.observe(this, Observer { shouldExpand ->
            if (shouldExpand) {
                binding.aboutOptionsFragment.maxLines = 100
                binding.textViewReadMore.text = getString(R.string.search_options_read_less)
            } else {
                binding.aboutOptionsFragment.maxLines = 7
                binding.textViewReadMore.text = getString(R.string.search_options_read_more)
            }
        })
    }

    private fun initNumberPickers() {
        //Initializing the number pickers
        binding.numberPickerMinMagnitude.initializeNumberPicker()
        binding.numberPickerMaxMagnitude.initializeNumberPicker()
        //If a previous value of the number pickers is saved, then we should use it
        if (viewModel.minMag.value != null) {
            binding.numberPickerMinMagnitude.value = viewModel.minMag.value!!
        }
        if (viewModel.maxMag.value != null) {
            binding.numberPickerMaxMagnitude.value = viewModel.maxMag.value!!
        }
    }

    private fun initInterstitialAd() {
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
    }

    private fun NumberPicker.initializeNumberPicker() {
        minValue = SearchOptionsUtils.EARTHQUAKE_MAGNITUDE_RANGE.MIN_MAGNITUDE
        maxValue = SearchOptionsUtils.EARTHQUAKE_MAGNITUDE_RANGE.MAX_MAGNITUDE
        wrapSelectorWheel = true
    }

    /*
    We get the value of a search parameter only if the CheckBox assigned to that parameter
    is checked
     */
    private fun getSearchOptions(
        binding: FragmentSearchOptionsBinding) : SearchOptions {

        val minMag = if (binding.checkBoxMinMag.isChecked)
            binding.numberPickerMinMagnitude.value else null
        val maxMag = if (binding.checkBoxMaxMag.isChecked)
            binding.numberPickerMaxMagnitude.value else null
        //We save the values of the number pickers so we can use it when the fragment is restored
        //from the back stack
        viewModel.setMaxMag(maxMag)
        viewModel.setMinMag(minMag)
        val orderBy = if (binding.checkBoxOrderBy.isChecked)
            getOrderBySearchOption(binding) else null
        val maxRadiusKm = if (binding.checkBoxMaxRadiusKm.isChecked)
            binding.editTextMaxRadiusKm.text.toString().toIntOrNull() else null
        val startTime = if (binding.checkBoxStartDate.isChecked)
            binding.editTextStartDate.text.toString().trim() else null
        val endTime = if (binding.checkBoxEndDate.isChecked)
            binding.editTextEndDate.text.toString().trim() else null
        return SearchOptions(minMag, maxMag, orderBy, maxRadiusKm, startTime, endTime)

    }

    /*
    Returns the value of the orderBy parameter depending on the
    selected option from the radio group
     */
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
            //The end date must also be after the start date
            val endDate = toDate(endTime)
            val startDate = if (startTime != null) toDate(startTime) else
                Date(Date().time - 30L * 24 * 3600 * 1000) //by default is set to NOW - 30 days
            if (!endDate.after(startDate)) {
                Toast.makeText(this.context, getString(R.string.end_date_after_start_date), Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    /*
    The string date is valid if we don't get an exception
    when we try to create a data object with it
     */
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
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                isLenient = false
            }.parse(dateString)
        } catch (e: Exception) {
            throw IllegalArgumentException()
        }
    }

    /*
    The input field for a parameter will appear only if the user wants to set that
    specific search parameter
     */
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

    /*
    If the corresponding checkBox is checked, then we show the View representing the input field
     */
    private fun setViewVisibility(checkBox: CheckBox, setValue: (Boolean) -> Unit,
                          isCheckBoxChecked: LiveData<Boolean>, viewToMakeVisible: View) {
        setClickListener(checkBox, setValue)
        isCheckBoxChecked.observe(this, androidx.lifecycle.Observer {
                isChecked -> viewToMakeVisible.visibility = if (isChecked) View.VISIBLE else View.GONE
        })
    }
    /*
    We save the value that says whether a CheckBox is checked or not so we can show (or not) the
    input field when the fragment is restored from the back-stack
     */
    private fun setClickListener(checkBox: CheckBox, setValue: (Boolean) -> Unit) {
        checkBox.setOnClickListener {
            setValue(checkBox.isChecked)
        }
    }

}