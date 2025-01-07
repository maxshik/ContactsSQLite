package com.example.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.contacts.databinding.ActivityMainBinding
import com.example.contacts.fragments.ContactsFragment

class MainActivity : AppCompatActivity() {
    lateinit var bindingClass : ActivityMainBinding
    var telephone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        openFragment(bindingClass.fragmentContainer.id, ContactsFragment(this))
    }

    fun openFragment(container: Int, fragment: Fragment) {
        supportFragmentManager.
        beginTransaction().
        addToBackStack(null).
        replace(container, fragment).
        commit()
    }

    fun makePhoneCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
            telephone = phoneNumber
            callIntent.data = Uri.parse(phoneNumber)
            startActivity(callIntent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val phoneNumber = "tel:${telephone}"
                Log.i("Test", phoneNumber)
                makePhoneCall(phoneNumber)
            } else {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}