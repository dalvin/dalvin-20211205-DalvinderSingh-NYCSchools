package com.dalvinlabs.nycschools.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dalvinlabs.nycschools.databinding.ViewItemBinding
import com.dalvinlabs.nycschools.model.SchoolWithDetails
import com.dalvinlabs.nycschools.viewmodel.ItemViewModel

private fun SchoolWithDetails.toItemViewModel(): ItemViewModel = ItemViewModel(this)

class MyViewHolder(val binding: ViewItemBinding) : RecyclerView.ViewHolder(binding.root)

class Adapter(data: MutableList<SchoolWithDetails>) :
    RecyclerView.Adapter<MyViewHolder>() {

    private val itemViewModels = mutableListOf<ItemViewModel>()

    init {
        data.forEach { itemViewModels.add(it.toItemViewModel()) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.viewModel = itemViewModels[position]
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = itemViewModels.size
}