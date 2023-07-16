package com.yandex.todo.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import com.yandex.todo.MyApp
import com.yandex.todo.R
import com.yandex.todo.databinding.FragmentMyWorkBinding
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.data.remote.worker.TodoWorker
import com.yandex.todo.data.remote.worker.TodoWorkerFactory
import com.yandex.todo.presentation.adapter.TodoItemDecoration
import com.yandex.todo.presentation.adapter.TodoItemTouchHelper
import com.yandex.todo.presentation.adapter.TodoListAdapter
import com.yandex.todo.presentation.adapter.delegates.CreateTodoItemDelegate
import com.yandex.todo.presentation.adapter.delegates.TodoItemDelegate
import com.yandex.todo.presentation.event.MainWorkEvent
import com.yandex.todo.presentation.viewmodel.MyWorkViewModel
import com.yandex.todo.presentation.viewmodel.TodoViewModelFactory
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MyWorkFragment : Fragment(), TodoListAdapter.Clickable {
    private var _binding: FragmentMyWorkBinding? = null
    private val binding: FragmentMyWorkBinding
        get() = _binding!!

    @Inject
    lateinit var todoViewModelFactory: TodoViewModelFactory
    private val myWorkViewModel: MyWorkViewModel by viewModels(
        factoryProducer = {
            todoViewModelFactory
        }
    )

    @Inject
    lateinit var todoWorkerFactory: TodoWorkerFactory
    private lateinit var todoListAdapter: TodoListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApp).appComponent
            .createTodoComponentFactory()
            .create()
            .injectMyWorkFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBackgroundTodoWorker()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        _binding = FragmentMyWorkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTodoList()
        initListRefresherListener()
        initAddButtonListener()
        initSettingsButton()
    }

    private fun initSettingsButton() {
        binding.settings.setOnClickListener {
            findNavController().navigate(
                R.id.action_myWorkFragment_to_settingsFragment
            )
        }
    }

    private fun initAddButtonListener() {
        binding.addTodoButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_myWorkFragment_to_detailWorkFragment
            )
        }
    }

    private fun initListRefresherListener() {
        binding.refresher.setOnRefreshListener {
            myWorkViewModel.onEvent(MainWorkEvent.Refresh)

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    myWorkViewModel.isRefreshing.collect { isRefreshing ->
                        if (!isRefreshing) {
                            binding.refresher.isRefreshing = false
                        }
                    }
                }
            }
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
            R.id.action_myWorkFragment_to_detailWorkFragment,
            bundle
        )
    }

    private fun initBackgroundTodoWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<TodoWorker>(
            repeatInterval = 8,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork(
                "TodoWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
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

        val todoItemTouchHelper = ItemTouchHelper(
            TodoItemTouchHelper(requireContext(), todoListAdapter) { position ->
                showCancelSnackBar(todoListAdapter.currentList[position] as TodoItem)
            }
        )

        todoItemTouchHelper.attachToRecyclerView(todoList)

        todoList.adapter = todoListAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myWorkViewModel.todoList.collect { newTodoList ->
                    todoListAdapter.submitList(newTodoList)
                }
            }
        }
    }

    private fun showCancelSnackBar(todoItem: TodoItem) {
        val snackbar = Snackbar.make(
            requireView(),
            todoItem.taskDescription,
            Snackbar.LENGTH_INDEFINITE
        )

        myWorkViewModel.onEvent(MainWorkEvent.TimingDelete(todoItem))

        snackbar.setAction("Отменить") {
            myWorkViewModel.onEvent(MainWorkEvent.TimingRestore(todoItem))
            snackbar.dismiss()
        }

        val countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(tick: Long) {
                snackbar.setText("Дело: ${todoItem.taskDescription}, удаление через: ${tick / 1000}")
            }

            override fun onFinish() {
                if (snackbar.isShown) {
                    myWorkViewModel.onEvent(MainWorkEvent.Delete(todoItem))
                    snackbar.dismiss()
                }
            }
        }

        snackbar.show()
        countDownTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}