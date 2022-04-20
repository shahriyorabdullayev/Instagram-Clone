package com.example.instagramappclone.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramappclone.R
import com.example.instagramappclone.adapter.SearchAdapter
import com.example.instagramappclone.managers.AuthManager
import com.example.instagramappclone.managers.DatabaseManager
import com.example.instagramappclone.managers.PrefsManager
import com.example.instagramappclone.managers.handler.DBFollowHandler
import com.example.instagramappclone.managers.handler.DBUserHandler
import com.example.instagramappclone.managers.handler.DBUsersHandler
import com.example.instagramappclone.model.FirebaseRequest
import com.example.instagramappclone.model.FirebaseResponse
import com.example.instagramappclone.model.Notification
import com.example.instagramappclone.model.User
import com.example.instagramappclone.network.ApiClient
import com.example.instagramappclone.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

/**
 * In SearchFragment, all registered users can be found by searching keyword and followed
 */

class SearchFragment : BaseFragment() {

    val TAG = SearchFragment::class.java.simpleName
    lateinit var rv_home: RecyclerView
    var items = ArrayList<User>()
    var users = ArrayList<User>()

    private lateinit var adapter: SearchAdapter

    private lateinit var apiService: ApiService
    private var token = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        initViews(view)

        return view
    }

    private fun initViews(view: View) {
        apiService = ApiClient.createService(ApiService::class.java)




        rv_home = view.findViewById(R.id.rv_search)
        rv_home.setLayoutManager(GridLayoutManager(activity, 1))
        val et_search = view.findViewById<EditText>(R.id.et_search)
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim()
                usersByKeyword(keyword)
            }
        })
        loadUsers()



    }




    private fun refreshAdapter(items: ArrayList<User>) {
        adapter = SearchAdapter(this, items)
        rv_home.adapter = adapter
    }

    fun usersByKeyword(keyword: String) {
        if (keyword.isEmpty())
            refreshAdapter(items)

        users.clear()
        for (user in items)
            if (user.fullname.toLowerCase().startsWith(keyword.toLowerCase()))
                users.add(user)

        refreshAdapter(users)
    }

    private fun loadUsers() {
        val uid = AuthManager.currentUser()!!.uid
        DatabaseManager.loadUsers(object : DBUsersHandler {
            override fun onSuccess(users: ArrayList<User>) {
                DatabaseManager.loadFollowing(uid, object : DBUsersHandler {
                    override fun onSuccess(following: ArrayList<User>) {
                        items.clear()
                        items.addAll(mergedUsers(uid,users, following))
                        refreshAdapter(items)
                    }

                    override fun onError(e: Exception) {

                    }
                })
            }

            override fun onError(e: Exception) {

            }
        })
    }

    private fun mergedUsers(uid:String, users: ArrayList<User>, following: ArrayList<User>): ArrayList<User> {
        val items = ArrayList<User>()
        for (u in users){
            val user = u
            for(f in following){
                if(u.uid == f.uid){
                    user.isFollowed = true
                    break
                }
            }
            if(uid != user.uid){
                items.add(user)
            }
        }
        return items
    }

    fun followOrUnfollow(to: User) {
        val uid = AuthManager.currentUser()!!.uid
        if (!to.isFollowed) {
            followUser(uid, to)
        } else {
            unFollowUser(uid, to)

        }
    }
    private fun sendFollowNotification(token: ArrayList<String>, user: String) {
        Log.d("tokennn", token.toString())
        apiService.sendNotification(FirebaseRequest(
            Notification("Sizga ${user} follow qildi", "Instagram clone"), token))
            .enqueue(object : Callback<FirebaseResponse> {
                override fun onResponse(
                    call: Call<FirebaseResponse>,
                    response: Response<FirebaseResponse>,
                ) {
                    Log.d("response", response.body().toString())
                }

                override fun onFailure(call: Call<FirebaseResponse>, t: Throwable) {
                    Log.d("failure", t.localizedMessage)
                }

            })
    }

    private fun sendUnFollowNotification(token:ArrayList<String>, user: String) {
        Log.d("tokennn", token.toString())
        apiService.sendNotification(FirebaseRequest(
            Notification("Sizdan ${user} unFollow qildi", "Instagram clone"), token))
            .enqueue(object : Callback<FirebaseResponse> {
                override fun onResponse(
                    call: Call<FirebaseResponse>,
                    response: Response<FirebaseResponse>,
                ) {
                    Log.d("response", response.body().toString())
                }

                override fun onFailure(call: Call<FirebaseResponse>, t: Throwable) {
                    Log.d("failure", t.localizedMessage)
                }

            })
    }

    private fun followUser(uid: String, to: User) {
        DatabaseManager.loadUser(uid, object : DBUserHandler {
            override fun onSuccess(me: User?) {
                DatabaseManager.followUser(me!!, to, object : DBFollowHandler {
                    override fun onSuccess(isFollowed: Boolean) {
                        to.isFollowed = true
                        //Log.d("@@@@", to.device_tokens[0])
                        if (to.device_tokens.size != 0){
                            sendFollowNotification(to.device_tokens, me.fullname)
                        } else {
                            Log.d("deviceToken", to.device_tokens.size.toString())
                        }

                        DatabaseManager.storePostsToMyFeed(uid, to)
                    }

                    override fun onError(e: Exception) {
                    }
                })
            }
            override fun onError(e: Exception) {

            }
        })
    }

    private fun unFollowUser(uid: String, to: User) {
        DatabaseManager.loadUser(uid, object : DBUserHandler {
            override fun onSuccess(me: User?) {
                DatabaseManager.unFollowUser(me!!, to, object : DBFollowHandler {
                    override fun onSuccess(isFollowed: Boolean) {
                        to.isFollowed = false
                        //Log.d("@@@@", to.device_tokens[0])
                        if (to.device_tokens.size != 0){
                            sendUnFollowNotification(to.device_tokens, me.fullname)
                        } else {
                            Log.d("deviceToken", to.device_tokens.size.toString())
                        }
                        DatabaseManager.removePostsFromMyFeed(uid, to)
                    }

                    override fun onError(e: Exception) {

                    }
                })
            }

            override fun onError(e: Exception) {

            }
        })
    }

}