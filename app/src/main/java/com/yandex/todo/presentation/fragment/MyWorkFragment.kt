package com.yandex.todo.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yandex.todo.MyApp
import com.yandex.todo.R
import com.yandex.todo.databinding.FragmentMyWorkBinding
import com.yandex.todo.domain.model.CreateTodoItem
import com.yandex.todo.domain.model.ImportanceLevel
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.presentation.adapter.TodoItemDecoration
import com.yandex.todo.presentation.adapter.TodoListAdapter
import com.yandex.todo.presentation.adapter.delegates.CreateTodoItemDelegate
import com.yandex.todo.presentation.adapter.delegates.TodoItemDelegate
import com.yandex.todo.presentation.viewmodel.MyWorkViewModel
import com.yandex.todo.presentation.viewmodel.TodoViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyWorkFragment : Fragment(), TodoListAdapter.Clickable {
    private var _binding: FragmentMyWorkBinding? = null
    private val binding: FragmentMyWorkBinding
        get() = _binding!!

    @Inject
    lateinit var todoViewModelFactory: TodoViewModelFactory

    private lateinit var todoListAdapter: TodoListAdapter
    private lateinit var myWorkViewModel: MyWorkViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApp).appComponent.injectMyWorkFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myWorkViewModel = ViewModelProvider(
            this,
            todoViewModelFactory
        )[MyWorkViewModel::class.java]
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

        binding.addTodoButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_myWorkFragment_to_detailedWorkFragment
            )
        }
    }


    private fun dpToPixel(dp: Int): Int {
        return (dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    override fun onClick(position: Int) {
        Log.i("TAG", position.toString())
        val bundle = Bundle().apply {
            putParcelable("TODO_ITEM", myWorkViewModel.todoList.value[position] as TodoItem)
        }

        findNavController().navigate(
            R.id.action_myWorkFragment_to_detailedWorkFragment,
            bundle
        )
    }

    private fun initTodoList() = with(binding) {
        todoListAdapter = TodoListAdapter(
            listDelegate = listOf(
                TodoItemDelegate(this@MyWorkFragment),
                CreateTodoItemDelegate()
            ),
        )

        todoList.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        TodoItemDecoration(
            bottomMargin = dpToPixel(20),
            topMargin = dpToPixel(25)
        ).also {
            todoList.addItemDecoration(it)
        }

        todoList.adapter = todoListAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myWorkViewModel.todoList.collect { newTodoList ->
                    todoListAdapter.submitList(newTodoList)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}