package com.kicker721.lab25

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BooksDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_BOOKS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_AUTHOR TEXT NOT NULL,
                $COL_IS_READ INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
        onCreate(db)
    }

    fun getAllBooks(): List<BookItem.Book> {
        val list = mutableListOf<BookItem.Book>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_BOOKS,
            arrayOf(COL_ID, COL_TITLE, COL_AUTHOR, COL_IS_READ),
            null,
            null,
            null,
            null,
            "$COL_IS_READ ASC, $COL_TITLE COLLATE NOCASE ASC"
        )
        cursor.use {
            val idxId = it.getColumnIndexOrThrow(COL_ID)
            val idxTitle = it.getColumnIndexOrThrow(COL_TITLE)
            val idxAuthor = it.getColumnIndexOrThrow(COL_AUTHOR)
            val idxIsRead = it.getColumnIndexOrThrow(COL_IS_READ)
            while (it.moveToNext()) {
                val id = it.getLong(idxId)
                val title = it.getString(idxTitle)
                val author = it.getString(idxAuthor)
                val isRead = it.getInt(idxIsRead) == 1
                list.add(BookItem.Book(id, title, author, isRead))
            }
        }
        return list
    }

    fun insertBook(title: String, author: String, isRead: Boolean): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, title)
            put(COL_AUTHOR, author)
            put(COL_IS_READ, if (isRead) 1 else 0)
        }
        return db.insert(TABLE_BOOKS, null, values)
    }

    fun updateBook(book: BookItem.Book): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, book.title)
            put(COL_AUTHOR, book.author)
            put(COL_IS_READ, if (book.isRead) 1 else 0)
        }
        return db.update(TABLE_BOOKS, values, "$COL_ID = ?", arrayOf(book.id.toString()))
    }

    fun deleteBook(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_BOOKS, "$COL_ID = ?", arrayOf(id.toString()))
    }

    companion object {
        private const val DATABASE_NAME = "books.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_BOOKS = "books"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_AUTHOR = "author"
        const val COL_IS_READ = "is_read"
    }
}

