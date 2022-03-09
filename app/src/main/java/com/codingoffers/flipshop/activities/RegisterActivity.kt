package com.codingoffers.flipshop.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.codingoffers.flipshop.R
import com.codingoffers.flipshop.firestore.FirestoreClass
import com.codingoffers.flipshop.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern


class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val typeface: Typeface = Typeface.createFromAsset(assets, "Montserrat-Bold.ttf")
        createAccountTv.typeface = typeface
        LoginAccount.typeface = typeface
        supportActionBar?.hide()
        backPressed.setOnClickListener {
            onBackPressed()
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        /*val emailPattern: String = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+"
        if(et_email.text.toString().matches(emailPattern)) {
            showErrorFunction("Enter a valid email ID", false)
            true
        }*/
        LoginAccount.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        register_btn.setOnClickListener {
            // Registering the user only if email is verified
            val emailText: String = et_email.text.toString()
            if(EMAIL_ADDRESS_PATTERN.matcher(emailText).matches()) {
                //validateRegisterUserDetail()
                registerUser()
            } else {
                showErrorFunction(resources.getString(R.string.pls_enter_a_valid_email_id), true)
               //Toast.makeText(this, resources.getString(R.string.pls_enter_a_valid_email_id), Toast.LENGTH_SHORT).show()
            }
            //validateRegisterUserDetail()

        }
        //val emailPattern: String = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+"
        //val emailID: String = et_email.text.toString().trim()
    }
    private fun validateRegisterUserDetail() : Boolean {
        return when {
            TextUtils.isEmpty(et_fName.text.toString().trim { it <= ' '}) -> {
                showErrorFunction(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(et_lName.text.toString().trim { it <= ' '}) -> {
                showErrorFunction(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' '}) -> {
                showErrorFunction(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' '}) -> {
                showErrorFunction(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(et_confirmPassword.text.toString().trim { it <= ' '}) -> {
                showErrorFunction(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }
            et_password.text.toString().trim { it <= ' ' } !=
                    et_confirmPassword.text.toString().trim { it <= ' ' } -> {
                        showErrorFunction(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                        false
                    }
            !cb_terms_and_condition.isChecked -> {
                showErrorFunction(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
                //showErrorFunction(resources.getString(R.string.registery_successfull), false)
                  true
            }
        }
    }
    // Email Address Pattern Function starts
    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    // Email Address Pattern Function ends

    private fun registerUser() {
        if(validateRegisterUserDetail()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }

            //Create an instance and create a register a user with email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        //hideProgressDialog()
                        if(task.isSuccessful) {

                            //Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            val user = User(
                                firebaseUser.uid,
                                et_fName.text.toString().trim { it <= ' ' },
                                et_lName.text.toString().trim { it <= ' ' },
                                et_email.text.toString().trim { it <= ' ' }
                            )

                            FirestoreClass().registerUser(this@RegisterActivity, user)

                            /*showErrorFunction(
                                "You are registered successfully. Your unique id is ${firebaseUser.uid}",
                                false
                            )*/
                            //FirebaseAuth.getInstance().signOut()
                            //finish()
                            finish()
                        } else {
                            hideProgressDialog()
                            showErrorFunction(task.exception!!.message.toString(), true)
                        }
                    }
                )
        }
    }
    fun userRegistrationSuccess() {
        // Hide the progress dialog
        hideProgressDialog()
        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.resgister_success),
            Toast.LENGTH_SHORT
        ).show()
    }
}