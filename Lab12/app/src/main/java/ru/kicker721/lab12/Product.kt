package ru.kicker721.lab12


data class Product(
    val name: String,
    val price: Double,
    val imageId: Int,
    var isInCart: Boolean = false
)
