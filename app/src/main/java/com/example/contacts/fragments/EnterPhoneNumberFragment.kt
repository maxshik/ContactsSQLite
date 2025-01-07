package com.example.contacts.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.contacts.MainActivity
import com.example.contacts.databinding.FragmentEnterPhoneNumberBinding
import com.example.contacts.db.MyDBManager
import com.example.contacts.models.PrevCalls
import java.sql.Time
import java.util.Date


class EnterPhoneNumberFragment : Fragment() {
    lateinit var bindingClass : FragmentEnterPhoneNumberBinding
    private lateinit var dbManager: MyDBManager
    var currentText = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentEnterPhoneNumberBinding.inflate(layoutInflater)
        dbManager = MyDBManager(requireContext())
        dbManager.openDb()

        fun addDigit(digit: String) {
            with(bindingClass) {
                val currentText = phoneNumberInput.text.toString()
                phoneNumberInput.setText(currentText + digit)
            }
        }

        with(bindingClass) {
            button0.setOnClickListener { addDigit("0") }
            button1.setOnClickListener { addDigit("1") }
            button2.setOnClickListener { addDigit("2") }
            button3.setOnClickListener { addDigit("3") }
            button4.setOnClickListener { addDigit("4") }
            button5.setOnClickListener { addDigit("5") }
            button6.setOnClickListener { addDigit("6") }
            button7.setOnClickListener { addDigit("7") }
            button8.setOnClickListener { addDigit("8") }
            button9.setOnClickListener { addDigit("9") }
            buttonDelete.setOnClickListener {
                val currentText = phoneNumberInput.text.toString()
                if (currentText.isNotEmpty()) {
                    phoneNumberInput.setText(currentText.dropLast(1))
                }
            }

            phoneNumberInput.addTextChangedListener(object : TextWatcher {
                private var isFormatting = false
                private var isDeleting = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    isDeleting = count > after
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting) {
                        return
                    }
                    isFormatting = true

                    val formatted = formatPhoneNumber(s.toString(), isDeleting)
                    currentText = formatted.second
                    phoneNumberInput.setText(formatted.first)
                    phoneNumberInput.setSelection(formatted.first.length)
                    isFormatting = false
                }
            })

            buttonCall.setOnClickListener {
                val phoneNumber = "+${currentText}"
                (activity as MainActivity?)?.makePhoneCall("tel:$phoneNumber")

                val contactName = dbManager.getContactNameByPhoneNumber(phoneNumber)

                val prevCall = PrevCalls(contactName, phoneNumber, Date(), Time(System.currentTimeMillis()))

                dbManager.savePrevCall(prevCall)

                Log.i("Test", dbManager.getPrevCalls().size.toString())
            }
        }

        return bindingClass.root
    }

    fun formatPhoneNumber(number: String, isDeleting: Boolean): Pair<String, String> {
        val cleanNumber = number.replace("[^\\d]".toRegex(), "")
        val sb = StringBuilder()
        var i = 0

        if (cleanNumber.length > 0) {
            sb.append("+")
            sb.append(cleanNumber.substring(i, i + minOf(3, cleanNumber.length - i)))
            i += 3
        }
        if (cleanNumber.length > i) {
            sb.append(" (")
            sb.append(cleanNumber.substring(i, i + minOf(2, cleanNumber.length - i)))
            i += 2
        }
        if (cleanNumber.length == 5) {
            sb.append(") ")
        }
        if (cleanNumber.length > i) {
            sb.append(") ")
            sb.append(cleanNumber.substring(i, i + minOf(3, cleanNumber.length - i)))
            i += 3
        }
        if (cleanNumber.length > i) {
            sb.append("-")
            sb.append(cleanNumber.substring(i, i + minOf(2, cleanNumber.length - i)))
            i += 2
        }
        if (cleanNumber.length > i) {
            sb.append("-")
            sb.append(cleanNumber.substring(i, i + minOf(2, cleanNumber.length - i)))
        }

        return Pair(sb.toString(), cleanNumber)
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }
}