package com.example.shale_nammapride.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shale_nammapride.databinding.ItemFacilityBinding
import com.example.shale_nammapride.models.Facility

class FacilityAdapter(private val facilities: List<Facility>) :
    RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder>() {

    class FacilityViewHolder(val binding: ItemFacilityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val binding = ItemFacilityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FacilityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        val facility = facilities[position]
        holder.binding.tvFacilityTitle.text = facility.title
        holder.binding.tvFacilityDesc.text = facility.description
        
        if (facility.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(facility.imageUrl)
                .into(holder.binding.ivFacilityImage)
        }
    }

    override fun getItemCount() = facilities.size
}