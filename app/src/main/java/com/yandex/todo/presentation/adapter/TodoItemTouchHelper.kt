package com.yandex.todo.presentation.adapter

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yandex.todo.R
import com.yandex.todo.domain.repository.TodoItemsRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

class TodoItemTouchHelper(
    private val applicationContext: Context,
    private val todoListAdapter: TodoListAdapter,
    private val onItemSwiped: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val acceptSwipePaint = Paint().apply {
        color = applicationContext.getColor(R.color.color_light_green)
    }
    private val deleteSwipePaint = Paint().apply {
        color = applicationContext.getColor(R.color.color_light_red)
    }
    private val whitePaint = Paint().apply {
        colorFilter = PorterDuffColorFilter(
            applicationContext.getColor(R.color.color_light_white),
            PorterDuff.Mode.SRC_IN
        )
    }

    private val acceptIcon = AppCompatResources.getDrawable(
        applicationContext, R.drawable.icon_done
    )!!.toBitmap()
    private val deleteIcon = AppCompatResources.getDrawable(
        applicationContext, R.drawable.icon_delete
    )!!.toBitmap()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        onItemSwiped.invoke(position)
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
        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        val itemView = viewHolder.itemView

        val isSwipeRight = dX > 0

        val swipeRect = if (isSwipeRight) {
            RectF(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.left.toFloat() + dX + convertDpToPx(8),
                itemView.bottom.toFloat()
            )
        } else if (dX.toInt() == 0) {
            RectF(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.left.toFloat(),
                itemView.bottom.toFloat()
            )
        } else {
            RectF(
                itemView.right.toFloat() + dX - convertDpToPx(8),
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
        }

        val iconX = if (isSwipeRight) {
            itemView.left.toFloat() - convertDpToPx(40) + dX
        } else {
            itemView.right.toFloat() + convertDpToPx(40) - deleteIcon.width + dX
        }

        val iconY =
            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - acceptIcon.height) / 2

        c.drawRect(swipeRect, if (isSwipeRight) acceptSwipePaint else deleteSwipePaint)
        c.drawBitmap(if (isSwipeRight) acceptIcon else deleteIcon, iconX, iconY, whitePaint)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun convertDpToPx(dp: Int): Int {
        return (dp * (applicationContext.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }
}