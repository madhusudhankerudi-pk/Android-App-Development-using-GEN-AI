package com.example.shale_nammapride.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shale_nammapride.R
import com.example.shale_nammapride.databinding.ItemAchievementBinding
import com.example.shale_nammapride.models.StudentStar

class AchievementAdapter(
    private var achievements: List<StudentStar>,
    private val isAdmin: Boolean = false,
    private val onAction: ((StudentStar, String) -> Unit)? = null
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.binding.tvAchievementTitle.text = achievement.achievement
        holder.binding.tvAchievementStudent.text = "${achievement.name} - Class ${achievement.grade}"
        holder.binding.tvAchievementDate.text = "Recent"

        if (achievement.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(achievement.imageUrl)
                .into(holder.binding.ivAchievementPhoto)
        } else {
            holder.binding.ivAchievementPhoto.setImageResource(R.mipmap.ic_launcher)
        }

        if (isAdmin) {
            holder.itemView.setOnLongClickListener {
                showMenu(holder, achievement)
                true
            }
        }
    }

    private fun showMenu(holder: AchievementViewHolder, item: StudentStar) {
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

    fun updateList(newList: List<StudentStar>) {
        this.achievements = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = achievements.size
}