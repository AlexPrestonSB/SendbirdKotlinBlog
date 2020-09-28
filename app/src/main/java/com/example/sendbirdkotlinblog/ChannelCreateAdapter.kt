package com.example.sbkotlinguide

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sendbirdkotlinblog.R
import com.sendbird.android.User
import kotlinx.android.synthetic.main.item_create.view.*

class ChannelCreateAdapter( listener: OnItemCheckedChangeListener) : RecyclerView.Adapter<ChannelCreateAdapter.UserHolder>() {

    interface OnItemCheckedChangeListener {
        fun onItemChecked(user: User, checked: Boolean)
    }

    private var users: MutableList<User>
    private var checkedListener: OnItemCheckedChangeListener

    companion object {
        fun selectedUsers() = ArrayList<String>()
        fun sparseArray() = SparseBooleanArray()
    }

    init {
        users = ArrayList()
        this.checkedListener = listener
    }


    fun addUsers(users: MutableList<User>) {
        this.users = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): UserHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserHolder(layoutInflater.inflate(R.layout.item_create, parent, false))
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bindViews(users[position], position, checkedListener)
    }

    class UserHolder(view: View) : RecyclerView.ViewHolder(view) {

        val checkbox = view.checkbox_create
        val userId = view.text_create_user

        fun bindViews(user: User, position: Int, listener: OnItemCheckedChangeListener) {

            userId.text = user.userId

            checkbox.isChecked = sparseArray().get(position, false)

            checkbox.setOnCheckedChangeListener() {buttonView, isChecked ->
                listener.onItemChecked(user, isChecked)

                if (isChecked) {
                    selectedUsers().add(user.userId)
                    sparseArray().put(position, true)
                } else {
                    selectedUsers().remove(user.userId)
                    sparseArray().put(position, false)
                }
            }

        }


    }

}