package com.yandex.todo.presentation.adapter

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yandex.todo.R

class TodoItemTouchHelper(
    private val todoListAdapter: TodoListAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        val currentList = todoListAdapter.currentList.toMutableList()
        currentList.removeAt(position)
        todoListAdapter.submitList(currentList)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val backgroundCornerOffset = 20
        val itemView = viewHolder.itemView

        val icon = ContextCompat.getDrawable(
            recyclerView.context, if (dX > 0) {
                R.drawable.icon_done
            } else {
                R.drawable.icon_delete
            }
        )

        val background = ColorDrawable(
            ContextCompat.getColor(
                recyclerView.context,
                if (dX > 0) {
                    R.color.color_light_green
                } else {
                    R.color.back_dark_elevated
                }
            )
        )

        val iconMargin = (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2
        val iconTop = itemView.top + (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2
        val iconBottom = itemView.top + (icon?.intrinsicHeight ?: 0)

        if (dX > 0) {
            val iconLeft = itemView.left + iconMargin
            val iconRight = itemView.left + iconMargin + (icon?.intrinsicWidth ?: 0)
            icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.left, itemView.top, itemView.left +
                        dX.toInt() + backgroundCornerOffset, itemView.bottom
            )
        } else if (dX < 0) {
            val iconLeft = itemView.right - iconMargin - (icon?.intrinsicWidth ?: 0)
            val iconRight = itemView.right - iconMargin
            icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.right + dX.toInt() - backgroundCornerOffset,
                itemView.top,
                itemView.right,
                itemView.bottom
            )
        } else {
            background.setBounds(0, 0, 0, 0)
        }

        background.draw(c)
        icon?.draw(c)
    }
}