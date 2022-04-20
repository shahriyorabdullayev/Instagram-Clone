package com.example.instagramappclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramappclone.R
import com.example.instagramappclone.fragment.SearchFragment
import com.example.instagramappclone.model.User
import com.google.android.material.imageview.ShapeableImageView


class SearchAdapter(var fragment: SearchFragment, var items: ArrayList<User>): BaseAdapter() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_search, parent, false)
        return SearchViewHolder(view)


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is SearchViewHolder) {
            holder.apply {
                fullname.text = item.fullname
                tvEmail.text = item.email
                tvFollow.setOnClickListener {
                    if (!item.isFollowed) {
                        tvFollow.text = fragment.getString(R.string.str_following)

                    } else {
                        tvFollow.text = fragment.getString(R.string.str_follow)

                    }
                    fragment.followOrUnfollow(item)

                }


                if (!item.isFollowed) {
                    tvFollow.text = fragment.getString(R.string.str_follow)
                } else {
                    tvFollow.text = fragment.getString(R.string.str_following)
                }

            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }



    class SearchViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        val fullname = view.findViewById<TextView>(R.id.tv_fullname)
        val tvEmail = view.findViewById<TextView>(R.id.tv_email)
        val tvFollow = view.findViewById<TextView>(R.id.tv_follow)
        val ivProfile = view.findViewById<ShapeableImageView>(R.id.iv_profile)

    }
}