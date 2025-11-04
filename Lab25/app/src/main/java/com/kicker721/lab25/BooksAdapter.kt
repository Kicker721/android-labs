package com.kicker721.lab25

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BooksAdapter(private val items: List<BookItem>,
    private val onBookClick: (BookItem.Book) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_BOOK = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is BookItem.Header -> TYPE_HEADER
            is BookItem.Book -> TYPE_BOOK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.item_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_book, parent, false)
                BookViewHolder(view, onBookClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is BookItem.Header -> (holder as HeaderViewHolder).bind(item)
            is BookItem.Book -> (holder as BookViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.headerTitle)
        fun bind(item: BookItem.Header) {
            tvTitle.text = item.title
        }
    }

    class BookViewHolder(view: View, val onBookClick: (BookItem.Book) -> Unit) : RecyclerView.ViewHolder(view) {
        private val ivIcon: ImageView = view.findViewById(R.id.bookIcon)
        private val tvTitle: TextView = view.findViewById(R.id.bookTitle)
        private val tvAuthor: TextView = view.findViewById(R.id.bookAuthor)

        fun bind(item: BookItem.Book) {
            tvTitle.text = item.title
            tvAuthor.text = item.author
            itemView.setOnClickListener { onBookClick(item) }

            val color = if (item.isRead) 0xFF1B873E.toInt() else 0xFF4A4A4A.toInt()
            ivIcon.setColorFilter(color)
        }
    }
}
