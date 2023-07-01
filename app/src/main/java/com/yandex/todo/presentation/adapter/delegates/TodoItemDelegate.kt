package com.yandex.todo.presentation.adapter.delegates

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yandex.todo.R
import com.yandex.todo.domain.model.ImportanceLevel
import com.yandex.todo.domain.model.ListItem
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.presentation.adapter.TodoListAdapter
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.coroutineContext

class TodoItemDelegate(
    private val clickable: TodoListAdapter.Clickable
) : Delegate {
    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return TodoItemListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        )
    }

    override fun bindViewHolder(
        holder: RecyclerView.ViewHolder,
        listItem: ListItem,
    ) {
        (holder as TodoItemListViewHolder).bind(listItem as TodoItem)
    }

    override fun forItem(listItem: ListItem): Boolean {
        return listItem is TodoItem
    }

    inner class TodoItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val doneCheckBox = itemView.findViewById<CheckBox>(R.id.checkbox)
        private val todoDescription = itemView.findViewById<TextView>(R.id.todo_description)
        private val todoDeadline = itemView.findViewById<TextView>(R.id.todo_deadline)
        private val buttonInfo = itemView.findViewById<AppCompatImageButton>(R.id.button_info)

        fun bind(todoItem: TodoItem) {
            todoDescription.text = todoItem.taskDescription

            buttonInfo.setOnClickListener {
                clickable.onClick(adapterPosition)
            }

            if (todoItem.importanceLevel == ImportanceLevel.IMPORTANT) {
                // TODO
            }

            todoDeadline.text = todoItem.createDate.toString()

            doneCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    todoDescription.paintFlags =
                        todoDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    todoDescription.paintFlags =
                        todoDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }
        }
    }
}