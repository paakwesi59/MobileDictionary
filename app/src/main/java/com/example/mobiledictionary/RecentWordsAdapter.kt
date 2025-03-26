package com.example.mobiledictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledictionary.databinding.RecentWordItemBinding

class RecentWordsAdapter(
    private var recentWords: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<RecentWordsAdapter.RecentWordViewHolder>() {

    inner class RecentWordViewHolder(private val binding: RecentWordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(word: String) {
            binding.recentWordText.text = word
            binding.root.setOnClickListener { onItemClick(word) }
        }
    }

    fun updateData(newWords: List<String>) {
        recentWords = newWords
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentWordViewHolder {
        val binding = RecentWordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentWordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentWordViewHolder, position: Int) {
        holder.bind(recentWords[position])
    }

    override fun getItemCount(): Int = recentWords.size
}
