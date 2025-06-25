package com.izzat.electricitybillestimator

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val textMonth = findViewById<TextView>(R.id.textMonth)
        val textUnits = findViewById<TextView>(R.id.textUnits)
        val textRebate = findViewById<TextView>(R.id.textRebate)
        val textTotal = findViewById<TextView>(R.id.textTotal)
        val textFinal = findViewById<TextView>(R.id.textFinal)

        val id = intent.getStringExtra("id")?.toIntOrNull()
        if (id != null) {
            val db = DatabaseHelper(this)
            val bill = db.getBillById(id)

            if (bill != null) {
                textMonth.text = "Month: ${bill["month"]}"
                textUnits.text = "Units Used: ${bill["units"]} kWh"
                textRebate.text = "Rebate: ${bill["rebate"]}"
                textTotal.text = "Total Charges: ${bill["total"]}"
                textFinal.text = "Final Cost: ${bill["final"]}"
            } else {
                Toast.makeText(this, "Record not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
