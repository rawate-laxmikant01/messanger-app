package com.example.messenger.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger.Chat
import com.example.messenger.Model.ChatModel
import com.example.messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ChatAdapter(val todolist: ArrayList<ChatModel>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val Text_type: Int = 0
    private val Image_type: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == Text_type) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_chatview, parent, false)
            return chatViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_chatimageview, parent, false)
            return chatImageviewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == Text_type) {
            (holder as chatViewHolder).bind(todolist[position])
        } else {
            (holder as chatImageviewHolder).bind(todolist[position])
        }
    }

    override fun getItemCount(): Int {
        return todolist.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (todolist[position].msgtype == 0) {
            return Text_type
        } else {
            return Image_type
        }
    }

    class chatViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        fun bind(chatModel: ChatModel) {
            var chatMsg: TextView = itemView.findViewById(R.id.chatMsg)
            var chattime: TextView = itemView.findViewById(R.id.chatTime)
            var chatConstrain: ConstraintLayout = itemView.findViewById(R.id.chatConstrain)
            var custmLinear: LinearLayout = itemView.findViewById(R.id.custumLinear)

            chatMsg.text = chatModel.msg
            chattime.text = chatModel.time

            if (!chatModel.id.toString().equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                chatConstrain.setBackgroundResource(R.drawable.tint_color)
            } else {
                chatConstrain.setBackgroundResource(R.drawable.purple_color)
                custmLinear.gravity = Gravity.START
            }

            custmLinear.setOnLongClickListener{
                //     Toast.makeText(itemView.context, "Long click", Toast.LENGTH_SHORT).show()

                var chatclass:chatImageviewHolder= chatImageviewHolder(itemView)
                chatclass.showAlertDialogButtonClicked(it,chatModel.documentid!!,chatModel.id!!)
                return@setOnLongClickListener true
            }


        }
    }

    class chatImageviewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        fun bind(chatModel: ChatModel) {
            val chatImg: ImageView = itemView.findViewById(R.id.chatImg)
            val imageTime: TextView = itemView.findViewById(R.id.imageTime)
            val imagelinear: LinearLayout = itemView.findViewById(R.id.imageLinear)
            val colorlinear: LinearLayout = itemView.findViewById(R.id.linearcolor)
            Glide.with(itemView.context).load(chatModel.msg).into(chatImg)
            imageTime.text = chatModel.time

            if (!chatModel.id.toString().equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                colorlinear.setBackgroundResource(R.drawable.tint_color)
            } else {
                colorlinear.setBackgroundResource(R.drawable.purple_color)
                imagelinear.gravity = Gravity.START
            }
            
            imagelinear.setOnLongClickListener{
           //     Toast.makeText(itemView.context, "Long click", Toast.LENGTH_SHORT).show()
                showAlertDialogButtonClicked(it,chatModel.documentid!!,chatModel.id!!)
                return@setOnLongClickListener true
            }
//

        }

        fun showAlertDialogButtonClicked(view: View?, documentid:String,chatid:String) {
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
           // builder.setTitle("Notice")
            builder.setMessage("Delete message?")

            // add the buttons
            builder.setPositiveButton("Delete from everyone", null)
            builder.setNeutralButton("Delete from me", null)
            builder.setNegativeButton("Cancel", null)

            builder.setPositiveButton("Delete from everyone",
                DialogInterface.OnClickListener { dialog, which -> // do something like...
                    deleteFromeveryone(documentid,chatid)
                    Toast.makeText(itemView.context, "Delete from everyone", Toast.LENGTH_SHORT).show()
                })

            builder.setNeutralButton("Delete from me",
                DialogInterface.OnClickListener { dialog, which -> // do something like...
                    deleteFromMe(documentid,chatid)
                    Toast.makeText(itemView.context, "Delete from me", Toast.LENGTH_SHORT).show()
                })

            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> // do something like...
               //  Toast.makeText(itemView.context, "Cancel", Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                })

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        private fun deleteFromeveryone(documentid: String, chatid: String) {
            Firebase.firestore.collection("chat").document(FirebaseAuth.getInstance().currentUser!!.uid+chatid)
                .collection("message").document(documentid).delete()

            Firebase.firestore.collection("chat").document(chatid+FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("message").document(documentid).delete()

        }

        private fun deleteFromMe(documentid: String,chatid: String) {
            Firebase.firestore.collection("chat").document(chatid+FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("message").document(documentid).delete()
        }
    }
}


//    override fun onBindViewHolder(holder: chatViewHolder, position: Int) {
//
//        if(position==Text_type){
//
//        }
//
////        holder.chatMsg.text=todolist[position].msg
////        holder.chattime.text=todolist[position].time
////
////        if(todolist[position].msgtype.equals("text")){
////            holder.chatImg.visibility=View.INVISIBLE
////            holder.chatConstrain.visibility=View.VISIBLE
////
////            if(!todolist[position].id.toString().equals(FirebaseAuth.getInstance().currentUser!!.uid)){
////                holder.chatConstrain.setBackgroundResource(R.drawable.tint_color)
////            }
////            else{
////                holder.chatConstrain.setBackgroundResource(R.drawable.purple_color)
////                holder.custmLinear.gravity=Gravity.LEFT
////            }
////        }
////        if(todolist[position].msgtype.equals("image")){
////            holder.chatImg.visibility=View.VISIBLE
////            holder.chatConstrain.visibility=View.INVISIBLE
////            Glide.with(context).load(todolist[position].msg).into(holder.chatImg);
////        }
//    }