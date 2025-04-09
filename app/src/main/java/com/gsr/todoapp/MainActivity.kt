package com.gsr.todoapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gsr.todoapp.ui.theme.ToDoAppTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Github
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialTheme = ThemeStorage.loadTheme(this)
        setContent {
            var isDarkTheme by remember { mutableStateOf(initialTheme) }
            ToDoAppTheme(darkTheme = isDarkTheme, dynamicColor = false) {
                MainScreen(
                    isDarkTheme = isDarkTheme,
                    toggleTheme = {
                        isDarkTheme = !isDarkTheme
                        ThemeStorage.saveTheme(this, isDarkTheme)
                    }
                )
            }
        }
    }
}

data class Task(val id: Int, val text: String)

@Composable
fun TaskItem(task: Task, onChecked: () -> Unit) {
    var checked by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                checked = it
                if (it) onChecked()
            },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,
                uncheckedColor = Color.White,
                checkmarkColor = MaterialTheme.colorScheme.primary,
                disabledCheckedColor = Color(0xFFBDBDBD),
                disabledUncheckedColor = Color(0xFFBDBDBD),
                disabledIndeterminateColor = Color(0xFFBDBDBD)
            )
        )
        Text(text = task.text, color = Color.White)
    }
}

object TaskStorage {
    private const val PREFS_NAME = "task_prefs"
    private const val KEY_TASKS = "tasks"

    fun saveTasks(context: Context, taskList: List<Task>) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(taskList)
        prefs.edit().putString(KEY_TASKS, json).apply()
    }

    fun loadTasks(context: Context): MutableList<Task> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_TASKS, null)
        return try {
            if (json != null) {
                val type = object : TypeToken<MutableList<Task>>() {}.type
                Gson().fromJson(json, type)
            } else {
                mutableListOf()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }
}

object ThemeStorage {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_IS_DARK = "is_dark_theme"

    fun saveTheme(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_DARK, isDark).apply()
    }

    fun loadTheme(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_DARK, false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(isDarkTheme: Boolean, toggleTheme: () -> Unit ){
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val taskList = remember { mutableStateListOf<Task>().apply { addAll(TaskStorage.loadTasks(context)) } }
//    val taskList = remember {
//        mutableStateListOf(
//            Task(id = 1, text = "Buy groceries"),
//            Task(id = 2, text = "Finish project"),
//            Task(id = 3, text = "Call Mom")
//        )
//    }
    //var greetingText by remember { mutableStateOf("World!") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = { Text(text = "To Do App", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            actions = {
                IconButton(onClick = { toggleTheme()
                    Toast.makeText(
                        context,
                        if (isDarkTheme) "Light Theme Applied" else "Dark Theme Applied",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.DarkMode,
                        contentDescription = "Toggle Dark Mode",
                        tint = Color.White
                    )
                }
                var showCreditsDialog by remember { mutableStateOf(false) }
                if (showCreditsDialog) {
                    AlertDialog(
                        onDismissRequest = { showCreditsDialog = false },
                        title = {
                            Column (
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = if (isDarkTheme) Color.White else Color.Black)
                                Text(
                                    "Credits",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        text = {
                            Column (
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ){
                                HorizontalDivider( modifier = Modifier.fillMaxWidth())
                                Text("Developed by Govind Sankar using Kotlin & Jetpack Compose!", textAlign = TextAlign.Center)
                                HorizontalDivider( modifier = Modifier.fillMaxWidth())
                                Column (
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = FontAwesomeIcons.Brands.Github,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp),
                                            tint = if (isDarkTheme) Color.White else Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(20.dp))
                                        Text("Govind-Sankar", fontSize = 12.sp)
                                    }
                                }
                                HorizontalDivider( modifier = Modifier.fillMaxWidth())
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showCreditsDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }
                IconButton( onClick = {
                    showCreditsDialog = !showCreditsDialog
                    Toast.makeText(
                        context,
                        "Credits",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Credits",
                        tint = Color.White
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxHeight(fraction = 0.10f),
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Task") },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            )
            Box(modifier = Modifier.padding(all = 7.dp)) {
                ElevatedButton(
                    modifier = Modifier.fillMaxHeight(fraction = 0.9f),
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val newTask = Task(id = Random.nextInt(), text = inputText)
                            taskList.add(newTask)
                            TaskStorage.saveTasks(context, taskList)
                            inputText = ""
                        }
                    },
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = if (MaterialTheme.colorScheme.primary.luminance() > 0.5f) Color.Black else Color.White
                    )
                ) {
                    Text(text = "+")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box (
            modifier = Modifier.fillMaxSize(fraction = 0.9f)
        ){
            LazyColumn {
                items(taskList, key = { it.id }) { task ->
                    TaskItem(task = task, onChecked = {
                        taskList.remove(task)
                        TaskStorage.saveTasks(context, taskList)
                    })
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    var isDarkTheme by remember { mutableStateOf(false) }
    ToDoAppTheme(darkTheme = isDarkTheme, dynamicColor = false) {
        MainScreen(isDarkTheme = isDarkTheme, toggleTheme = { isDarkTheme = !isDarkTheme })
    }
}