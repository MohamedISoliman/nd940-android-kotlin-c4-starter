package com.udacity.project4.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.R

/**
 *
 * Created by Mohamed Ibrahim on 3/24/20.
 */
class RegisterActivity : AppCompatActivity() {


    companion object {
        @JvmStatic
        fun start(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

    }
}