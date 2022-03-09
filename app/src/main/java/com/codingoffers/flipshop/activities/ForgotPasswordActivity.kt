package com.codingoffers.flipshop.activities

import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.codingoffers.flipshop.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        backPressed.setOnClickListener {
            onBackPressed()
        }
        val typeface: Typeface = Typeface.createFromAsset(assets, "Montserrat-Bold.ttf")
        forgotPasswordTv.typeface = typeface

        btn_submit.setOnClickListener {
            val email: String = et_email_forgot.text.toString().trim { it <= ' ' }
            if(email.isEmpty()) {
                showErrorFunction(resources.getString(R.string.err_msg_enter_email), true)
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                 FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                     .addOnCompleteListener { task ->
                         hideProgressDialog()
                         if(task.isSuccessful) {
                             Toast.makeText(this@ForgotPasswordActivity,
                                 resources.getString(R.string.email_sent_success),
                                 Toast.LENGTH_LONG
                             ).show()
                             finish()
                         } else {
                             showErrorFunction(task.exception!!.message.toString(), true)
                         }
                     }
            }
        }
    }


}