package com.example.lab6_android_kotlin.ui.home

import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.lab6_android_kotlin.databinding.FragmentHomeBinding
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode

class HomeFragment : Fragment() {

    private lateinit var etFirstConversion: EditText
    private lateinit var etSecondConversion: EditText
    private lateinit var spinnerFirstConversion: Spinner
    private lateinit var spinnerSecondConversion: Spinner

    private var currencyFrom = ""
    private var currencyTo = ""

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nbp.pl/api/exchangerates/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val nbpApi = retrofit.create(NbpApi::class.java)

    interface NbpApi {
        @GET("rates/A/{currency}/?format=json")
        fun getExchangeRate(@Path("currency") currency: String): Call<ExchangeRateResponse>
    }

    data class ExchangeRateResponse(
        @SerializedName("rates") val rates: List<Rates>
    )

    data class Rates(
        @SerializedName("mid") val mid: Double
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        etFirstConversion = binding.etFirstConversion
        etSecondConversion = binding.etSecondConversion
        spinnerFirstConversion = binding.spinnerFirstConversion
        spinnerSecondConversion = binding.spinnerSecondConversion

        val currencies = arrayOf("USD", "EUR", "GBP", "PLN","JPY")

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFirstConversion.adapter = adapter
        spinnerSecondConversion.adapter = adapter

        etFirstConversion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable?) {
                if (!editable.isNullOrBlank()) {
                    currencyFrom = spinnerFirstConversion.selectedItem.toString()
                    currencyTo = spinnerSecondConversion.selectedItem.toString()
                    CurrencyExchangeTask(this@HomeFragment, currencyFrom, currencyTo).execute()
                }
            }
        })

        spinnerFirstConversion.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                currencyFrom = spinnerFirstConversion.selectedItem.toString()
                currencyTo = spinnerSecondConversion.selectedItem.toString()
                CurrencyExchangeTask(this@HomeFragment, currencyFrom, currencyTo).execute()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        })

        spinnerSecondConversion.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                currencyFrom = spinnerFirstConversion.selectedItem.toString()
                currencyTo = spinnerSecondConversion.selectedItem.toString()
                CurrencyExchangeTask(this@HomeFragment, currencyFrom, currencyTo).execute()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class CurrencyExchangeTask(
        private val fragment: HomeFragment,
        private val currencyFrom: String,
        private val currencyTo: String
    ) : AsyncTask<Void, Void, Double>() {

        override fun doInBackground(vararg params: Void?): Double {
            return try {
                // Asynchronous network call for currencyFrom
                val responseFrom = fragment.nbpApi.getExchangeRate(currencyFrom).execute().body()
                val responseTo = fragment.nbpApi.getExchangeRate(currencyTo).execute().body()

                val rateFrom = responseFrom?.rates?.get(0)?.mid ?: if (currencyFrom == "PLN") 1.0 else 0.0
                val rateTo = responseTo?.rates?.get(0)?.mid ?: if (currencyTo == "PLN") 1.0 else 0.0

                rateFrom / rateTo
            } catch (e: Exception) {
                Log.e("CurrencyExchangeTask", "Error in doInBackground", e)
                0.0
            }
        }

        override fun onPostExecute(result: Double) {
            val inputValue = fragment.etFirstConversion.text.toString().toDoubleOrNull() ?: 0.0
            val convertedValue = inputValue * result
            val value = BigDecimal(convertedValue).setScale(2,RoundingMode.HALF_EVEN).toDouble()
            fragment.etSecondConversion.setText(value.toString())
        }
    }
}
