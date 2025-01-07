package com.example.contacts.fragments

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.MainActivity
import com.example.contacts.R
import com.example.contacts.adapters.ContactsAdapter
import com.example.contacts.databinding.FragmentContactsBinding
import com.example.contacts.db.MyDBManager
import kotlin.math.abs

class ContactsFragment(private val context: Context) : Fragment() {
    private lateinit var adapter: ContactsAdapter
    private lateinit var bindingClass: FragmentContactsBinding
    private lateinit var dbManager: MyDBManager
    private var flagImportantContacts = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dbManager = MyDBManager(context)
        dbManager.openDb()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentContactsBinding.inflate(layoutInflater)
        val contacts = dbManager.getContacts()
        adapter = ContactsAdapter(context, contacts, activity as MainActivity)
        init()
        (activity as AppCompatActivity?)?.supportActionBar!!.title = getString(R.string.contact_list)
        (activity as AppCompatActivity?)?.supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF6200EE")))

        bindingClass.overlayImage.setOnClickListener {
            (activity as MainActivity?)?.openFragment(bindingClass.frgEnterNumber.id, EnterPhoneNumberFragment())
        }

        return bindingClass.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun init() {
        bindingClass.apply {
            rcViewOfContacts.layoutManager = LinearLayoutManager(context)
            rcViewOfContacts.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addContact -> {
                (activity as? MainActivity)?.openFragment(R.id.fragmentContainer, CreateNewContactFragment())
                return true
            }
            R.id.loveContacts -> {
                if (!flagImportantContacts) {
                    val contacts = dbManager.getContacts(true)
                    item.setIcon(android.R.drawable.btn_star_big_on)
                    adapter = ContactsAdapter(context, contacts, activity as MainActivity)
                    init()
                    flagImportantContacts = true
                } else {
                    val contacts = dbManager.getContacts()
                    item.setIcon(android.R.drawable.btn_star_big_off)
                    adapter = ContactsAdapter(context, contacts, activity as MainActivity)
                    init()
                    flagImportantContacts = false
                }
                return true
            }
            R.id.history -> {
                (activity as? MainActivity)?.openFragment(R.id.fragmentContainer, PrevCallsFragment())
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }
}