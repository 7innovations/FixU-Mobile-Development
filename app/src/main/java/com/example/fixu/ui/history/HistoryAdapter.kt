package com.example.fixu.ui.history

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fixu.ResultActivity
import com.example.fixu.databinding.HistoryItemBinding
import com.example.fixu.response.HistoryDataItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<HistoryDataItem, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryDataItem) {
            val dateFormatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val dateFormatOutput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val formattedDate = history.createdAt.let { dateString ->
                runCatching {
                    val parsedDate = dateFormatInput.parse(dateString)
                    dateFormatOutput.format(parsedDate ?: Date())
                }.getOrDefault("Invalid date")
            }

            binding.tvDateHistory.text = formattedDate
            binding.tvResultLabel.text = history.result

            binding.btnHistoryViewMore.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ResultActivity::class.java)
                intent.putExtra(ResultActivity.EXTRA_FROM_ADAPTER, true)
                intent.putExtra(ResultActivity.EXTRA_FEEDBACK, history.feedback)
                intent.putExtra(ResultActivity.EXTRA_PROBABILITY, history.probability.toString())
                intent.putExtra(ResultActivity.EXTRA_RESULT, history.result)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryDataItem>() {
            override fun areItemsTheSame(oldItem: HistoryDataItem, newItem: HistoryDataItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: HistoryDataItem, newItem: HistoryDataItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}