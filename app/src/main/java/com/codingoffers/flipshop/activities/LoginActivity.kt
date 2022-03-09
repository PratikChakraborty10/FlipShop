package com.codingoffers.flipshop.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.codingoffers.flipshop.R
import com.codingoffers.flipshop.firestore.FirestoreClass
import com.codingoffers.flipshop.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestoreSettings
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.et_email
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_splash.*


class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val typeface: Typeface = Typeface.createFromAsset(assets, "Montserrat-Bold.ttf")
        loginTv.typeface = typeface
        BtnLogin.typeface = typeface
        RegisterAccount.typeface = typeface

        RegisterAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        tv_forgotPassword.setOnClickListener(this)
        BtnLogin.setOnClickListener(this)
        RegisterAccount.setOnClickListener(this)
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        Log.i("First Name: ", user.firstName)
        Log.i("Last Name: ", user.lastName)
        Log.i("Email: ", user.email)

        // Redirect to the main screen after login
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onClick(view: View?) {
        if(view != null) {
            when(view.id) {
                R.id.tv_forgotPassword -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.BtnLogin -> {
                    logInRegisteredUser()
                }
                R.id.RegisterAccount -> {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' '}) -> {
                showErrorFunction(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' '}) -> {
                showErrorFunction(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                //showErrorFunction("Your details are valid", false)
                true
            }
        }
    }
    private fun logInRegisteredUser() {
        if(validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    //hideProgressDialog()
                    if(task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity )
                        //showErrorFunction("You are logged in successfully", false)
                    } else {
                        hideProgressDialog()
                        showErrorFunction(task.exception!!.message.toString(), true)
                    }
                }
        }
    }
}
