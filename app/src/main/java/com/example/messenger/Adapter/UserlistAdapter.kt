package com.example.messenger.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.green
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.Chat
import com.example.messenger.Model.RegistrationModel
import com.example.messenger.R
import com.google.firebase.auth.FirebaseAuth

class UserlistAdapter (val todolist: ArrayList<RegistrationModel>,val context: Context):
    RecyclerView.Adapter<UserlistAdapter.userViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_userlist, parent, false)

            return userViewHolder(view)
        }

        override fun onBindViewHolder(holder: userViewHolder, position: Int) {
            holder.username.text=todolist[position].name
            holder.userOnline.text=todolist[position].status
            if(todolist[position].status.equals("online")){
                holder.userOnline.setTextColor(Color.parseColor("#3DB511"))
            }
            holder.cardView.setOnClickListener(View.OnClickListener {
                val intent= Intent(context, Chat::class.java)
                intent.putExtra("name",todolist[position].name)
                intent.putExtra("id",todolist[position].id)
                intent.putExtra("token",todolist[position].token)

                context.startActivity(intent)
            })


        }

        override fun getItemCount(): Int {
            return todolist.size
        }

        class userViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView){

            var username: TextView =itemView.findViewById(R.id.customUserNAme)
            var cardView:CardView=itemView.findViewById(R.id.customUserCardview)
            var userOnline:TextView=itemView.findViewById(R.id.userlistOffline)

        }
}