package com.yandex.todo

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.yandex.todo.presentation.viewmodel.NetworkStatusViewModel
import com.yandex.todo.presentation.viewmodel.TodoViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var todoViewModelFactory: TodoViewModelFactory
    private val networkStatusViewModel: NetworkStatusViewModel by viewModels(
        factoryProducer = {
            todoViewModelFactory
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as MyApp).appComponent
            .createTodoComponentFactory()
            .create()
            .injectMainActivity(this)

        initInternetMonitoring()
    }

    private fun initInternetMonitoring() {
        lifecycleScope.launch {
            networkStatusViewModel.networkStatus.collect { isConnected ->
                findViewById<LinearLayoutCompat>(R.id.connection_state).visibility =
                    if (!isConnected) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
        }
    }
}