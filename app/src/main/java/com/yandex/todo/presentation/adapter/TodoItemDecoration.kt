package com.yandex.todo.presentation.adapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.yandex.todo.R

class TodoItemDecoration(
    private val bottomMargin: Int,
    private val topMargin: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val lastPositionTodoItem = parent.adapter?.itemCount?.minus(1) ?: -1

        when (parent.getChildAdapterPosition(view)) {
            0 -> {
                Log.i("TAG", "null")
                view.setBackgroundResource(R.drawable.first_todo_style)
            }
            lastPositionTodoItem -> {
                view.setBackgroundResource(R.drawable.last_todo_style)
            }
            else -> {
                view.setBackgroundResource(R.drawable.main_todo_style)
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }
}