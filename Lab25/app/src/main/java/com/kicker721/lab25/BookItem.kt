package com.kicker721.lab25

sealed class BookItem {
    data class Header(val title: String) : BookItem()
    data class Book(
        val id: Long,
        val title: String,
        val author: String,
        val isRead: Boolean
    ) : BookItem()
}
