package com.example.contacts.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.contacts.models.Contact
import com.example.contacts.R
import com.example.contacts.db.MyDBManager
import com.example.contacts.databinding.FragmentCreateNewContactBinding

class CreateNewContactFragment : Fragment() {
    lateinit var bindingClass: FragmentCreateNewContactBinding
    private val editTextList = mutableListOf<EditText>()
    private lateinit var dbManager: MyDBManager
    var countOfTags = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = FragmentCreateNewContactBinding.inflate(layoutInflater)
        dbManager = MyDBManager(requireContext())
        dbManager.openDb()

        bindingClass.btnAddTag.setOnClickListener {
            countOfTags++
            if (countOfTags <= 10) {
                val newET = addEditText(bindingClass.tagContainer)
                editTextList.add(newET)
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_to_much_tags), Toast.LENGTH_SHORT).show()
            }
        }

        var isImportant = 0

        bindingClass.switchIsImpotrant.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) isImportant = 1
            else isImportant = 0
        }

        bindingClass.btnAddContact.setOnClickListener {
            val firstLetter = bindingClass.editTextName.text.run {
                this?.firstOrNull()?.toString() ?: ""
            }
            val name = bindingClass.editTextName.text.toString()
            val telephone = bindingClass.editTextTelephone.text.toString()
            val textArray = editTextList.map { it.text.toString() }.toTypedArray()

            val newContact = Contact(name, firstLetter, telephone, textArray, isImportant)

            dbManager.saveContact(newContact)
            Toast.makeText(requireContext(), "Контакт добавлен", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return bindingClass.root
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }

    private fun addEditText(container: LinearLayout): EditText {
        val newEditText = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 40, 0, 0)
            }
            hint = getString(R.string.tag)
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setHintTextColor(ContextCompat.getColor(context, R.color.white))
            setEms(12)
        }
        container.addView(newEditText)
        return newEditText
    }
}
