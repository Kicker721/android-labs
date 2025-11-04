package com.kicker721.lab25

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: BooksAdapter
    private lateinit var items: MutableList<BookItem>
    private lateinit var rvBooks: RecyclerView
    private lateinit var floatingButton: FloatingActionButton
    private lateinit var db: BooksDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        floatingButton = findViewById(R.id.fabAddBook)
        rvBooks = findViewById(R.id.booksRecyclerView)
        db = BooksDbHelper(this)

        items = mutableListOf()
        adapter = BooksAdapter(items) { book ->
            showEditDialog(book)
        }
        rvBooks.adapter = adapter

        floatingButton.setOnClickListener {
            showEditDialog(null)
        }

        loadBooks()
    }

    private fun loadBooks() {
        val headerNew = getString(R.string.header_new)
        val headerRead = getString(R.string.header_read)

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val books = db.getAllBooks()
            val newItems = mutableListOf<BookItem>()
            newItems.add(BookItem.Header(headerNew))
            newItems.addAll(books.filter { !it.isRead })
            newItems.add(BookItem.Header(headerRead))
            newItems.addAll(books.filter { it.isRead })
            withContext(Dispatchers.Main) {
                items.clear()
                items.addAll(newItems)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showEditDialog(book: BookItem.Book?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_book, null)
        val checkBox = dialogView.findViewById<CheckBox>(R.id.checkboxRead)
        val etTitle = dialogView.findViewById<EditText>(R.id.editTitle)
        val etAuthor = dialogView.findViewById<EditText>(R.id.editAuthor)

        if (book != null) {
            etTitle.setText(book.title)
            etAuthor.setText(book.author)
            checkBox.isChecked = book.isRead
        } else {
            checkBox.isChecked = false
        }

        val builder = AlertDialog.Builder(this)
            .setTitle(if (book == null) getString(R.string.add_book) else getString(R.string.edit_book))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val title = etTitle.text.toString().trim()
                val author = etAuthor.text.toString().trim()

                if (title.isEmpty() || author.isEmpty()) {
                    Toast.makeText(this, getString(R.string.enter_title_author), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val isRead = checkBox.isChecked

                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    if (book == null) {
                        db.insertBook(title = title, author = author, isRead = isRead)
                    } else {
                        db.updateBook(book.copy(title = title, author = author, isRead = isRead))
                    }
                    loadBooks()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)

        if (book != null) {
            builder.setNeutralButton(getString(R.string.delete)) { _, _ ->
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_book_confirm_title))
                    .setMessage(getString(R.string.delete_book_confirm_message))
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        val scope = CoroutineScope(Dispatchers.IO)
                        scope.launch {
                            db.deleteBook(book.id)
                            loadBooks()
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
        }

        builder.show()
    }

}