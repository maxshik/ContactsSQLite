package com.example.contacts.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.MainActivity
import com.example.contacts.R
import com.example.contacts.adapters.ContactsAdapter
import com.example.contacts.adapters.PrevCallsAdapter
import com.example.contacts.databinding.FragmentPrevCallsBinding
import com.example.contacts.db.MyDBManager
import com.example.contacts.models.PrevCalls

class PrevCallsFragment : Fragment() {
    lateinit var bindingClass: FragmentPrevCallsBinding
    private lateinit var adapter: PrevCallsAdapter
    private lateinit var dbManager: MyDBManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = MyDBManager(requireContext())
        dbManager.openDb()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingClass = FragmentPrevCallsBinding.inflate(layoutInflater)
        val prevCalls : List<PrevCalls> = dbManager.getPrevCalls()

        adapter = PrevCallsAdapter(requireContext(), prevCalls, activity as MainActivity)
        init()

        return bindingClass.root
    }


    private fun init() {
        bindingClass.apply {
            rcViewOfPrevCalls.layoutManager = LinearLayoutManager(context)
            rcViewOfPrevCalls.adapter = adapter
        }
    }

}