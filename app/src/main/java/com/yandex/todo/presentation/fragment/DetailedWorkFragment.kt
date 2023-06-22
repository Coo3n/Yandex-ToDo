package com.yandex.todo.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.coroutineScope
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

    private val disposeBag = CompositeDisposable()
    private lateinit var detailedWorkViewModel: DetailedWorkViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApp).appComponent.injectDetailedWorkFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailedWorkViewModel = ViewModelProvider(
            this,
            todoViewModelFactory
        )[DetailedWorkViewModel::class.java]
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
        with(binding) {
            val todoItem = arguments?.getParcelable<TodoItem>("TODO_ITEM")

            if (todoItem != null) {
                Log.i("sdd", todoItem.toString())

                importanceLevel.text = todoItem.importanceLevel.name
                deadline.text = todoItem.createDate.toString()
                inputTodo.setText(todoItem.taskDescription)

                deleteItem.setOnClickListener {
                    detailedWorkViewModel.onEvent(DetailedWorkEvent.RemoveData(todoItem))
                    findNavController().navigateUp()
                }
            }

            inputTodo.textChanges().addTo(disposeBag)

            importanceLevel.setOnClickListener { view ->
                initImportanceLevelMenu(view)
            }

            switchButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    showDatePicker()
                } else {
                    resetDeadline()
                }
            }

            closeButton.setOnClickListener {
                findNavController().navigateUp()
            }

            saveButton.setOnClickListener {
                detailedWorkViewModel.onEvent(DetailedWorkEvent.SaveData)
                findNavController().navigateUp()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailedWorkViewModel.detailedWorkState.collect { newDetailedState ->
                        importanceLevel.text = newDetailedState.importanceLevel.name
                        deadline.text = newDetailedState.deadLine
                    }
                }
            }
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
                        DetailedWorkEvent.OnChangedImportanceLevel(ImportanceLevel.NO)
                    )
                    true
                }
                R.id.importance_low -> {
                    detailedWorkViewModel.onEvent(
                        DetailedWorkEvent.OnChangedImportanceLevel(ImportanceLevel.LOW)
                    )
                    true
                }
                R.id.importance_tall -> {
                    detailedWorkViewModel.onEvent(
                        DetailedWorkEvent.OnChangedImportanceLevel(ImportanceLevel.TALL)
                    )
                    true
                }
                else -> false
            }
        }
    }

    private fun EditText.textChanges(): Disposable {
        return RxTextView.textChanges(this)
            .skipInitialValue()
            .debounce(500, TimeUnit.MILLISECONDS)
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