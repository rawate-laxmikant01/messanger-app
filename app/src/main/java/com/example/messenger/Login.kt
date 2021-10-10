package com.example.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.messenger.Fcm.FcmNotificationsSender
import com.example.messenger.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class Login : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        binding.btnLogIn.setOnClickListener(View.OnClickListener {

            if(binding.loginEmail.text.toString().isEmpty()){
                Toast.makeText(this, "Please Fill Email", Toast.LENGTH_SHORT).show()
            }
            if(binding.loginPassword.text.toString().isEmpty()){
                Toast.makeText(this, "Please Fill Password", Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword(binding.loginEmail.text.toString(),binding.loginPassword.text.toString()).addOnCompleteListener(
                    OnCompleteListener {
                        notification()
                        startActivity(Intent(this,MainActivity::class.java))
                    }).addOnFailureListener(OnFailureListener {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                })
            }


        })

    }

    fun signUp(view: android.view.View) {

        startActivity(Intent(this,Registration::class.java))
    }


    private fun notification() {
        lateinit var token:String
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@Login, "Failed", Toast.LENGTH_SHORT).show()
                } else token = task.result.toString()
                //    Log.d("otp", token)
                val fcmNotificationsSender = FcmNotificationsSender(
                    token, "Messenger", "Welcome To Chat Family",
                    this@Login
                )
                fcmNotificationsSender.SendNotifications()
            }
    }

}