package com.example.instagramappclone.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramappclone.R
import com.example.instagramappclone.adapter.HomeAdapter
import com.example.instagramappclone.managers.AuthManager
import com.example.instagramappclone.managers.DatabaseManager
import com.example.instagramappclone.managers.handler.DBPostsHandler
import com.example.instagramappclone.model.Post
import java.lang.RuntimeException
import java.util.concurrent.RecursiveTask


class HomeFragment : BaseFragment() {

    val TAG = HomeFragment::class.java.simpleName
    private var homeListener: HomeListener? = null



    private lateinit var ivCamera: ImageView

    private lateinit var rvHome: RecyclerView
    private lateinit var homeAdapter: HomeAdapter
    private var items: ArrayList<Post> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        ivCamera = view.findViewById(R.id.iv_camera)
        rvHome = view.findViewById(R.id.rv_home)



        ivCamera.setOnClickListener {
            homeListener!!.scrollToUpload()
        }

        loadMyFeeds()


    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser && items.size > 0) {
            loadMyFeeds()
        }
    }



    private fun loadMyFeeds() {
        showLoading(requireActivity())
        val uid = AuthManager.currentUser()!!.uid
        DatabaseManager.loadFeeds(uid, object : DBPostsHandler {
            override fun onSuccess(posts: ArrayList<Post>) {
                dismissLoading()
                items.clear()
                items.addAll(posts)
                refreshAdapter(items)

            }

            override fun onError(e: Exception) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun refreshAdapter(items: ArrayList<Post>) {
        val adapter = HomeAdapter(this, items)
        rvHome.adapter = adapter
    }


    /**
     * onAttach is for communication of Fragments
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeListener = if (context is HomeListener){
            context
        }else {
            throw RuntimeException("$context must implementation HomeListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        homeListener = null
    }

    fun likeOrUnlikePost(post: Post) {
        val uid = AuthManager.currentUser()!!.uid
        DatabaseManager.likeFeedPost(uid, post)

    }

    fun showDeleteDialog(post: Post) {


    }

    /**
     * This interface is created for communication Fragment
     */
    interface HomeListener {
        fun scrollToUpload()
    }


}