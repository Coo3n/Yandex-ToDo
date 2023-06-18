package com.yandex.todo.presentation.adapter.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yandex.todo.R
import com.yandex.todo.domain.model.CreateTodoItem
import com.yandex.todo.domain.model.ListItem

class CreateTodoItemDelegate : Delegate {
    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return CreateTodoItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.create_todo_item, parent, false
            )
        )
    }

    override fun bindViewHolder(
        holder: RecyclerView.ViewHolder,
        listItem: ListItem,
    ) {
        (holder as CreateTodoItemViewHolder).bind(listItem as CreateTodoItem)
    }

    override fun forItem(listItem: ListItem): Boolean {
        return listItem is CreateTodoItem
    }

    inner class CreateTodoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(createTodoItem: CreateTodoItem) {

        }
    }
}