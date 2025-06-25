package com.izzat.electricitybillestimator

import android.os.Bundle
import android.text.util.Linkify
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val textGithub = findViewById<TextView>(R.id.textGithub)
        textGithub.text = "https://github.com/IzzatInfra/ElectricityBillEstimator"
        Linkify.addLinks(textGithub, Linkify.WEB_URLS)
    }
}
