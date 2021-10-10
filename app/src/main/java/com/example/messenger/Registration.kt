package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.Model.RegistrationModel
import com.example.messenger.databinding.ActivityRegistrationBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class Registration : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var token: String
    //val myRef = db.collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = Firebase.auth
        binding.btnRegister.setOnClickListener(View.OnClickListener {

            if (binding.registerName.text.toString().isEmpty()) {
                Toast.makeText(this, "Please Fill Name", Toast.LENGTH_SHORT).show()
            }
            if (binding.registerEmail.text.toString().isEmpty()) {
                Toast.makeText(this, "Please Fill Email", Toast.LENGTH_SHORT).show()
            }
            if (binding.registerNumber.text.toString().isEmpty()) {
                Toast.makeText(this, "Please Fill Number", Toast.LENGTH_SHORT).show()
            }
            if (binding.registerPassword.text.toString().isEmpty()) {
                Toast.makeText(this, "Please Fill Password", Toast.LENGTH_SHORT).show()
            } else {

                tokengenerate()

                var name: String = binding.registerName.text.toString()
                var email: String = binding.registerEmail.text.toString()
                var number: String = binding.registerNumber.text.toString()
                var password: String = binding.registerPassword.text.toString()
                var status = "online"
                // val id:String=FirebaseAuth.getInstance().currentUser.uid


                auth.createUserWithEmailAndPassword(
                    binding.registerEmail.text.toString(),
                    binding.registerPassword.text.toString()
                )
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth!!.currentUser!!.uid
                            // val idd = auth.currentUser.uid
                            startActivity(Intent(this, MainActivity::class.java))
                            val registrationModel =
                                RegistrationModel(
                                    name,
                                    email,
                                    userId,
                                    number,
                                    password,
                                    status,
                                    token
                                )

                            var uid = FirebaseAuth.getInstance().currentUser?.uid
                            db.collection("users")
                                .document(uid ?: "")
                                .set(registrationModel)
                                .addOnSuccessListener { documentRefrence ->
                                    // Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                    Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    // Log.w(TAG, "Error adding document", e)
                                    Toast.makeText(this, "failed" + e.message, Toast.LENGTH_SHORT)
                                        .show()
                                }

                            finish()
                        } else {
                            Toast.makeText(this, "Authentecation failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        })
    }

    fun logIn(view: android.view.View) {
        startActivity(Intent(this, Login::class.java))
    }

    private fun tokengenerate() {

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@Registration, "Failed", Toast.LENGTH_SHORT).show()
                } else {
                    token = task.result.toString()

                }
//                var sendertoken: HashMap<String, Any> = HashMap()
//                sendertoken.put("token", token)
//                db.collection("chat").document(FirebaseAuth.getInstance().currentUser.uid)
//                    .set(sendertoken)
            }
    }
}