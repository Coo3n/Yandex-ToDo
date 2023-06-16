package com.yandex.todo.presentation.adapter.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.yandex.todo.R
import com.yandex.todo.domain.model.ListItem
import com.yandex.todo.domain.model.TodoItem

class TodoItemDelegate : Delegate {
    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return TodoItemListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        )
    }

    override fun bindViewHolder(holder: RecyclerView.ViewHolder, listItem: ListItem) {
        (holder as TodoItemListViewHolder).bind(listItem as TodoItem)
    }

    override fun forItem(listItem: ListItem): Boolean {
        return listItem is TodoItem
    }

    inner class TodoItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val isDone = itemView.findViewById<CheckBox>(R.id.checkbox)
        private val todoDescription = itemView.findViewById<TextView>(R.id.todo_description)
        private val buttonInfo = itemView.findViewById<AppCompatImageButton>(R.id.button_info)

        fun bind(todoItem: TodoItem) {
            todoDescription.text = todoItem.taskDescription
            buttonInfo.setOnClickListener {

            }
        }
    }
}