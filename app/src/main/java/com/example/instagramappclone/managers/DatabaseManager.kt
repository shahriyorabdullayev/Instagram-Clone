package com.example.instagramappclone.managers

import com.example.instagramappclone.managers.handler.*
import com.example.instagramappclone.model.Post
import com.example.instagramappclone.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

private var USER_PATH = "users"
private var POST_PATH = "posts"
private var FEED_PATH = "feeds"
private var FOLLOWING_PATH = "following"
private var FOLLOWERS_PATH = "followers"

object DatabaseManager {


    private var database = FirebaseFirestore.getInstance()

    fun storeUser(user: User, handler: DBUserHandler) {
        database.collection(USER_PATH).document(user.uid).set(user).addOnSuccessListener {
            handler.onSuccess()
        }.addOnFailureListener {
            handler.onError(it)
        }
    }

    fun loadUser(uid: String, handler: DBUserHandler) {
        database.collection(USER_PATH).document(uid).get().addOnSuccessListener {
            if (it.exists()){
                val fullname: String? = it.getString("fullname")
                val email: String? = it.getString("email")
                var device_tokens: ArrayList<String>? = it.get("device_tokens") as ArrayList<String>?

                if (device_tokens == null) {
                    device_tokens = ArrayList()
                }
                val user = User(fullname!!, email!!, device_tokens!!)
                user.uid = uid
                handler.onSuccess(user)
            }else {
                handler.onSuccess(null)
            }
        }.addOnFailureListener {
            handler.onError(it)
        }
    }

    fun updateUserImage(userImg: String) {
        val uid = AuthManager.currentUser()!!.uid
        database.collection(USER_PATH).document(uid).update("userImg", userImg)
    }


    fun loadUsers(handler: DBUsersHandler) {
        database.collection(USER_PATH).get().addOnCompleteListener {
            val users = ArrayList<User>()
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    val uid = document.getString("uid")
                    val fullname = document.getString("fullname")
                    val email = document.getString("email")
                    var device_tokens: ArrayList<String>? = document.get("device_tokens") as ArrayList<String>?
                    if (device_tokens == null) {
                        device_tokens = ArrayList()
                    }
                    val user = User(fullname!!, email!!, device_tokens)
                    user.uid = uid!!
                    users.add(user)
                }
                handler.onSuccess(users)
            }else {
                handler.onError(it.exception!!)
            }
        }
    }

    fun storePost(post: Post, handler: DBPostHandler) {
        val reference = database.collection(USER_PATH).document(post.uid).collection(POST_PATH)
        val id = reference.document().id
        post.id = id

        reference.document(post.id).set(post).addOnSuccessListener {
            handler.onSuccess(post)
        }.addOnFailureListener {
            handler.onError(it)
        }
    }

    fun storeFeeds(post: Post, handler: DBPostHandler) {
        val reference = database.collection(USER_PATH).document(post.uid).collection(FEED_PATH)

        reference.document(post.id).set(post).addOnSuccessListener {
            handler.onSuccess(post)
        }.addOnFailureListener {
            handler.onError(it)
        }
    }

    fun loadPosts(uid: String, handler: DBPostsHandler) {
        val reference = database.collection(USER_PATH).document(uid).collection(POST_PATH)
        reference.get().addOnCompleteListener {
            val posts = ArrayList<Post>()
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    val id = document.getString("id")
                    val caption = document.getString("caption")
                    val postImg = document.getString("postImg")
                    val fullname = document.getString("fullname")
                    val userImg = document.getString("userImg")

                    val post = Post(id!!, caption!!, postImg!!)
                    post.uid = uid
                    post.fullname = fullname!!
                    post.userImg = userImg!!
                    posts.add(post)
                }
                handler.onSuccess(posts)
            } else {
                handler.onError(it.exception!!)
            }
        }
    }

    fun loadFeeds(uid: String, handler: DBPostsHandler) {
        val reference = database.collection(USER_PATH).document(uid).collection(FEED_PATH)
        reference.get().addOnCompleteListener {
            val posts = ArrayList<Post>()
            if (it.isSuccessful) {
                for (document in it.result) {
                    val id = document.getString("id")
                    val caption = document.getString("caption")
                    val postImg = document.getString("postImg")
                    val fullname = document.getString("fullname")
                    val userImg = document.getString("userImg")

                    var isLiked = document.getBoolean("isLiked")
                    if (isLiked == null) isLiked = false

                    val post = Post(id!!, caption!!, postImg!!)
                    post.uid = uid
                    post.fullname = fullname!!
                    post.userImg = userImg!!
                    post.isLiked = isLiked
                    posts.add(post)
                }
                handler.onSuccess(posts)
            }else {
                handler.onError(it.exception!!)
            }
        }
    }

    fun followUser(me: User, to: User, handler: DBFollowHandler) {
        // User(To) is in my following
        database.collection(USER_PATH).document(me.uid).collection(FOLLOWING_PATH).document(to.uid)
            .set(to).addOnSuccessListener {
                //User (Me) is in his/her followers
                database.collection(USER_PATH).document(to.uid).collection(FOLLOWERS_PATH)
                    .document(me.uid)
                    .set(me).addOnSuccessListener {
                        handler.onSuccess(true)
                    }.addOnFailureListener {
                        handler.onError(it)
                    }
            }.addOnFailureListener {
                handler.onError(it)
            }
    }

    fun unFollowUser(me: User, to: User, handler: DBFollowHandler) {
        // User(To) is in my following
        database.collection(USER_PATH).document(me.uid).collection(FOLLOWING_PATH).document(to.uid)
            .delete().addOnSuccessListener {
                //User (Me) is in his/her followers
                database.collection(USER_PATH).document(to.uid).collection(FOLLOWERS_PATH)
                    .document(me.uid)
                    .delete().addOnSuccessListener {
                        handler.onSuccess(true)
                    }.addOnFailureListener {
                        handler.onError(it)
                    }
            }.addOnFailureListener {
                handler.onError(it)
            }
    }

    fun loadFollowing(uid: String, handler: DBUsersHandler ){
        database.collection(USER_PATH).document(uid).collection(FOLLOWING_PATH).get().addOnCompleteListener {
            val users = ArrayList<User>()
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    val uid = document.getString("uid")
                    val fullname = document.getString("fullname")
                    val email = document.getString("email")
                    val userImg = document.getString("userImg")
                    val user = User(fullname!!, email!!, "", userImg!!)
                    user.uid = uid!!
                    users.add(user)
                }
                handler.onSuccess(users)
            } else {
                handler.onError(it.exception!!)
            }
        }
    }

    fun loadFollowers(uid: String, handler: DBUsersHandler) {
        database.collection(USER_PATH).document(uid).collection(FOLLOWERS_PATH).get()
            .addOnCompleteListener {
                val users = ArrayList<User>()
                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        val uid = document.getString("uid")
                        val fullname = document.getString("fullname")
                        val email = document.getString("email")
                        val userImg = document.getString("userImg")
                        val user = User(fullname!!, email!!,"", userImg!!)
                        user.uid = uid!!
                        users.add(user)
                    }
                    handler.onSuccess(users)
                } else {
                    handler.onError(it.exception!!)
                }
            }
    }

    fun storePostsToMyFeed(uid: String, to: User) {
        loadPosts(to.uid, object : DBPostsHandler {
            override fun onSuccess(posts: ArrayList<Post>) {
                for (post in posts) {
                    storeFeed(uid, post)
                }
            }

            override fun onError(e: Exception) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun storeFeed(uid: String, post: Post) {
        val refrence = database.collection(USER_PATH).document(uid).collection(FEED_PATH)
        refrence.document(post.id).set(post)
    }

    fun removePostsFromMyFeed(uid: String, to: User) {
        loadPosts(to.uid, object : DBPostsHandler {
            override fun onSuccess(posts: ArrayList<Post>) {
                for(post in posts) {
                    removeFeed(uid, post)
                }
            }

            override fun onError(e: Exception) {

            }
        })

    }

    private fun removeFeed(uid: String, post: Post) {
        val reference = database.collection(USER_PATH).document(uid).collection(FEED_PATH)
        reference.document(post.id).delete()

    }

    fun likeFeedPost(uid: String, post: Post) {
        database.collection(USER_PATH).document(uid).collection(FEED_PATH).document(post.uid)
            .update("isLiked", post.isLiked)
        if (uid == post.uid) {
            database.collection(USER_PATH).document(uid).collection(POST_PATH).document(post.id)
                .update("isLiked", post.isLiked)
        }

    }

    fun loadLikedFeeds(uid: String, handler: DBPostsHandler) {
        val reference = database.collection(USER_PATH).document(uid).collection(FEED_PATH)
            .whereEqualTo("isLiked", true)
        reference.get().addOnCompleteListener {
            val posts = ArrayList<Post>()
            if (it.isSuccessful) {
                for (document in it.result) {
                    val id = document.getString("id")
                    val caption = document.getString("caption")
                    val postImg = document.getString("postImg")
                    val fullname = document.getString("fullname")
                    val userImg = document.getString("userImg")

                    var isLiked = document.getBoolean("isLiked")
                    if (isLiked == null) isLiked = false

                    val post = Post(id!!, caption!!, postImg!!)
                    post.uid = uid
                    post.fullname = fullname!!
                    post.userImg = userImg!!
                    posts.add(post)
                }
                handler.onSuccess(posts)
            }else {
                handler.onError(it.exception!!)
            }
        }
    }

    fun addMyDeviceToken(uid: String, deviceToken: String?, handler: DBUserHandler) {
        val reference = database.collection(USER_PATH).document(uid)
        reference.update("device_tokens", FieldValue.arrayUnion(deviceToken)).addOnSuccessListener {
            handler.onSuccess(null)
        }.addOnFailureListener {
            handler.onError(it)
        }

    }


}