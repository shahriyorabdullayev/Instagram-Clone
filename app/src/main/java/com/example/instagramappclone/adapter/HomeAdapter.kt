package com.example.instagramappclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramappclone.R
import com.example.instagramappclone.fragment.FavoriteFragment
import com.example.instagramappclone.fragment.HomeFragment
import com.example.instagramappclone.managers.AuthManager
import com.example.instagramappclone.model.Post
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class HomeAdapter(var fragment: HomeFragment, var items: ArrayList<Post>): BaseAdapter() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = items[position]

        if (holder is PostViewHolder) {
            holder.apply {
                //Glide.with(fragment).load(post.image).into(ivPost)
                if(fragment is HomeFragment){
                    Picasso.get().load(post.postImg).placeholder(R.drawable.lottie2).into(ivPost)
                    tvFullname.text = post.fullname
                    ivLike.setOnClickListener {
                        if (post.isLiked){
                            post.isLiked = false
                            ivLike.setImageResource(R.drawable.ic_love)
                        } else {
                            post.isLiked = true
                            ivLike.setImageResource(R.drawable.ic_love_red)
                        }
                        fragment.likeOrUnlikePost(post)
                    }
                    if (post.isLiked) {
                        ivLike.setImageResource(R.drawable.ic_love_red)
                    }else {
                        ivLike.setImageResource(R.drawable.ic_love)
                    }

                    val uid = AuthManager.currentUser()!!.uid
                    if (uid == post.uid){
                        ivMore.visibility = View.VISIBLE
                    }else {
                        ivMore.visibility = View.GONE
                    }
                    ivMore.setOnClickListener {
                        fragment.showDeleteDialog(post)
                    }

                }

            }
        }
    }

    class PostViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        val ivPost = view.findViewById<ImageView>(R.id.iv_photo)
        val ivProfile = view.findViewById<ShapeableImageView>(R.id.iv_profile)
        val tvFullname = view.findViewById<TextView>(R.id.tv_name)
        val tvTime = view.findViewById<TextView>(R.id.tv_time)
        val ivMore = view.findViewById<ImageView>(R.id.iv_more)
        val ivLike = view.findViewById<ImageView>(R.id.iv_like)
        val ivShare = view.findViewById<ImageView>(R.id.iv_share)

    }
}