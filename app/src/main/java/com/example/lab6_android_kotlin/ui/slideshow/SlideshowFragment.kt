package com.example.lab6_android_kotlin.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lab6_android_kotlin.databinding.FragmentSlideshowBinding
import net.objecthunter.exp4j.ExpressionBuilder

class SlideshowFragment : Fragment() {

    private lateinit var bc: Button
    private lateinit var bb1: Button
    private lateinit var bb2: Button
    private lateinit var bdevide: Button
    private lateinit var b7: Button
    private lateinit var b8: Button
    private lateinit var b9: Button
    private lateinit var bmult: Button
    private lateinit var b4: Button
    private lateinit var b5: Button
    private lateinit var b6: Button
    private lateinit var bplus: Button
    private lateinit var b1: Button
    private lateinit var b2: Button
    private lateinit var b3: Button
    private lateinit var bminus: Button
    private lateinit var bac: Button
    private lateinit var b0: Button
    private lateinit var bdot: Button
    private lateinit var beq: Button



    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var currentInput: StringBuilder = StringBuilder()
    private lateinit var solutionTextView: TextView
    private lateinit var resultTextView: TextView

    private var isResultDisplayed = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        solutionTextView = binding.solutionTv
        resultTextView = binding.resultTv

        bc = binding.buttonC
        bb1 = binding.buttonOpenBracket
        bb2 = binding.buttonCloseBracket
        bdevide = binding.buttonDivide
        b7 = binding.button7
        b8 = binding.button8
        b9 = binding.button9
        bmult = binding.buttonMultiply
        b4 = binding.button4
        b5 = binding.button5
        b6 = binding.button6
        bplus = binding.buttonPlus
        b1 = binding.button1
        b2 = binding.button2
        b3 = binding.button3
        bminus = binding.buttonMinus
        bac = binding.buttonAc
        b0 = binding.button0
        bdot = binding.buttonDot
        beq =  binding.buttonEquals

        setButtonClickListener(bc, "C")
        setButtonClickListener(bb1, "(")
        setButtonClickListener(bb2, ")")
        setButtonClickListener(bdevide, "/")

        setButtonClickListener(b7, "7")
        setButtonClickListener(b8, "8")
        setButtonClickListener(b9, "9")
        setButtonClickListener(bmult, "*")

        setButtonClickListener(b4, "4")
        setButtonClickListener(b5, "5")
        setButtonClickListener(b6, "6")
        setButtonClickListener(bplus, "+")

        setButtonClickListener(b1, "1")
        setButtonClickListener(b2, "2")
        setButtonClickListener(b3, "3")
        setButtonClickListener(bminus, "-")

        setButtonClickListener(bac, "AC")
        setButtonClickListener(b0, "0")
        setButtonClickListener(bdot, ".")
        setButtonClickListener(beq, "=")

        return root
    }

    private fun setButtonClickListener(button: Button, value: String) {
        button.setOnClickListener {
            handleButtonClick(value)
        }
    }

    private fun handleButtonClick(value: String) {
        when (value) {
            "=" -> calculateResult()
            "C" -> clearInput()
            "AC" -> clearAll()
            else -> appendInput(value)
        }
    }

    private fun clearInput() {
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            updateResultView()
        }
    }

    private fun appendInput(value: String) {
        if (isResultDisplayed) {
            if (value=="(" || value==")" || value=="/" || value=="*" || value=="+" || value=="-")
            {
                isResultDisplayed = false
            }
            else{
                currentInput.clear()
                isResultDisplayed = false
            }
        }
        currentInput.append(value)
        updateResultView()
    }

    private fun clearAll() {
        currentInput.clear()
        isResultDisplayed = false
        updateResultView()
    }

    private fun calculateResult() {
        try {
            val expression = ExpressionBuilder(currentInput.toString()).build()
            val result = expression.evaluate()
            currentInput.clear()
            currentInput.append(result)
            isResultDisplayed = true
            updateResultView()
        } catch (e: Exception) {
            // Obsługa błędów obliczeń
            currentInput.clear()
            currentInput.append("Error")
            updateResultView()
        }
    }

    private fun updateResultView() {
        resultTextView.text = currentInput.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}