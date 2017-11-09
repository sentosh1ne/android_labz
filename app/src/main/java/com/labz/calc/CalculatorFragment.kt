package com.labz.calc

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.labz.R
import com.labz.base.fragment.BaseFragment
import kotlinx.android.synthetic.main.layout_calc.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.text.DecimalFormat


/**
 * Created by Stanislav Vylegzhanin on 10.10.17.
 */
class CalculatorFragment : BaseFragment(), AnkoLogger {

    private var valueOne = Double.NaN
    private var valueTwo: Double = 0.0
    private lateinit var currentAction: CalcAction
    private lateinit var decimalFormat: DecimalFormat

    companion object {
        fun newInstance(): Fragment {
            val args = Bundle()
            val fragment = CalculatorFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_calculator, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        decimalFormat = DecimalFormat("#.##########")
        initEvents()
    }

    private fun initEvents() {
        info(containerCalc.childCount)
        initNumPad()

        btnAdd.onClick {
            setExpressionResult(CalcAction.ADDITION)
        }

        btnSubstract.onClick {
            setExpressionResult(CalcAction.SUBTRACTION)
        }

        btnMultiply.onClick {
            setExpressionResult(CalcAction.MULTIPLICATION)
        }

        btnDivide.onClick {
            setExpressionResult(CalcAction.DIVISION)
        }

        btnEquals.onClick {
            computeCalculation()
            txtResult.text = "${txtResult.text} ${decimalFormat.format(valueTwo)}=${decimalFormat.format(valueOne)}"
            valueOne = Double.NaN
            currentAction = CalcAction.EQUALS
        }

        buttonDot.onClick {
            txtCalculations.setText("${txtCalculations.text}.")
        }

        btnClear.onClick {
            if (txtCalculations.text.isNotEmpty()) {
                val currentText = txtCalculations.text
                txtCalculations.setText(currentText.subSequence(0, currentText.length - 1))
            } else {
                valueOne = Double.NaN
                valueTwo = Double.NaN
                txtCalculations.setText("")
                txtResult.text = ""
            }
        }
    }

    private fun setExpressionResult(calcAction: CalcAction) {
        computeCalculation()
        var sign = ""
        when (calcAction) {
            CalcAction.ADDITION -> sign = "+"
            CalcAction.SUBTRACTION -> sign = "-"
            CalcAction.MULTIPLICATION -> sign = "*"
            CalcAction.DIVISION -> sign = "/"
        }

        currentAction = calcAction
        txtResult.text = "${decimalFormat.format(valueOne)}$sign"
        txtCalculations.text = null
    }

    private fun computeCalculation() {
        info("Compute")
        if (!valueOne.isNaN()) {
            valueTwo = txtCalculations.text.toDouble()
            txtCalculations.text = null

            when (currentAction) {
                CalcAction.ADDITION -> valueOne += valueTwo
                CalcAction.SUBTRACTION -> valueOne -= valueTwo
                CalcAction.MULTIPLICATION -> valueOne *= valueTwo
                CalcAction.DIVISION -> valueOne /= valueTwo
            }
        } else {
            try {
                valueOne = txtCalculations.text.toDouble()
            } catch (e: ClassCastException) {

            }
        }
    }

    private fun initNumPad() {
        initNumPadSection(containerCalc)
        (0..containerCalc.childCount)
                .map { containerCalc.getChildAt(it) }
                .filter { it is ViewGroup }
                .map { it as ViewGroup }
                .forEach { initNumPadSection(it) }
    }

    private fun initNumPadSection(container: ViewGroup) {
        (0..container.childCount)
                .map { container.getChildAt(it) }
                .filter { it is Button }
                .map { it as Button }
                .forEach { setNumberClickListener(it) }
    }


    private fun setNumberClickListener(it: Button): Boolean {
        val btnText = it.text
        try {
            btnText.toString().toInt()
        } catch (e: NumberFormatException) {
            return true
        }

        if (btnText.toString().toInt() in (0..9)) {
            it.onClick {
                val newValue = txtCalculations.text.toString() + btnText
                txtCalculations.setText(newValue)
            }
        }
        return false
    }
}

enum class CalcAction {
    ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EQUALS
}

fun Editable.toDouble(): Double {
    return this.toString().toDouble()
}
