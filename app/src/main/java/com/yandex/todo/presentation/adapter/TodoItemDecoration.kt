package com.yandex.todo.presentation.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

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
        when (parent.getChildAdapterPosition(view)) {
            0 -> {
                outRect.top = topMargin
                outRect.bottom = bottomMargin
            }
            else -> {
                outRect.bottom = bottomMargin
            }
        }
    }
}