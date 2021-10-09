package com.mohamed.abdo.myhealth.ui.about

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import com.mohamed.abdo.myhealth.R
import com.mohamed.abdo.myhealth.databinding.ActivityAboutBinding
import com.mohamed.abdo.myhealth.ui.chat.ChatActivity
import com.mohamed.abdo.myhealth.ui.main.MainActivity

class AboutActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAboutBinding
    // drawer var
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this ,R.layout.activity_about )
        drawerNavigationBar()


    }

    private fun drawerNavigationBar() {
        toggle = ActionBarDrawerToggle(
            this@AboutActivity,
            binding.drawer,
            binding.toolbar,
            R.string.open,
            R.string.close)
        binding.drawer.addDrawerListener(toggle)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_drawer);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        binding.navViewAbout.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_home ->{
                    val intent = Intent(this@AboutActivity, MainActivity::class.java)
                    startActivity(intent)
                    this@AboutActivity.finish()
                    true
                }
                R.id.menu_about ->{
                val intent = Intent(this@AboutActivity,AboutActivity::class.java)
                startActivity(intent)
                 this@AboutActivity.finish()
                true
                }
                R.id.menu_chat ->{
                    val intent = Intent(this@AboutActivity,ChatActivity::class.java)
                    startActivity(intent)
                    true
                }
                else ->{
                    false
                }
            }

        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
        {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}