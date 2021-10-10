package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.Adapter.UserlistAdapter
import com.example.messenger.Model.RegistrationModel
import com.example.messenger.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val db = Firebase.firestore
    lateinit var uuid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uuid = FirebaseAuth.getInstance().currentUser!!.uid
        db.collection("users").document(uuid).update("status", "online")
        var userRecycler: RecyclerView = binding.userRecycler

        val userlist = ArrayList<RegistrationModel>()
        userRecycler.layoutManager = LinearLayoutManager(this)


        db.collection("users")
            .addSnapshotListener { data, error ->
                if (error != null) {
                    Toast.makeText(this, "" + error.message, Toast.LENGTH_SHORT).show()
                } else {
                    if (data != null) {
                         userlist.clear()
                        for (document in data) {
                            val user: RegistrationModel =
                                document.toObject(RegistrationModel::class.java)
                            if (!FirebaseAuth.getInstance().currentUser!!.uid!!.equals(document.id)) {
                                userlist.add(user!!)
                            }
                            userRecycler.adapter = UserlistAdapter(userlist, this@MainActivity)
                        }
                    }
                }
            }
    }

    fun logOut(view: android.view.View) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, Login::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        db.collection("users").document(uuid).update("status", "online")
    }

    override fun onPause() {
        super.onPause()
        db.collection("users").document(uuid).update("status", "offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        db.collection("users").document(uuid).update("status", "offline")
    }


}