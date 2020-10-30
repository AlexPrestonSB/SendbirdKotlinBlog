package com.example.sendbirdkotlinblog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sendbirdkotlinblog.adapters.MessageAdapter
import com.sendbird.android.*
import com.sendbird.android.BaseChannel.SendUserMessageHandler
import com.sendbird.android.GroupChannel.GroupChannelGetHandler
import com.sendbird.android.SendBird.ChannelHandler
import kotlinx.android.synthetic.main.activity_chat.*


class ChannelActivity : AppCompatActivity() {

    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"
    private val CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT"


    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupChannel: GroupChannel
    private lateinit var channelUrl: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setUpRecyclerView()
        setButtonListeners()
    }

    override fun onResume() {
        super.onResume()
        channelUrl = getChannelURl()

        GroupChannel.getChannel(channelUrl,
            GroupChannelGetHandler { groupChannel, e ->
                if (e != null) {
                    // Error!
                    e.printStackTrace()
                    return@GroupChannelGetHandler
                }
                this.groupChannel = groupChannel
                getMessages()
            })

        SendBird.addChannelHandler(
            CHANNEL_HANDLER_ID,
            object : ChannelHandler() {
                override fun onMessageReceived(
                    baseChannel: BaseChannel,
                    baseMessage: BaseMessage
                ){
                    if (baseChannel.url == channelUrl) {
                        // Add new message to view
                        adapter.addFirst(baseMessage)
                        groupChannel.markAsRead()
                    }
                }
            })
    }

    override fun onPause() {
        super.onPause()
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID)
    }

    /**
     * Function handles setting handlers for back/send button
     */
    private fun setButtonListeners() {
        val back = button_gchat_back
        back.setOnClickListener {
            val intent = Intent(this, ChannelListActivity::class.java)
            startActivity(intent)
        }

        val send = button_gchat_send
        send.setOnClickListener {
            sendMessage()
        }
    }

    /**
     * Sends the message from the edit text, and clears text field.
     */
    private fun sendMessage()
    {
        val params = UserMessageParams()
            .setMessage(edit_gchat_message.text.toString())
        groupChannel.sendUserMessage(params,
            SendUserMessageHandler { userMessage, e ->
                if (e != null) {    // Error.
                    return@SendUserMessageHandler
                }
                adapter.addFirst(userMessage)
                edit_gchat_message.text.clear()
            })
    }


    /**
     * Function to get previous messages in channel
     */
    private fun getMessages() {

        val previousMessageListQuery = groupChannel.createPreviousMessageListQuery()

        previousMessageListQuery.load(
            100,
            true,
            object : PreviousMessageListQuery.MessageListQueryResult {
                override fun onResult(
                    messages: MutableList<BaseMessage>?,
                    e: SendBirdException?
                ) {
                    if (e != null) {
                        Log.e("Error", e.message)
                    }
                    adapter.loadMessages(messages!!)
                }
            })

    }

    /**
     * Set up the  recyclerview and set the adapter
     */
    private fun setUpRecyclerView() {
        adapter = MessageAdapter(this)
        recyclerView = recycler_gchat
        recyclerView.adapter = adapter
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        recyclerView.scrollToPosition(0)

    }

    /**
     * Get the Channel URL from the passed intent
     */
    private fun getChannelURl(): String {
        val intent = this.intent
        return intent.getStringExtra(EXTRA_CHANNEL_URL)
    }


}