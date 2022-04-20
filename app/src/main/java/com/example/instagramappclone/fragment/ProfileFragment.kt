package com.example.instagramappclone.fragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramappclone.R
import com.example.instagramappclone.adapter.ProfileAdapter
import com.example.instagramappclone.managers.*
import com.example.instagramappclone.managers.handler.DBPostsHandler
import com.example.instagramappclone.managers.handler.DBUserHandler
import com.example.instagramappclone.managers.handler.DBUsersHandler
import com.example.instagramappclone.managers.handler.StorageHandler
import com.example.instagramappclone.model.Post
import com.example.instagramappclone.model.User
import com.google.android.material.imageview.ShapeableImageView
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter

class ProfileFragment : BaseFragment() {
    val TAG = ProfileFragment::class.java.simpleName
    lateinit var rv_profile: RecyclerView
    lateinit var tv_fullname: TextView
    lateinit var tv_email: TextView
    lateinit var tv_posts: TextView
    lateinit var iv_profile: ShapeableImageView

    private lateinit var tvFollowing: TextView
    private lateinit var tvFollowers: TextView

    var pickedPhoto: Uri? = null
    var allPhotos = ArrayList<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        rv_profile = view.findViewById(R.id.rv_profile)
        rv_profile.setLayoutManager(GridLayoutManager(activity, 2))
        tv_fullname = view.findViewById(R.id.tv_fullname_profile)
        tv_email = view.findViewById(R.id.tv_email_profile)
        tv_posts = view.findViewById(R.id.tv_posts)
        iv_profile = view.findViewById(R.id.iv_profile_profile)
        tvFollowers = view.findViewById(R.id.tv_followers)
        tvFollowing = view.findViewById(R.id.tv_following)

        val iv_logout = view.findViewById<ImageView>(R.id.iv_log_out)
        iv_logout.setOnClickListener {
            AuthManager.signOut()
            callSignInActivity(requireActivity())
        }
        iv_profile.setOnClickListener {
            pickFishBunPhoto()
        }

        loadUserInfo()
        loadMyPosts()
        loadMyFollowing()
        loadMyFollowers()
    }

    private fun loadMyFollowers() {
        val uid = AuthManager.currentUser()!!.uid
        DatabaseManager.loadFollowers(uid, object : DBUsersHandler {
            override fun onSuccess(users: ArrayList<User>) {
                tvFollowers.text = users.size.toString()
            }

            override fun onError(e: Exception) {

            }
        })

    }

    private fun loadMyFollowing() {
        val uid = AuthManager.currentUser()!!.uid
        DatabaseManager.loadFollowing(uid, object : DBUsersHandler {
            override fun onSuccess(users: ArrayList<User>) {
                tvFollowing.text = users.size.toString()
            }

            override fun onError(e: Exception) {

            }

        })
    }


    private fun loadMyPosts(){
        val uid = AuthManager.currentUser()!!.uid
        DatabaseManager.loadPosts(uid, object: DBPostsHandler {
            override fun onSuccess(posts: ArrayList<Post>) {
                tv_posts.text = posts.size.toString()
                refreshAdapter(posts)
            }

            override fun onError(e: Exception) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun loadUserInfo() {
        DatabaseManager.loadUser(AuthManager.currentUser()!!.uid, object : DBUserHandler {
            override fun onSuccess(user: User?) {
                if (user != null) {
                    showUserInfo(user)
                }
            }

            override fun onError(e: Exception) {

            }
        })
    }

    private fun uploadUserPhoto() {
        if (pickedPhoto == null) return
        StorageManager.uploadUserPhoto(pickedPhoto!!, object : StorageHandler {
            override fun onSuccess(imgUrl: String) {
                DatabaseManager.updateUserImage(imgUrl)
                iv_profile.setImageURI(pickedPhoto)
            }

            override fun onError(exception: Exception?) {

            }
        })
    }

    private fun showUserInfo(user: User){
        tv_fullname.text = user.fullname
        tv_email.text = user.email
        Glide.with(this).load(user.userImg)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_user)
            .into(iv_profile)
    }

    /**
     * Pick photo using FishBun library
     */
    private fun pickFishBunPhoto() {
        FishBun.with(this)
            .setImageAdapter(GlideAdapter())
            .setMaxCount(1)
            .setMinCount(1)
            .setSelectedImages(allPhotos)
            .startAlbumWithActivityResultCallback(photoLauncher)
    }

    private val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            allPhotos =
                it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf()
            pickedPhoto = allPhotos.get(0)
            uploadUserPhoto()
        }
    }

    private fun refreshAdapter(items: ArrayList<Post>) {
        val adapter = ProfileAdapter(this, items)
        rv_profile.adapter = adapter
    }
}
//class ProfileFragment : BaseFragment() {
//    val TAG = ProfileFragment::class.java.simpleName
//
//    lateinit var rvProfile: RecyclerView
//
//    lateinit var ivProfile: ShapeableImageView
//    lateinit var tvFullname: TextView
//    lateinit var tvEmail: TextView
//    private lateinit var tvPosts: TextView
//
//
//    var pickedPhoto: Uri? = null
//    var allPhotos = ArrayList<Uri>()
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View? {
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_profile, container, false)
//        initViews(view)
//
//
//        return view
//    }
//
//    private fun initViews(view: View) {
//        tvPosts = view.findViewById(R.id.tv_posts)
//        rvProfile = view.findViewById(R.id.rv_profile)
//        rvProfile.layoutManager = GridLayoutManager(activity, 2)
//
//        val btnLogOut = view.findViewById<ImageView>(R.id.iv_log_out)
//        btnLogOut.setOnClickListener {
//            logOut()
//            callSignInActivity(requireActivity())
//        }
//
//        tvFullname = view.findViewById(R.id.tv_fullname_profile)
//        tvEmail = view.findViewById(R.id.tv_email_profile)
//
//        ivProfile = view.findViewById(R.id.iv_profile_profile)
//        ivProfile.setOnClickListener {
//            pickFishBunPhoto()
//        }
//
//        loadUserInfo()
//        loadMyposts()
//
//    }
//
//    private fun loadMyposts() {
//        val uid = AuthManager.currentUser()!!.uid
//        DatabaseManager.loadPosts(uid, object : DBPostsHandler {
//            override fun onSuccess(posts: ArrayList<Post>) {
//                tvPosts.text = posts.size.toString()
//                refreshAdapter(posts)
//            }
//
//            override fun onError(e: Exception) {
//                TODO("Not yet implemented")
//            }
//        })
//    }
//
//    fun logOut() {
//        AuthManager.signOut()
//    }
//
//    private fun pickFishBunPhoto() {
//        FishBun.with(this)
//            .setImageAdapter(GlideAdapter())
//            .setMaxCount(1)
//            .setMinCount(1)
//            .setSelectedImages(allPhotos)
//            .startAlbumWithActivityResultCallback(activityResultLauncher = photoLauncher)
//    }
//
//    private val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//        if (it.resultCode == Activity.RESULT_OK) {
//            allPhotos = it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf()
//            pickedPhoto = allPhotos[0]
//            uploadPickedPhoto()
//        }
//    }
//
//    private fun uploadPickedPhoto() {
//        if (pickedPhoto != null) return
//        StorageManager.uploadUserPhoto(pickedPhoto!!, object : StorageHandler {
//            override fun onSuccess(imgUrl: String) {
//                DatabaseManager.updateUserImage(imgUrl)
//                ivProfile.setImageURI(pickedPhoto)
//
//
//            }
//
//            override fun onError(exception: Exception?) {
//                Log.d("dddd", exception.toString())
//            }
//
//        })
//    }
//
//    fun loadUserInfo() {
//        DatabaseManager.loadUser(AuthManager.currentUser()!!.uid, object : DBuserHandler {
//            override fun onSuccess(user: User?) {
//                if (user != null) {
//                    showUserInfo(user)
//                }
//            }
//
//            override fun onError(e: Exception) {
//
//            }
//        })
//    }
//
//    private fun showUserInfo(user: User) {
//        tvFullname.text = user.fullname
//        tvEmail.text = user.email
//        Glide.with(this).load(user.userImg)
//            .placeholder(R.drawable.ic_user)
//            .error(R.drawable.ic_user)
//            .into(ivProfile)
//
//    }
//
//
//    private fun refreshAdapter(items: ArrayList<Post>) {
//        val adapter = ProfileAdapter(this, items)
//        rvProfile.adapter = adapter
//
//    }
//
////    private fun loadPosts(): ArrayList<Post> {
////        val list = ArrayList<Post>()
////        for (i in 0..6) {
////            list.add(Post("haa", "https://media.istockphoto.com/photos/dandelions-with-wind-in-field-seeds-blowing-away-blue-sky-picture-id1138405619?b=1&k=20&m=1138405619&s=170667a&w=0&h=MINGv3_A8EBUzXT1llp3k9qCUTUFd_DOg_WFToD2qVE="))
//////            list.add(Post("https://images.unsplash.com/photo-1588650944142-c9160fd45802?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Nnx8YmVhdXRpZnVsJTIwaW1hZ2VzfGVufDB8MnwwfHw%3D&auto=format&fit=crop&w=500&q=60"))
//////            list.add(Post("https://images.unsplash.com/photo-1649142583534-ffa36e8823e2?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHx0b3BpYy1mZWVkfDIwfGJvOGpRS1RhRTBZfHxlbnwwfHx8fA%3D%3D&auto=format&fit=crop&w=500&q=60"))
////        }
////        return list
////
////    }
//
//
//}