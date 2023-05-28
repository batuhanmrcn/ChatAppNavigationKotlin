package com.example.chatappkotlinnavigation

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class ChatRecyclerAdapter : RecyclerView.Adapter<ChatRecyclerAdapter.ChatHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val TYPE_MESSAGE_RECEIVED = 2

    class ChatHolder(itemView : View) : RecyclerView.ViewHolder(itemView)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {



        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row,parent,false)
        return ChatHolder(view)
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Chat>(){
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

    }


    private val recyclerListDiffer = AsyncListDiffer(this,diffUtil)

    var chats : List<Chat>
        get() = recyclerListDiffer.currentList
                set(value) = recyclerListDiffer.submitList(value)


    override fun getItemViewType(position: Int): Int {


        val chat = chats.get(position)

        if (chat.user == FirebaseAuth.getInstance().currentUser?.email.toString()){

            return VIEW_TYPE_MESSAGE_SENT

        } else {
            return TYPE_MESSAGE_RECEIVED
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {

        val textView = holder.itemView.findViewById<TextView>(R.id.chatRecyclerTextView)
        textView.text = "${chats.get(position).user} : ${chats.get(position).text}"

    }
}