package com.example.instagramappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.instagramappclone.activity.BaseActivity
import com.example.instagramappclone.adapter.ViewPagerAdapter
import com.example.instagramappclone.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Contains view pager with 5 fragments in MainActivity,
 * and pages can be controlled by BottomNavigationView
 */
class MainActivity : BaseActivity(), UploadFragment.UploadListener,HomeFragment.HomeListener {
    val TAG = MainActivity::class.java.simpleName
    var index = 0
    lateinit var homeFragment: HomeFragment
    lateinit var uploadFragment: UploadFragment
    lateinit var viewPager: ViewPager
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_home -> {
                    viewPager.setCurrentItem(0)
                }
                R.id.navigation_search -> {
                    viewPager.setCurrentItem(1)
                }
                R.id.navigation_upload -> {
                    viewPager.setCurrentItem(2)
                }
                R.id.navigation_favorite -> {
                    viewPager.setCurrentItem(3)
                }
                R.id.navigation_profile -> {
                    viewPager.setCurrentItem(4)
                }
            }
            true
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {

            }

            override fun onPageSelected(position: Int) {
                index = position
                bottomNavigationView.menu.getItem(index).setChecked(true)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        // Home and Upload Fragments are global for communication purpose
        homeFragment = HomeFragment()
        uploadFragment = UploadFragment()

        setupViewPager(viewPager)


    }

    private fun setupViewPager(viewPager: ViewPager?) {

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(homeFragment)
        adapter.addFragment(SearchFragment())
        adapter.addFragment(uploadFragment)
        adapter.addFragment(FavoriteFragment())
        adapter.addFragment(ProfileFragment())

        viewPager!!.adapter = adapter

    }

    override fun scrollToHome() {
        index = 0
        scrollByIndex(index)
    }

    private fun scrollByIndex(index: Int) {
        viewPager.setCurrentItem(index)
        bottomNavigationView.menu.getItem(index).setChecked(true)

    }

    override fun scrollToUpload() {
        index = 2
        scrollByIndex(index)
    }


}