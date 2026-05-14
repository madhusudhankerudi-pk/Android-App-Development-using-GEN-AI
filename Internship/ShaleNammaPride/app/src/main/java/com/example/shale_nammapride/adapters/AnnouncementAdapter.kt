package com.example.shale_nammapride.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.shale_nammapride.databinding.ItemAnnouncementBinding
import com.example.shale_nammapride.models.Announcement

class AnnouncementAdapter(
    private var announcements: List<Announcement>,
    private val isAdmin: Boolean = false,
    private val onAction: ((Announcement, String) -> Unit)? = null
) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    class AnnouncementViewHolder(val binding: ItemAnnouncementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val binding = ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnnouncementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.binding.tvAnnouncementTitle.text = announcement.title
        holder.binding.tvAnnouncementMessage.text = announcement.message
        holder.binding.tvAnnouncementDate.text = announcement.date

        val iconRes = when (announcement.type) {
            "Holiday" -> android.R.drawable.ic_menu_today
            "Event" -> android.R.drawable.btn_star_big_on
            "Exam" -> android.R.drawable.ic_menu_edit
            else -> android.R.drawable.ic_popup_reminder
        }
        holder.binding.ivAnnouncementType.setImageResource(iconRes)

        if (isAdmin) {
            holder.itemView.setOnLongClickListener {
                showMenu(holder, announcement)
                true
            }
        }
    }

    private fun showMenu(holder: AnnouncementViewHolder, item: Announcement) {
        val popup = PopupMenu(holder.itemView.context, holder.itemView)
        popup.menu.add("Edit")
        popup.menu.add("Delete")
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Edit" -> onAction?.invoke(item, "EDIT")
                "Delete" -> onAction?.invoke(item, "DELETE")
            }
            true
        }
        popup.show()
    }

    fun updateList(newList: List<Announcement>) {
        this.announcements = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = announcements.size
}