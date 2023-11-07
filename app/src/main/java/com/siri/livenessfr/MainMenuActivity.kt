package com.siri.livenessfr

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.main_menu_activity.*

class MainMenuActivity : AppCompatActivity() {
    private var animatImg: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_activity)

        animatImg = findViewById(R.id.ld_wating) as ImageView

        Glide.with(this)
            .load(R.drawable.linkface)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            .into(animatImg!!)


        btn_start.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
    }


    companion object {

        fun open(activity: Activity ) {
            val intent = Intent(activity, MainMenuActivity::class.java)
            activity.startActivity(intent)
        }

    }
}
