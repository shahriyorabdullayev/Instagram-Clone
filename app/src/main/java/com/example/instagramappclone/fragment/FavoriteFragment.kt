package com.example.instagramappclone.fragment

import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramappclone.R
import com.example.instagramappclone.adapter.HomeAdapter
import com.example.instagramappclone.managers.AuthManager
import com.example.instagramappclone.managers.DatabaseManager
import com.example.instagramappclone.managers.handler.DBPostsHandler
import com.example.instagramappclone.model.Post

class FavoriteFragment : BaseFragment() {

    private lateinit var rvFavorite: RecyclerView
    private lateinit var favoriteAdapter: HomeAdapter
    private lateinit var favorites: ArrayList<Post>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        initViews(view)


        return view
    }

    private fun initViews(view: View) {
        rvFavorite = view.findViewById(R.id.rv_favorite)
        favorites = allFavorites()
//        favoriteAdapter = HomeAdapter(this, favorites)
//        rvFavorite.adapter = favoriteAdapter
    }

    fun loadLikedFeeds() {
        showLoading(requireActivity())
        val uid = AuthManager.currentUser()!!.uid
        DatabaseManager.loadLikedFeeds(uid, object : DBPostsHandler {
            override fun onSuccess(posts: ArrayList<Post>) {
                dismissLoading()

            }

            override fun onError(e: Exception) {
                TODO("Not yet implemented")
            }

        })
    }

    fun refreshAdapter(posts: ArrayList<Post>) {

    }

    private fun allFavorites(): ArrayList<Post> {
        val list = ArrayList<Post>()
        for (i in 0..6) {
            list.add(Post("salom","https://media.istockphoto.com/photos/dandelions-with-wind-in-field-seeds-blowing-away-blue-sky-picture-id1138405619?b=1&k=20&m=1138405619&s=170667a&w=0&h=MINGv3_A8EBUzXT1llp3k9qCUTUFd_DOg_WFToD2qVE="))
//            list.add(Post("https://images.unsplash.com/photo-1588650944142-c9160fd45802?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Nnx8YmVhdXRpZnVsJTIwaW1hZ2VzfGVufDB8MnwwfHw%3D&auto=format&fit=crop&w=500&q=60"))
//            list.add(Post("https://images.unsplash.com/photo-1649142583534-ffa36e8823e2?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHx0b3BpYy1mZWVkfDIwfGJvOGpRS1RhRTBZfHxlbnwwfHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60"))
        }
        return list
    }


}