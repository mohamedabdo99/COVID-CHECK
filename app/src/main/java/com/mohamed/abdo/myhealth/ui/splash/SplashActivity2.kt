package com.mohamed.abdo.myhealth.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mohamed.abdo.myhealth.R
import com.mohamed.abdo.myhealth.databinding.ActivitySplach2Binding
import com.mohamed.abdo.myhealth.ui.main.MainActivity

class SplashActivity2 : AppCompatActivity() {
    private lateinit var binding:ActivitySplach2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this , R.layout.activity_splach2)
        //FULL SCREEN
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        //handler
        Handler().postDelayed(Runnable { //init Shared
            var intent = Intent(this@SplashActivity2, MainActivity::class.java)
            startActivity(intent)
            this@SplashActivity2.finish()
        }, 3000)
    }

    override fun onStart() {
        super.onStart()

    }

}