package com.example.instagramappclone.fragment

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.instagramappclone.R
import com.example.instagramappclone.managers.*
import com.example.instagramappclone.managers.handler.DBPostHandler
import com.example.instagramappclone.managers.handler.DBUserHandler
import com.example.instagramappclone.managers.handler.StorageHandler
import com.example.instagramappclone.model.Post
import com.example.instagramappclone.model.User
import com.example.instagramappclone.utils.Logger
import com.example.instagramappclone.utils.Utils
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter


/**
 * In UploadFragment, user can upload
 * a post with photo and caption
 */

class UploadFragment : BaseFragment() {
    val TAG = UploadFragment::class.java.simpleName
    private var uploadListener: UploadListener? = null

    private lateinit var flPhoto: FrameLayout
    private lateinit var ivPhoto: ImageView
    private lateinit var etCaption: EditText

    var pickedPhoto: Uri? = null
    var allPhotos = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickedPhoto = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_upload, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        val flView = view.findViewById<FrameLayout>(R.id.fl_view)
        setViewHeight(flView)
        flPhoto = view.findViewById(R.id.fl_photo)
        ivPhoto = view.findViewById(R.id.iv_photo)
        etCaption = view.findViewById(R.id.et_caption)
        val ivClose = view.findViewById<ImageView>(R.id.iv_close)
        val ivPick = view.findViewById<ImageView>(R.id.iv_pick)
        val ivUpload = view.findViewById<ImageView>(R.id.iv_upload)

        ivPick.setOnClickListener {
            pickFishBunPhoto()
        }

        ivClose.setOnClickListener {
            hidePickedPhoto()
        }

        ivUpload.setOnClickListener {
            uploadPostPhoto("hello", pickedPhoto!!)
        }

    }

    private fun uploadNewPost() {
        uploadListener!!.scrollToHome()
        val caption = etCaption.text.toString().trim()
        if (caption.isNotEmpty() && pickedPhoto != null) {
            Logger.d(TAG, caption)
            Logger.d(TAG, pickedPhoto!!.path.toString())
            resetAll()
        }
    }


    private fun uploadPostPhoto(caption: String, uri: Uri) {
        showLoading(requireActivity())
        StorageManager.uploadPostPhoto(uri, object : StorageHandler {
            override fun onSuccess(imgUrl: String) {
                val post = Post(caption, imgUrl)
                val uid = AuthManager.currentUser()!!.uid

                DatabaseManager.loadUser(uid, object : DBUserHandler {
                    override fun onSuccess(user: User?) {
                        post.uid = uid
                        post.fullname = user!!.fullname
                        post.userImg = user.userImg
                        storePostToDB(post)
                    }

                    override fun onError(e: Exception) {

                    }
                })
            }

            override fun onError(exception: Exception?) {

            }
        })
    }

    private fun storePostToDB(post: Post) {
        DatabaseManager.storePost(post, object : DBPostHandler {
            override fun onSuccess(post: Post) {
                storeFeedToDb(post)
            }

            override fun onError(e: Exception) {
                dismissLoading()
            }
        })

    }

    private fun storeFeedToDb(post: Post) {
        DatabaseManager.storeFeeds(post, object : DBPostHandler {
            override fun onSuccess(post: Post) {
                dismissLoading()
                resetAll()
                uploadListener!!.scrollToHome()
            }

            override fun onError(e: Exception) {
                dismissLoading()
            }
        })
    }

    private fun resetAll() {
        allPhotos.clear()
        etCaption.text.clear()
        pickedPhoto = null
        flPhoto.visibility = View.GONE
    }

    private fun hidePickedPhoto() {
        pickedPhoto = null
        flPhoto.visibility = View.GONE
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

    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                allPhotos =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf()
                pickedPhoto = allPhotos[0]
                showPickedPhoto()
            }
        }

    private fun showPickedPhoto() {
        flPhoto.visibility = View.VISIBLE
        ivPhoto.setImageURI(pickedPhoto)
    }

    /**
     * Set view as Height
     */
    private fun setViewHeight(flView: FrameLayout) {
        val params: ViewGroup.LayoutParams = flView.layoutParams
        params.height = Utils.screenSize(requireActivity().application).height
        flView.layoutParams = params
    }

    /**
     * onAttach is for communication of Fragments
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        uploadListener = if (context is UploadListener) {
            context
        } else {
            throw RuntimeException("$context must implement UploadListener")
        }
    }

    /**
     * onDetach is for communication of fragments
     */
    override fun onDetach() {
        super.onDetach()
        uploadListener = null
    }

    /**
     * This interface is created for communication with HomeFragment
     */
    interface UploadListener {
        fun scrollToHome()
    }


}