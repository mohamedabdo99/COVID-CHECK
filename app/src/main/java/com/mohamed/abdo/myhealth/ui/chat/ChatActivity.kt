package com.mohamed.abdo.myhealth.ui.chat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mohamed.abdo.myhealth.R
import com.mohamed.abdo.myhealth.databinding.ActivityChatBinding
import com.mohamed.abdo.myhealth.pojo.MessageModel
import com.mohamed.abdo.myhealth.utils.BotResponse
import com.mohamed.abdo.myhealth.utils.Constants.OPEN_GOOGLE
import com.mohamed.abdo.myhealth.utils.Constants.OPEN_SEARCH
import com.mohamed.abdo.myhealth.utils.Constants.RECEIVE_ID
import com.mohamed.abdo.myhealth.utils.Constants.SEND_ID
import com.mohamed.abdo.myhealth.utils.Time
import kotlinx.coroutines.*

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    val botList = listOf("Dr:mohamed","Dr:ahmed","Dr:mahmoud")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this ,R.layout.activity_chat)
        intiRecyclerView()
        clickWhatHappen()

        val random = (0..3).random()
        customMessage("Hello! Today you're speaking with ${botList[random]} " +
                ", how may I help ? ")

    }

    private fun customMessage(botName: String) {
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main){
                val timeStamp = Time.timeStamp()
                chatAdapter.setMessageList(MessageModel(botName , RECEIVE_ID,timeStamp))
                binding.rvMessages.scrollToPosition(chatAdapter.itemCount-1)

            }
        }
    }

    private fun clickWhatHappen()
    {
        binding.btnSend.setOnClickListener {
            sendMessage()
        }
        binding.etMessage.setOnClickListener {
            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main){
                 binding.rvMessages.scrollToPosition(chatAdapter.itemCount-1)
                }
            }
        }
    }

    private fun intiRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.rvMessages.adapter = chatAdapter
        binding.rvMessages.layoutManager = LinearLayoutManager(applicationContext)

    }

    private fun sendMessage(){
        val message = binding.etMessage.text.toString()
        val timestamp = Time.timeStamp()
        if (message.isNotEmpty()){
            binding.etMessage.setText("")
            chatAdapter.setMessageList(MessageModel(message, SEND_ID,timestamp))
            binding.rvMessages.scrollToPosition(chatAdapter.itemCount-1)

            botResponse(message)
        }
    }

    private fun botResponse(message: String) {
        val timestamp = Time.timeStamp()
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main){
                val response = BotResponse.basicResponses(message)
                chatAdapter.setMessageList(MessageModel(response, RECEIVE_ID,timestamp))
                binding.rvMessages.scrollToPosition(chatAdapter.itemCount-1)

                when(response){
                    OPEN_GOOGLE->{
                        val goToSite = Intent(Intent.ACTION_VIEW)
                        goToSite.data = Uri.parse("https://www.google.com/")
                        startActivity(goToSite)
                    }
                    OPEN_SEARCH->{
                        val goToSite = Intent(Intent.ACTION_VIEW)
                        val searchTerm : String? = message.substringAfter("search")
                        goToSite.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(goToSite)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main){
                binding.rvMessages.scrollToPosition(chatAdapter.itemCount-1)
            }
        }
    }
}