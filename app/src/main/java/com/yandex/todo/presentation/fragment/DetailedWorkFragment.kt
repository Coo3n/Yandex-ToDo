package com.yandex.todo.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding2.widget.RxTextView
import com.yandex.todo.MyApp
import com.yandex.todo.databinding.FragmentDetailedWorkBinding
import com.yandex.todo.presentation.event.DetailedWorkEvent
import com.yandex.todo.presentation.viewmodel.DetailedWorkViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class DetailedWorkFragment : Fragment() {
    private var _binding: FragmentDetailedWorkBinding? = null
    private val binding: FragmentDetailedWorkBinding
        get() = _binding!!

    private val disposeBag = CompositeDisposable()
    private lateinit var detailedWorkViewModel: DetailedWorkViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApp).appComponent.injectDetailedWorkFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailedWorkViewModel = ViewModelProvider(this)[DetailedWorkViewModel::class.java]
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
        binding.inputTodo.textChanges().addTo(disposeBag)
    }

    private fun EditText.textChanges(): Disposable {
        return RxTextView.textChanges(this)
            .debounce(400, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe({ description ->
                Log.i("TAG", description.toString())
                detailedWorkViewModel.onEvent(
                    DetailedWorkEvent.OnChangedTextDescription(description.toString())
                )
            }, {
                Log.e("Editable", it.message.toString())
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