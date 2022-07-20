package com.woohoo.schedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import android.widget.*
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Bottom navigation bar
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_setting).build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(navView, navController)

        //Create a new list button FAB definition that may be changed over time
        val fab = findViewById<ImageView>(R.id.add_main)
        fab.setOnClickListener()
        {
            //Page jump
            val intent = Intent(this, AddListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.trans_in, R.anim.no_anim)
        }
    }
}