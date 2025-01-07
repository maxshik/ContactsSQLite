package com.example.contacts.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.contacts.MainActivity
import com.example.contacts.R
import com.example.contacts.databinding.FragmentContactDetailsBinding
import com.example.contacts.db.MyDBManager
import com.example.contacts.db.MyDBNameClass

private const val ARG_PARAM1 = "paramName"
private const val ARG_PARAM2 = "paramTelephone"
private const val ARG_PARAM3 = "paramTags"
private const val ARG_PARAM4 = "paramFirstLatter"
private const val ARG_PARAM5 = "isImp"

class ContactDetailsFragment : Fragment() {

    lateinit var bindingClass: FragmentContactDetailsBinding
    private var name: String? = null
    private var telephone: String? = null
    private var firstLatter: String? = null
    private var tags: List<String> = emptyList()
    var isImportant: Int? = null

    private lateinit var tagContainer: Flow
    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_PARAM1)
            telephone = it.getString(ARG_PARAM2)
            tags = it.getStringArrayList(ARG_PARAM3)?.toList() ?: emptyList()
            firstLatter = it.getString(ARG_PARAM4)
            isImportant = it.getInt(ARG_PARAM5)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentContactDetailsBinding.inflate(layoutInflater, container, false)
        tagContainer = bindingClass.root.findViewById(R.id.tagContainer)
        constraintLayout = bindingClass.root as ConstraintLayout
        with(bindingClass) {
            contactName.text = name
            imageTextView.text = firstLatter
            mobilePhone.text = telephone
            addTags(tags)
            if (isImportant == 1) {
                isImportantFlag.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                isImportantFlag.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }
            var isImportant = 0
            bindingClass.isImportantFlag.setOnClickListener {
            isImportant = if (isImportant == 1) {
                bindingClass.isImportantFlag.setImageResource(android.R.drawable.btn_star_big_off)
                0
            } else {
                bindingClass.isImportantFlag.setImageResource(android.R.drawable.btn_star_big_on)
                1
            }
                name?.let { updateContactImportance(it, isImportant)
                }
            }

        bindingClass.imageCall.setOnClickListener {
            Log.i("Test", telephone.toString())
            (activity as MainActivity?)?.makePhoneCall("tel:${telephone}")
        }
        bindingClass.imageSMS.setOnClickListener {
            sendSMS(telephone.toString())
        }
        return bindingClass.root
    }

    private fun addTags(tags: List<String>) {
        tags.forEach { tag ->
            val textView = TextView(requireContext()).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                setBackgroundResource(R.drawable.telephone_container)
                text = tag
                textSize = 20f
                setTextColor(resources.getColor(R.color.white, null))
                setPadding(30, 20, 30, 20)
                typeface = Typeface.SERIF
            }
            constraintLayout.addView(textView)
            tagContainer.addView(textView)
        }
    }

    private fun sendSMS(phoneNumber: String) {
        val uri = Uri.parse("smsto:$phoneNumber")
        val smsIntent = Intent(Intent.ACTION_SENDTO, uri)
        smsIntent.putExtra("sms_body", "")

        if (smsIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(smsIntent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.no_app_to_send_msg), Toast.LENGTH_SHORT).show()
        }
    }

    fun updateContactImportance(contactName: String, isImportant: Int) {
        val dbManager = MyDBManager(requireContext())
        dbManager.openDb()
        val values = ContentValues().apply {
            put(MyDBNameClass.COLUMN_CONTACT_ISIMPORTANT, isImportant)
        }
        dbManager.db?.update(
            MyDBNameClass.TABLE_CONTACTS,
            values,
            "${MyDBNameClass.COLUMN_CONTACT_NAME} = ?",
            arrayOf(contactName)
        )
        dbManager.closeDb()
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String, telephone: String, tags: ArrayList<String>, firstLatter: String, isImportant: Int) =
            ContactDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, name)
                    putString(ARG_PARAM2, telephone)
                    putStringArrayList(ARG_PARAM3, tags)
                    putString(ARG_PARAM4, firstLatter)
                    putInt(ARG_PARAM5, isImportant)
                }
            }
    }
}
