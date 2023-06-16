package com.yandex.todo.presentation.adapter.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yandex.todo.domain.model.ListItem

interface Delegate {
    fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun bindViewHolder(holder: RecyclerView.ViewHolder, listItem: ListItem)
    fun forItem(listItem: ListItem): Boolean
}