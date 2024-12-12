package com.example.fixu.ui.notes

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fixu.databinding.NoteItemBinding
import com.example.fixu.response.NoteDataItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(private val onNoteClick: (NoteDataItem) -> Unit) : ListAdapter<NoteDataItem, NoteAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteDataItem,  onNoteClick: (NoteDataItem) -> Unit) {
            val dateFormatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val dateFormatOutput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val formattedDate = note.updatedAt.let { dateString ->
                runCatching {
                    val parsedDate = dateFormatInput.parse(dateString)
                    dateFormatOutput.format(parsedDate ?: Date())
                }.getOrDefault("Invalid date")
            }
            binding.tvDateNote.text = formattedDate
            binding.tvNoteTitle.text = note.title
            binding.tvNoteContent.text = note.content

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, AddNoteActivity::class.java)
                intent.putExtra("TITLE_EXTRA", note.title)
                intent.putExtra("CONTENT_EXTRA", note.content)
                intent.putExtra("NOTEID_EXTRA", note.id.toString())
                context.startActivity(intent)
            }
            itemView.setOnClickListener {
                onNoteClick(note)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note, onNoteClick)
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NoteDataItem>() {
            override fun areItemsTheSame(oldItem: NoteDataItem, newItem: NoteDataItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: NoteDataItem, newItem: NoteDataItem): Boolean {
                return oldItem.title == newItem.title &&
                        oldItem.content == newItem.content &&
                        oldItem.updatedAt == newItem.updatedAt
            }
        }
    }
}