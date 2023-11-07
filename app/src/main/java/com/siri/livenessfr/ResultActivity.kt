package com.siri.livenessfr

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)
    }


    companion object {

        fun open(activity: Activity ) {
            val intent = Intent(activity, ResultActivity::class.java)
            activity.startActivity(intent)
        }

    }
}
