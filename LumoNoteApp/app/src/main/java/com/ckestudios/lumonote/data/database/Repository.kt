package com.ckestudios.lumonote.data.database

import com.ckestudios.lumonote.data.models.Item

interface Repository {

    fun getItems() : List<Item>

    fun getItemByID(itemID: Int) : Item

    fun insertItem(item: Item)

    fun updateItem(item: Item)

    fun deleteItem(itemID: Int)
}