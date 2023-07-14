package com.yandex.todo

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yandex.todo.data.local.ThemeManager
import com.yandex.todo.data.remote.notification.NotificationManager
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

        checkPermissionNotification()
        initInternetMonitoring()
    }


    private fun checkPermissionNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(applicationContext, "Лучше разреши!", Toast.LENGTH_LONG).show()
                }
            }.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
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