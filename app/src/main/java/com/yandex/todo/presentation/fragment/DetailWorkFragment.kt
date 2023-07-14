package com.yandex.todo.presentation.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yandex.todo.MyApp
import com.yandex.todo.R
import com.yandex.todo.domain.model.ImportanceLevel
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.presentation.event.DetailedWorkEvent
import com.yandex.todo.presentation.viewmodel.DetailedWorkViewModel
import com.yandex.todo.presentation.viewmodel.TodoViewModelFactory
import com.yandex.todo.utils.Blue
import com.yandex.todo.utils.BlueTranslucent
import com.yandex.todo.utils.ExtendedTheme
import com.yandex.todo.utils.GrayLight
import com.yandex.todo.utils.Red
import com.yandex.todo.utils.TodoAppTheme
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject


class DetailWorkFragment : Fragment() {
    @Inject
    lateinit var todoViewModelFactory: TodoViewModelFactory

    private val detailedWorkViewModel: DetailedWorkViewModel by viewModels(
        factoryProducer = {
            todoViewModelFactory
        }
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApp).appComponent
            .createTodoComponentFactory()
            .create()
            .injectDetailWorkFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detail_work, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<ComposeView>(R.id.compose_detailed_fragment).setContent {
            val todoItem = arguments?.getParcelable<TodoItem>("TODO_ITEM")
            if (todoItem != null) {
                detailedWorkViewModel.onEvent(DetailedWorkEvent.SetData(todoItem))
            }

            val state = detailedWorkViewModel.detailedWorkState.collectAsState()

            DetailedWorkScene(
                todoItem = todoItem,
                state = state,
                onNavigateUp = {
                    findNavController().navigate(R.id.action_detailWorkFragment_to_myWorkFragment)
                },
                onEvent = { event -> detailedWorkViewModel.onEvent(event) },
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DetailedWorkScene(
    todoItem: TodoItem? = null,
    state: State<DetailedWorkViewModel.DetailedState> = remember {
        mutableStateOf(
            DetailedWorkViewModel.DetailedState()
        )
    },
    onNavigateUp: () -> Unit = {},
    onEvent: (DetailedWorkEvent) -> Unit = {},
) {
    TodoAppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            containerColor = ExtendedTheme.colors.backPrimary,
            topBar = {
                TodoTaskTopBar( //Тулбар
                    onNavigateUp = { onNavigateUp() },
                    onAction = {
                        onEvent(
                            if (todoItem != null) {
                                DetailedWorkEvent.UpdateData(todoItem)
                            } else {
                                DetailedWorkEvent.SaveData
                            }
                        )
                        onNavigateUp()
                    }
                )
            },
            content = { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = padding.calculateTopPadding()),
                ) {
                    item {
                        InputFieldTask(state.value, onEvent) // Поле ввода
                        ChoiceImportantTask(state.value, onEvent) // Выбор приоритета
                        Divider(
                            color = GrayLight,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        ChoiceDateTask(state.value, onEvent) // Выбор времени
                        Divider(
                            color = GrayLight,
                            thickness = 1.dp,
                        )
                        DeleteTask(todoItem, onNavigateUp, onEvent) // Удаление таски
                    }
                }
            }
        )
    }
}


@Composable
private fun TodoTaskTopBar(
    onNavigateUp: () -> Unit,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 8.dp, end = 8.dp, top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onNavigateUp() }
                ),
            contentAlignment = Center,
            content = {
                Image(
                    painter = painterResource(id = R.drawable.icon_close),
                    contentDescription = "CLOSE",
                    colorFilter = ColorFilter.tint(ExtendedTheme.colors.labelPrimary),
                )
            }
        )

        Text(
            text = stringResource(id = R.string.save).uppercase(),
            style = ExtendedTheme.typography.titleSmall,
            color = Blue,
            modifier = Modifier
                .wrapContentSize(align = Center)
                .padding(end = 7.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onAction() }
                ),
        )
    }
}

@Composable
private fun InputFieldTask(
    inputText: DetailedWorkViewModel.DetailedState,
    onEvent: (DetailedWorkEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 12.dp)
            .wrapContentSize(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = ExtendedTheme.colors.backSecondary,
            contentColor = ExtendedTheme.colors.labelTertiary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        OutlinedTextField(
            value = inputText.description,
            onValueChange = { newInputText ->
                onEvent(DetailedWorkEvent.OnChangedTextDescription(newInputText))
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.what_needs_done),
                    color = ExtendedTheme.colors.labelSecondary,
                    style = ExtendedTheme.typography.body
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 120.dp),
        )
    }
}


@Composable
private fun ChoiceImportantTask(
    detailedState: DetailedWorkViewModel.DetailedState,
    onEvent: (DetailedWorkEvent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val items = listOf(
        stringResource(id = R.string.low),
        stringResource(id = R.string.basic),
        "!! ${stringResource(id = R.string.important)}"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { expanded = true }
            ),

        content = {
            Column {
                Text(
                    text = stringResource(id = R.string.important),
                    color = ExtendedTheme.colors.labelPrimary,
                    style = ExtendedTheme.typography.body
                )

                Text(
                    text = getImportanceLevelToString(items, detailedState.importanceLevel),
                    color = ExtendedTheme.colors.labelSecondary,
                    style = ExtendedTheme.typography.subhead
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .size(width = 164.dp, height = 165.dp)
                        .background(ExtendedTheme.colors.backSecondary),
                    content = {
                        items.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item,
                                        style = TextStyle(
                                            color = if (index == 2) Color.Red else ExtendedTheme.colors.labelSecondary
                                        )
                                    )
                                },
                                onClick = {
                                    onEvent(
                                        DetailedWorkEvent.OnChangedImportanceLevel(
                                            enumValueOf(
                                                when (index) {
                                                    0 -> "LOW"
                                                    1 -> "BASIC"
                                                    2 -> "IMPORTANT"
                                                    else -> ""
                                                }
                                            )
                                        )
                                    )
                                    expanded = false
                                }
                            )
                        }
                    }
                )
            }
        }
    )
}

private fun getImportanceLevelToString(
    items: List<String>,
    importanceLevel: ImportanceLevel
): String {
    return when (importanceLevel.name.lowercase()) {
        "low" -> items[0]
        "basic" -> items[1]
        "important" -> items[2]
        else -> ""
    }
}

@Composable
private fun ChoiceDateTask(
    detailedState: DetailedWorkViewModel.DetailedState,
    onEvent: (DetailedWorkEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = CenterVertically
    ) {
        Box(content = {
            Column {
                Text(
                    text = stringResource(id = R.string.make_up_to),
                    color = ExtendedTheme.colors.labelPrimary,
                    style = ExtendedTheme.typography.body
                )

                Text(
                    text = detailedState.deadLine,
                    color = Blue,
                    style = ExtendedTheme.typography.subhead
                )
            }
        })

        var checkedState by rememberSaveable { mutableStateOf<Boolean>(false) }
        val context = LocalContext.current

        Switch(
            checked = checkedState,
            onCheckedChange = { newState ->
                if (newState) {
                    showDataPicker(context, onEvent)
                } else {
                    onEvent(DetailedWorkEvent.OnChangedDeadLine(""))
                }

                checkedState = newState
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Blue,
                uncheckedThumbColor = Blue,
                checkedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent,
                checkedTrackColor = BlueTranslucent,
                uncheckedTrackColor = GrayLight
            ),
        )
    }
}


private fun showDataPicker(
    context: Context,
    onEvent: (DetailedWorkEvent) -> Unit
) {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onEvent(DetailedWorkEvent.OnChangedDeadLine("$dayOfMonth/$month/$year"))
        }, year, month, dayOfMonth
    ).show()
}

@Composable
private fun DeleteTask(
    todoItem: TodoItem?,
    onNavigateUp: () -> Unit,
    onEvent: (DetailedWorkEvent) -> Unit,
) {
    val backgroundColor = if (todoItem != null) Red else GrayLight

    Box(
        modifier = Modifier
            .size(width = 115.dp, height = 50.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (todoItem != null) {
                        onEvent(DetailedWorkEvent.RemoveData(todoItem))
                        onNavigateUp()
                    }
                }
            ),
        contentAlignment = Center
    ) {
        Row(verticalAlignment = CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.icon_delete),
                contentDescription = "DELETE",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(backgroundColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(id = R.string.delete),
                color = backgroundColor,
                style = ExtendedTheme.typography.body,
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}