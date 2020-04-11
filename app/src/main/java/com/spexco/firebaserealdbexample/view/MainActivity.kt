package com.spexco.firebaserealdbexample.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.spexco.firebaserealdbexample.R
import com.spexco.firebaserealdbexample.adapter.UsersAdapter
import com.spexco.firebaserealdbexample.databinding.RowUserBinding
import com.spexco.firebaserealdbexample.model.User
import com.yenen.ahmet.basecorelibrary.base.adapter.BaseViewBindingRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val dbReferance = FirebaseDatabase.getInstance().reference.child("users")
    private val userAdapter = UsersAdapter()
    private var selectedUser: User? = null
    private var selectedUserPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        addValueEventListener()
        addUserClickListener()
        deleteUserClickListener()
        updateUserListener()
        orderByAgeClickListener()
    }

    private fun addValueEventListener() {
        dbReferance.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                setAdapterItems(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DB", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun addUserClickListener() {
        addUser.setOnClickListener {
            val userName = edtUserName.text.toString().trim()
            val userMail = edtUserMail.text.toString().trim()
            val userAge = edtUserAge.text.toString().trim()
            if (userName.length > 1 && userMail.length > 1 && userAge.length > 0) {
                isCheckEmail(userMail, userName, userAge.toInt())
            }
        }
    }

    private fun addUser(userName: String, userMail: String, userAge: Int) {
        clearText()
        dbReferance.push().key?.let {
            // Loading //
            val user = User(it, userAge, userMail, userName)
            val task  = dbReferance.child(it).setValue(user)
            task.addOnSuccessListener {
                    // Loading // finish
                    showToast("Kullanıcı Eklendi")

                }.addOnFailureListener {
                    // Loading // finish
                    showToast("Kullanıcı Eklenirken Hata Oluştu : ${it.toString()}")
                    Log.e("Error Firebase ADD", it.toString())
                }
        }

    }

    private fun initRecyclerView() {
        recyclerView.adapter = userAdapter
        userAdapter.setListener(object :
            BaseViewBindingRecyclerViewAdapter.ClickListener<User, RowUserBinding> {
            override fun onItemClick(item: User, position: Int, rowBinding: RowUserBinding) {
                edtUserName.setText(item.userName)
                edtUserMail.setText(item.userEmail)
                edtUserAge.setText(item.userAge.toString())
                selectedUser = item
                selectedUserPosition = position
            }
        })
    }

    private fun deleteUser(userDbReferance: DatabaseReference) {
        // Delete User //
        userDbReferance.removeValue().addOnSuccessListener {
            // Loading // finish
            showToast("Kullanıcı silindi")
        }.addOnFailureListener {
            // Loading // finish
            showToast(it.toString())
            Log.e("Error Firebase Delete", it.toString())
        }
        clearText()
    }

    private fun deleteUserClickListener() {
        deleteUser.setOnClickListener {
            selectedUser?.let {
                deleteUser(dbReferance.child(it.userKey))
            }
        }
    }

    private fun clearText() {
        edtUserName.setText("")
        edtUserMail.setText("")
        edtUserAge.setText("")
        selectedUser = null
    }

    private fun updateUserListener() {
        updateUser.setOnClickListener {
            val userName = edtUserName.text.toString().trim()
            val userMail = edtUserMail.text.toString().trim()
            val userAge = edtUserAge.text.toString().trim()
            if (userName.length > 1 && userMail.length > 1 && userAge.length > 0 && selectedUser != null) {
                val user = User(selectedUser!!.userKey, userAge.toInt(), userName, userMail)
                updateUser(user)
            }
        }
    }

    private fun updateUser(user: User) {
        val map = HashMap<String, Any>().apply {
            put("userAge", user.userAge)
            put("userEmail", user.userEmail)
            put("userName", user.userName)
            put("userKey",user.userKey)
        }
        dbReferance.child(user.userKey).updateChildren(map)
            .addOnSuccessListener {
                showToast("Kullanıcı güncellendi")
            }
            .addOnFailureListener {
                showToast(it.toString())
                Log.e("Error Firebase Update", it.toString())
            }

    }

    private fun isCheckEmail(userEmail: String, userName: String, userAge: Int) {
        dbReferance.child("userEmail").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    showToast(p0.toString())
                    Log.e("ErrorFirabaseCheckEmail", p0.toString())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var isEmail = false
                    p0.children.iterator().forEach {
                        it?.getValue(User::class.java)?.let {
                            isEmail = true
                        }
                    }
                    if (!isEmail) {
                        addUser(userName, userEmail, userAge)
                    } else {
                        showToast("Sistemde bu gmail bulunmakdatır!")
                    }
                }

            })
    }

    private fun orderByAgeClickListener(){
        orderByAge.setOnClickListener {
            orderByAge()
        }
    }

    private fun orderByAge() {
        dbReferance.orderByChild("userAge")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    showToast(p0.toString())
                    Log.e("ErrorFirabaseOrderByAge", p0.toString())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    setAdapterItems(p0)
                }
            })
    }

    private fun showToast(text: String) {
        Toast.makeText(
            applicationContext,
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setAdapterItems(p0: DataSnapshot){
        val userItems = mutableListOf<User>()
        for (item in p0.children) {
            item?.getValue(User::class.java)?.let { user ->
                user.userKey = item.key!!
                userItems.add(user)
            }
        }

        userAdapter.clearItems()
        userAdapter.setItems(userItems)
    }

}
