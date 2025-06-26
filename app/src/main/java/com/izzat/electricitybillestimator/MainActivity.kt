package com.izzat.electricitybillestimator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.OnBackPressedCallback

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Load XML layout

        dbHelper = DatabaseHelper(this)

        // Find views
        val monthSpinner = findViewById<Spinner>(R.id.spinnerMonth)
        val rebateSpinner = findViewById<Spinner>(R.id.spinnerRebate)
        val editTextUnits = findViewById<EditText>(R.id.editTextUnits)
        val buttonCalculate = findViewById<Button>(R.id.buttonCalculate)
        val textTotalCharges = findViewById<TextView>(R.id.textTotalCharges)
        val textFinalCost = findViewById<TextView>(R.id.textFinalCost)
        val buttonViewHistory = findViewById<Button>(R.id.buttonViewHistory)

        // Setup month spinner
        val monthAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.month_array,
            android.R.layout.simple_spinner_item
        )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter

        // Setup rebate spinner
        val rebateAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.rebate_array,
            android.R.layout.simple_spinner_item
        )
        rebateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rebateSpinner.adapter = rebateAdapter

        // On calculate button click
        buttonCalculate.setOnClickListener {
            val unitText = editTextUnits.text.toString()

            if (unitText.isEmpty()) {
                Toast.makeText(this, "Please enter units used", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val units = unitText.toInt()
            val rebatePercentStr = rebateSpinner.selectedItem.toString().replace("%", "")
            val rebatePercent = rebatePercentStr.toDouble() / 100
            val totalCharges = calculateElectricityCharges(units)
            val finalCost = totalCharges - (totalCharges * rebatePercent)
            val month = monthSpinner.selectedItem.toString()

            textTotalCharges.text = "Total Charges: RM %.2f".format(totalCharges)
            textFinalCost.text = "Final Cost (After Rebate): RM %.2f".format(finalCost)

            val inserted = dbHelper.insertBill(month, units, rebatePercent * 100, totalCharges, finalCost)
            if (inserted) {
                Toast.makeText(this, "Saved to database", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error saving to database", Toast.LENGTH_SHORT).show()
            }
        }

        // Open history screen
        buttonViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        //About Activity
        val buttonAbout = findViewById<Button>(R.id.buttonAbout)
        buttonAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
        //Back press 2 times
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    backToast.cancel()
                    finish() // or activity?.finish() if inside fragment
                } else {
                    backToast = Toast.makeText(this@MainActivity, "Press back again to exit", Toast.LENGTH_SHORT)
                    backToast.show()
                    backPressedTime = System.currentTimeMillis()
                }
            }
        })

    }

    private fun calculateElectricityCharges(units: Int): Double {
        var total = 0.0
        var remaining = units

        if (remaining > 0) {
            val block = minOf(200, remaining)
            total += block * 0.218
            remaining -= block
        }

        if (remaining > 0) {
            val block = minOf(100, remaining)
            total += block * 0.334
            remaining -= block
        }

        if (remaining > 0) {
            val block = minOf(300, remaining)
            total += block * 0.516
            remaining -= block
        }

        if (remaining > 0) {
            total += remaining * 0.546
        }

        return total
    }

}
