package com.example.sendbirdkotlinblog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        SendBird.init("APP_ID_HERE", this)

        button_login_connect.setOnClickListener {
            connectToSendBird(edittext_login_user_id.text.toString(), edittext_login_nickname.text.toString())

        }
    }

    private fun connectToSendBird(userID: String, nickname: String) {
        SendBird.connect(userID) { user, e ->
            if (e != null) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            } else {
                SendBird.updateCurrentUserInfo(nickname, null) { e ->
                    if (e != null) {
                        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    }
                    val intent = Intent(this, ChannelListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
