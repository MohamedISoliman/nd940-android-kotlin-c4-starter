package com.udacity.project4.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {


    companion object {

        fun startIntent(context: Context): Intent {
            return Intent(context, AuthenticationActivity::class.java)
        }
    }

    val RC_SIGN_IN = 201


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        
        if (FirebaseAuth.getInstance().currentUser != null) {
            navigateToHomeScreen()
        } else {
            startAuthenticationScreen()
        }
    }

    private fun startAuthenticationScreen() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_location)
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    navigateToHomeScreen()
                } else {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
                }
            } else {
                finish()
            }
        }

    }

    private fun navigateToRegisterScreen() {
        startActivity(RegisterActivity.start(this))
    }

    private fun navigateToHomeScreen() {
        startActivity(RemindersActivity.startIntent(this))
        finish()
    }
}
