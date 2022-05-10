package com.example.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minuteworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BmiActivity : AppCompatActivity() {
    private lateinit var bindinging: ActivityBmiBinding

    val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
    val US_UNITS_VIEW = "US_UNIT_VIEW"

    var currentVisibleView : String = METRIC_UNITS_VIEW


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindinging = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(bindinging.root)

        setSupportActionBar(bindinging.toolbarBmiActivity)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "CALCULATE BMI"
        }
        bindinging.toolbarBmiActivity.setNavigationOnClickListener {
            onBackPressed()
        }

        bindinging.btnCalculateUnits.setOnClickListener {
            if(validateMetric() && currentVisibleView == METRIC_UNITS_VIEW){
                val height : Float = bindinging.etHeight.text.toString().toFloat()/100
                val weight : Float = bindinging.etWeight.text.toString().toFloat()

                val bmi = weight/(height * height)
                displayBMIResults(bmi)
            }else if (validateUS() && currentVisibleView == US_UNITS_VIEW){
                var weight : Float = bindinging.etUsWeight.text.toString().toFloat()
                val feet : Float = bindinging.etUsFeet.text.toString().toFloat()
                val inches : Float = bindinging.etUsInches.text.toString().toFloat()
                val height : Float = (feet*12 + inches)
                val bmi : Float = 703*(weight/(height*height))
                displayBMIResults(bmi)
            }

            else{
                Toast.makeText(this@BmiActivity,"Please enter Valid Values",Toast.LENGTH_SHORT).show()
            }
        }
        makeVisibleMetricUnitView()
        bindinging.rgContainer.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.rb_metric_normal){
                makeVisibleMetricUnitView()
            }else{
                makeVisibleUSUnitView()
            }
        }
    }


    private fun makeVisibleUSUnitView(){
        currentVisibleView = US_UNITS_VIEW
        bindinging.etHeight.visibility = View.GONE
        bindinging.etWeight.visibility = View.GONE
        bindinging.llMetricsView.visibility = View.GONE

        bindinging.etUsWeight.text!!.clear()
        bindinging.etUsFeet.text!!.clear()
        bindinging.etUsInches.text!!.clear()

        bindinging.etUsWeight.visibility = View.VISIBLE
        bindinging.llUsView.visibility = View.VISIBLE

        bindinging.llBmiResults.visibility = View.INVISIBLE
    }

    private fun makeVisibleMetricUnitView(){
        currentVisibleView = METRIC_UNITS_VIEW
        bindinging.etHeight.visibility = View.VISIBLE
        bindinging.etWeight.visibility = View.VISIBLE
        bindinging.llMetricsView.visibility = View.VISIBLE

        bindinging.etHeight.text!!.clear()
        bindinging.etWeight.text!!.clear()

        bindinging.etUsWeight.visibility = View.GONE
        bindinging.llUsView.visibility = View.GONE

        bindinging.llBmiResults.visibility = View.INVISIBLE
    }



    private fun displayBMIResults(bmi: Float){
        val bmiLabel : String
        val bmiDescription: String

        if(bmi.compareTo(15.0f)<=0){
            bmiLabel = "Very Severely UnderWeight"
            bmiDescription = "Oops! You really need to take better care of yourself. Eat more."
        }else if(bmi.compareTo(15.0f) >0 && bmi.compareTo(16f)<=0){
            bmiLabel = "Severely UnderWeight"
            bmiDescription = "Oops! You really need to take better care of yourself. Eat more."
        }
        else if(bmi.compareTo(16f) >0 && bmi.compareTo(18.5f)<=0){
            bmiLabel = "UnderWeight"
            bmiDescription = "Oops! You really need to take better care of yourself. Eat more."
        }
        else if(bmi.compareTo(18.5f) >0 && bmi.compareTo(25f)<=0){
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in Good Shape."
        }else if(bmi.compareTo(25f) >0 && bmi.compareTo(30f)<=0){
            bmiLabel = "OverWeight"
            bmiDescription = "Oops! You really need to take better care of yourself. Workout more."
        }else if(bmi.compareTo(30) >0 && bmi.compareTo(35)<=0){
            bmiLabel = "Obese Class | (Moderately Obese)"
            bmiDescription = "Oops! You really need to take better care of yourself. Workout more."
        }else if(bmi.compareTo(35) >0 && bmi.compareTo(0)<=0){
            bmiLabel = "Obese Class || (Severely Obese)"
            bmiDescription = "OMG! You are in very dangerous Condition. Act Now!"
        }else{
            bmiLabel = "Obese Class ||| (Severely Obese)"
            bmiDescription = "OMG! You are in very dangerous Condition. Act Now!"
        }
        bindinging.llBmiResults.visibility = View.VISIBLE


        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()

        bindinging.tvYourBmiValue.text = bmiValue
        bindinging.tvYourBmiType.text = bmiLabel
        bindinging.tvYourBmiDescription.text = bmiDescription
    }

    private fun validateMetric() : Boolean{
        var isValid = true
        if(bindinging.etWeight.text.toString().isEmpty()){
            isValid = false
        }else if (bindinging.etHeight.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }
    private fun validateUS(): Boolean {
        var isValid = true
        when {
            bindinging.etUsFeet.text.toString().isEmpty() -> {
                isValid = false
            }
            bindinging.etUsInches.text.toString().isEmpty() -> {
                isValid = false
            }
            bindinging.etUsWeight.text.toString().isEmpty() -> {
                isValid = false
            }
        }
        return isValid
    }
}