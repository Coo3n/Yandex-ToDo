package com.yandex.todo.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.jakewharton.rxbinding2.widget.RxTextView
import com.yandex.todo.MyApp
import com.yandex.todo.R
import com.yandex.todo.databinding.FragmentDetailedWorkBinding
import com.yandex.todo.domain.model.ImportanceLevel
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.presentation.event.DetailedWorkEvent
import com.yandex.todo.presentation.viewmodel.DetailedWorkViewModel
import com.yandex.todo.presentation.viewmodel.TodoViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DetailedWorkFragment : Fragment() {
    private var _binding: FragmentDetailedWorkBinding? = null
    private val binding: FragmentDetailedWorkBinding
        get() = _binding!!

    @Inject
    lateinit var todoViewModelFactory: TodoViewModelFactory

    private val detailedWorkViewModel: DetailedWorkViewModel by viewModels(
        factoryProducer = {
            todoViewModelFactory
        }
    )

    private val disposeBag = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApp).appComponent
            .createTodoComponentFactory()
            .create()
            .injectDetailedWorkFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailedWorkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val todoItem = arguments?.getParcelable<TodoItem>("TODO_ITEM")

        binding.inputTodo.textChanges(
            todoItem?.taskDescription.toString()
        ).addTo(disposeBag)

        setupDeleteItem(todoItem)
        setupImportanceLevelMenu()
        setupSwitchButton()
        setupCloseButton()
        setupSaveButton(todoItem)
        observeDetailedWorkState()
    }

    private fun observeDetailedWorkState() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailedWorkViewModel.detailedWorkState.collect { newDetailedState ->
                    importanceLevel.text = newDetailedState.importanceLevel.name
                    deadline.text = newDetailedState.deadLine
                }
            }
        }
    }

    private fun setupSaveButton(todoItem: TodoItem?) {
        binding.saveButton.setOnClickListener {
            val eventType = if (todoItem == null) {
                DetailedWorkEvent.SaveData
            } else {
                DetailedWorkEvent.UpdateData(todoItem)
            }

            detailedWorkViewModel.onEvent(eventType)
            findNavController().navigate(R.id.action_detailedWorkFragment_to_myWorkFragment)
        }
    }

    private fun setupCloseButton() {
        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailedWorkFragment_to_myWorkFragment)
        }
    }

    private fun setupSwitchButton() {
        binding.switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDatePicker()
            } else {
                resetDeadline()
            }
        }
    }

    private fun setupImportanceLevelMenu() {
        binding.importanceLevel.setOnClickListener { view ->
            initImportanceLevelMenu(view)
        }
    }

    private fun setupDeleteItem(todoItem: TodoItem?) = with(binding.deleteItem) {
        visibility = if (todoItem != null) {
            binding.importanceLevel.text = todoItem.importanceLevel.name
            binding.deadline.text = todoItem.createDate.toString()
            binding.inputTodo.setText(todoItem.taskDescription)

            setOnClickListener {
                detailedWorkViewModel.onEvent(DetailedWorkEvent.RemoveData(todoItem))
                findNavController().navigate(R.id.action_detailedWorkFragment_to_myWorkFragment)
            }

            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a Date")
            .build()

        datePicker.addOnPositiveButtonClickListener { pickedDate ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = pickedDate

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            setDeadline("$year-$month-$dayOfMonth")
        }

        datePicker.addOnNegativeButtonClickListener {
            binding.switchButton.isChecked = false
        }

        datePicker.addOnCancelListener {
            binding.switchButton.isChecked = false
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun resetDeadline() {
        setDeadline("")
    }

    private fun setDeadline(deadline: String) {
        detailedWorkViewModel.onEvent(DetailedWorkEvent.OnChangedDeadLine(deadline))
    }

    private fun initImportanceLevelMenu(view: View) {
        val importanceLevelMenu = PopupMenu(requireContext(), view).apply {
            inflate(R.menu.importance_level_menu)
            show()
        }

        importanceLevelMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.importance_no -> {
                    detailedWorkViewModel.onEvent(
                        DetailedWorkEvent.OnChangedImportanceLevel(ImportanceLevel.LOW)
                    )
                    true
                }
                R.id.importance_low -> {
                    detailedWorkViewModel.onEvent(
                        DetailedWorkEvent.OnChangedImportanceLevel(ImportanceLevel.BASIC)
                    )
                    true
                }
                R.id.importance_tall -> {
                    detailedWorkViewModel.onEvent(
                        DetailedWorkEvent.OnChangedImportanceLevel(ImportanceLevel.IMPORTANT)
                    )
                    true
                }
                else -> false
            }
        }
    }

    private fun EditText.textChanges(text: String): Disposable {
        return RxTextView.textChanges(this)
            .skipInitialValue()
            .debounce(400, TimeUnit.MILLISECONDS)
            .startWith(text)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe({ description ->
                Log.i("TAG", description.toString())
                detailedWorkViewModel.onEvent(
                    DetailedWorkEvent.OnChangedTextDescription(description.toString())
                )
            }, {
                Log.e("Editable error", it.message.toString())
            })
    }

    private fun Disposable.addTo(disposeBag: CompositeDisposable) {
        disposeBag.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.clear()
        _binding = null
    }
}