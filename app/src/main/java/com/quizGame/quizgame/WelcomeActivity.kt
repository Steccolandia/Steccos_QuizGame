package com.quizGame.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.example.quizgame.R
import com.example.quizgame.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var  splashBinding : ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //by writing these three lines (and line 9) we can now access the components in the welcome activity (splash screen), using the splash binding object
        splashBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view = splashBinding.root
        setContentView(view)

        val alphaAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.splash_anim)
        splashBinding.textViewSplash.startAnimation(alphaAnimation)
        //handlers allow us to make a schedule. the process that we make is waiting for the time to start
        //when time's up, the code is executed. Create an object from the handler class now:
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)   //the animation lasts 3 seconds, then waits 1 second then execute the code written here

    }

}