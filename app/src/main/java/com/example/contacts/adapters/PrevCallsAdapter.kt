package com.example.contacts.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.models.Contact
import com.example.contacts.MainActivity
import com.example.contacts.R
import com.example.contacts.databinding.ContactCardBinding
import com.example.contacts.databinding.PrevCallCardBinding
import com.example.contacts.db.MyDBManager
import com.example.contacts.fragments.ContactDetailsFragment
import com.example.contacts.models.PrevCalls
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class PrevCallsAdapter(private val context: Context, private val prevCalls: List<PrevCalls>, private val activity: MainActivity) : RecyclerView.Adapter<PrevCallsAdapter.PrevCallsHolder>() {
    private lateinit var dbManager: MyDBManager

    class PrevCallsHolder(item: View) : RecyclerView.ViewHolder(item){
        val binding = PrevCallCardBinding.bind(item)
        fun bind(prevCall: PrevCalls, context: Context) = with(binding) {
            val dateFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            imageTextView.text = prevCall.name?.firstOrNull()?.toString() ?: "С"
            contactNameOrTelephone.text = prevCall.name ?: prevCall.telephone
            date.text = dateFormat.format(prevCall.date)
            time.text = timeFormat.format(prevCall.time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrevCallsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.prev_call_card, parent, false)
        return PrevCallsHolder(view)
    }

    override fun getItemCount(): Int {
        return prevCalls.size
    }

    override fun onBindViewHolder(holder: PrevCallsHolder, position: Int) {
        val call = prevCalls[position]

        holder.bind(call, context)
    }
}