package com.example.chatappkotlinnavigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.DocumentsContract.Document
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappkotlinnavigation.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ChatFragment : Fragment() {

    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : ChatRecyclerAdapter
    private var chats = arrayListOf<Chat>()
    


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatRecyclerAdapter()
        binding.chatRecycler.adapter = adapter
        binding.chatRecycler.layoutManager = LinearLayoutManager(requireContext())

            //Send Buttona basınca olacaklar yani mesaj yollayacağız
        binding.sendButton.setOnClickListener {


            auth.currentUser?.let { // Eğer currentuser null değilse
                val user = it.email.toString()
                val chatText  = binding.chatText.text.toString()
                val date = FieldValue.serverTimestamp() // Güncel zamanı alıyoruz.

                val dataMap = HashMap<String,Any>()
                dataMap.put("text",chatText)
                dataMap.put("user",user)
                dataMap.put("date",date)

                firestore.collection("Chats").add(dataMap).addOnSuccessListener {

                    binding.chatText.setText("")


                }.addOnFailureListener {
                    Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_LONG).show()
                    binding.chatText.setText("")

                }
            }
        }

        firestore.collection("Chats").orderBy("date",Query.Direction.ASCENDING).addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()

            } else {
                if (value != null){
                    if (value.isEmpty){
                        Toast.makeText(requireContext(),"Mesaj Yok",Toast.LENGTH_LONG).show()
                    } else { // Boş olmadığına emin olduk
                        //Veriyi almak
                        val documents = value.documents
                            chats.clear()
                        for (document in documents){
                            val text = document.get("text") as String
                            val user = document.get("user") as String
                            val chat = Chat(user, text)
                            chats.add(chat)
                            adapter.chats = chats
                        }

                    }
                    adapter.notifyDataSetChanged()
                }
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}