package com.example.contacts.adapters

import android.content.Context
import android.graphics.Color
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.MainActivity
import com.example.contacts.models.Contact
import com.example.contacts.R
import com.example.contacts.databinding.ContactCardBinding
import com.example.contacts.db.MyDBManager
import com.example.contacts.fragments.ContactDetailsFragment
import kotlin.random.Random

class ContactsAdapter(
    private val context: Context,
    private val contacts: MutableList<Contact>,
    private val activity: MainActivity
) : RecyclerView.Adapter<ContactsAdapter.ContactHolder>() {

    private lateinit var dbManager: MyDBManager

    class ContactHolder(item: View) : RecyclerView.ViewHolder(item), View.OnCreateContextMenuListener {
        init {
            item.setOnCreateContextMenuListener(this)
        }

        val binding = ContactCardBinding.bind(item)

        fun bind(contact: Contact, context: Context) = with(binding) {
            contactName.text = contact.name
            imageTextView.text = contact.firstLatter

            val colors = context.resources.getStringArray(R.array.bgColors)
            val randomColor = colors[Random.nextInt(colors.size)]
            imageTextView.setBackgroundColor(Color.parseColor(randomColor))
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            val inflater = MenuInflater(v.context)
            inflater.inflate(R.menu.context_menu, menu)
        }
    }

    fun updateContacts(newContacts: List<Contact>) {
        contacts.clear()
        contacts.addAll(newContacts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_card, parent, false)
        return ContactHolder(view)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val contact = contacts[position]
        dbManager = MyDBManager(context)
        dbManager.openDb()

        holder.itemView.setOnClickListener {
            val tagsList = contact.tags.toCollection(ArrayList())
            activity.openFragment(
                R.id.fragmentContainer,
                ContactDetailsFragment.newInstance(
                    contact.name, contact.telephone,
                    tagsList, contact.firstLatter,
                    contact.isImportant
                )
            )
        }

        holder.itemView.setOnCreateContextMenuListener { menu, _, _ ->
            val inflater = MenuInflater(context)
            inflater.inflate(R.menu.context_menu, menu)
            menu.findItem(R.id.menu_delete)?.setOnMenuItemClickListener {
                dbManager.deleteContact(contact.name)
                val newContacts = dbManager.getContacts()
                updateContacts(newContacts)
                true
            }
        }

        holder.bind(contact, context)
    }
}