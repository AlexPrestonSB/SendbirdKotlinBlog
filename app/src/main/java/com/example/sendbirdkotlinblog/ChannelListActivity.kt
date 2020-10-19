package com.example.sendbirdkotlinblog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sendbirdkotlinblog.adapters.ChannelListAdapter
import com.sendbird.android.GroupChannel
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_channel.*


class ChannelListActivity : AppCompatActivity(), ChannelListAdapter.OnChannelClickedListener {

    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"

    lateinit var recyclerView: RecyclerView

    lateinit var adapter: ChannelListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)

        adapter =
            ChannelListAdapter(this)
        recyclerView = recycler_group_channels
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fab_group_channel_create.setOnClickListener{
            val intent = Intent(this, ChannelCreateActivity::class.java)
            startActivity(intent)
        }

        addChannels()
    }

    private fun addChannels() {
        val channelList = GroupChannel.createMyGroupChannelListQuery()
        channelList.limit = 100
        channelList.next { list, e ->
            if (e != null) {
                Log.e("TAG", e.message)
            }
            adapter.addChannels(list)
        }
    }

    override fun onItemClicked(channel: GroupChannel) {
        val intent = Intent(this, ChannelActivity::class.java)
        intent.putExtra(EXTRA_CHANNEL_URL, channel.url)
        startActivity(intent)
    }
}