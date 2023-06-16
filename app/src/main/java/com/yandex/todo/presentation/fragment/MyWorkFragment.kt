package com.yandex.todo.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yandex.todo.MyApp
import com.yandex.todo.databinding.FragmentMyWorkBinding
import com.yandex.todo.domain.model.ImportanceLevel
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.presentation.adapter.TodoListAdapter
import com.yandex.todo.presentation.adapter.delegates.TodoItemDelegate
import com.yandex.todo.presentation.viewmodel.MyWorkViewModel

class MyWorkFragment : Fragment() {
    private var _binding: FragmentMyWorkBinding? = null
    private val binding: FragmentMyWorkBinding
        get() = _binding!!

    private lateinit var todoListAdapter: TodoListAdapter
    private lateinit var myWorkViewModel: MyWorkViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApp).appComponent.injectMyWorkFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myWorkViewModel = ViewModelProvider(this)[MyWorkViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyWorkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTodoList()

    }

    private fun initTodoList() = with(binding) {
        todoListAdapter = TodoListAdapter(
            listDelegate = listOf(TodoItemDelegate()),
        )
        todoList.layoutManager = LinearLayoutManager(requireContext())
        todoList.adapter = todoListAdapter
        todoListAdapter.submitList(
            listOf(
                TodoItem(
                    id = "123",
                    taskDescription = "Dsdsad",
                    importanceLevel = ImportanceLevel.LOW,
                    isDone = true,
                    createDate = "dsad"
                ),
                TodoItem(
                    id = "123",
                    taskDescription = "Dsdsad",
                    importanceLevel = ImportanceLevel.LOW,
                    isDone = true,
                    createDate = "dsad"
                ),
                TodoItem(
                    id = "123",
                    taskDescription = "Dsdsad",
                    importanceLevel = ImportanceLevel.LOW,
                    isDone = true,
                    createDate = "dsad"
                ),
                TodoItem(
                    id = "123",
                    taskDescription = "Dsdsad",
                    importanceLevel = ImportanceLevel.LOW,
                    isDone = true,
                    createDate = "dsad"
                ),
                TodoItem(
                    id = "123",
                    taskDescription = "Dsdsad",
                    importanceLevel = ImportanceLevel.LOW,
                    isDone = true,
                    createDate = "dsad"
                ),
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}