package com.example.instagramappclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramappclone.R
import com.example.instagramappclone.model.Post
import com.example.instagramappclone.model.User
import com.example.instagramappclone.utils.Utils
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso


class ProfileAdapter(var fragment: Fragment, var items: ArrayList<Post>): BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return ProfileViewHolder(view)


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is ProfileViewHolder) {
            holder.apply {
                setViewHeight(ivPost)
                Picasso.get().load(item.postImg).into(ivPost)
                tvCaption.text = item.caption


            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }



    class ProfileViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        val ivPost = view.findViewById<ImageView>(R.id.iv_post)
        val tvCaption = view.findViewById<TextView>(R.id.tv_caption)


    }

    private fun setViewHeight(view: View) {
        val params:ViewGroup.LayoutParams = view.layoutParams
        params.height = Utils.screenSize(fragment.requireActivity().application).width/2
        view.layoutParams = params
    }
}