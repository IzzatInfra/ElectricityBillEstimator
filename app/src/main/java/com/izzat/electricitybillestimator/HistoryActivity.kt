package com.izzat.electricitybillestimator

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val listView = findViewById<ListView>(R.id.listViewHistory)
        val db = DatabaseHelper(this)
        val data = db.getAllBills()

        val adapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf("month", "final"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = data[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("id", selected["id"])
            startActivity(intent)
        }
    }
}
