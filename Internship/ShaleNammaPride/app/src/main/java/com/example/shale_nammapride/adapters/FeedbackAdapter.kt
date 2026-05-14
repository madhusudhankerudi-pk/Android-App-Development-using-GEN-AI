package com.example.shale_nammapride.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shale_nammapride.databinding.ItemFeedbackBinding
import com.example.shale_nammapride.models.Feedback
import java.text.SimpleDateFormat
import java.util.*

class FeedbackAdapter(private val feedbacks: List<Feedback>) :
    RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    class FeedbackViewHolder(val binding: ItemFeedbackBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val binding = ItemFeedbackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedbackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbacks[position]
        holder.binding.tvFeedbackCategory.text = feedback.category
        holder.binding.tvFeedbackMessage.text = feedback.message
        
        val date = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(feedback.timestamp))
        holder.binding.tvFeedbackDate.text = date

        if (feedback.anonymous) {
            holder.binding.tvFeedbackUser.text = "Anonymous Parent"
        } else {
            holder.binding.tvFeedbackUser.text = "From: ${feedback.userName ?: "Unknown User"}"
        }
    }

    override fun getItemCount() = feedbacks.size
}