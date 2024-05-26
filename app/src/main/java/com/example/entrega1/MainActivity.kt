package com.example.entrega1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val mi: MenuInflater = menuInflater;
        mi.inflate(R.menu.menu_ppal, menu);
        return true; // true indica que debe visualizarse
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== R.id.menuAyuda){
            val i = Intent(this, HelpActivity::class.java)
            startActivity(i)
        }
        return true;
    }


    fun openGame(v: View){
        val i = Intent(this, GameActivity::class.java)
        val category = v.tag.toString()
        i.putExtra("category", category)
        startActivity(i)
    }

}