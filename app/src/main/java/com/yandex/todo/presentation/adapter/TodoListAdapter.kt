package com.yandex.todo.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yandex.todo.domain.model.ListItem
import com.yandex.todo.presentation.adapter.delegates.Delegate

class TodoListAdapter(
    private val listDelegate: List<Delegate>
) : ListAdapter<ListItem, ViewHolder>(TodoDiffUtil()) {
    class TodoDiffUtil : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder = listDelegate[viewType].getViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listDelegate[getItemViewType(position)].bindViewHolder(holder, currentList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return listDelegate.indexOfFirst { delegate -> delegate.forItem(currentList[position]) }
    }
}