package com.example.lab6_android_kotlin.ui.gallery

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.lab6_android_kotlin.databinding.FragmentGalleryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.math.BigDecimal
import java.math.RoundingMode

data class GoldRate(
    val data: String,
    val cena: Double
)

interface NbpApiService {
    @GET("cenyzlota")
    suspend fun getGoldPrice(): List<GoldRate>
}

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var et_zlotowki: EditText
    private lateinit var et_zloto: EditText

    private lateinit var nbpApiService: NbpApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        et_zloto = binding.etZloto
        et_zlotowki = binding.etZlotowki

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nbp.pl/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        nbpApiService = retrofit.create(NbpApiService::class.java)

        et_zloto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for this example
            }

            override fun afterTextChanged(s: Editable?) {
                // Trigger conversion when the text in et_zloto changes
                convertGoldToZlotowki()
            }
        })

        return root
    }

    private fun convertGoldToZlotowki() {
        val quantityOfGold = et_zloto.text.toString().toDoubleOrNull() ?: 0.0

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val result = getGoldPrice()
                val roundedResult = BigDecimal(result).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                val totalValueInZlotowki = quantityOfGold * roundedResult
                val value = BigDecimal(totalValueInZlotowki).setScale(2,RoundingMode.HALF_EVEN).toDouble()
                et_zlotowki.setText(value.toString())
            } catch (e: Exception) {
                Log.e("GalleryFragment", "Error in convertGoldToZlotowki", e)
                // Handle error, e.g., display an error message
                et_zlotowki.setText("Error fetching gold price")
            }
        }
    }

    private suspend fun getGoldPrice(): Double {
        val goldRates = nbpApiService.getGoldPrice()
        if (goldRates.isNotEmpty()) {
            val latestGoldRate = goldRates.first()
            return latestGoldRate.cena
        } else {
            // Handle empty response
            return 0.0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
