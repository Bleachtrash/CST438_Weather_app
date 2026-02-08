package com.example.weather_app.ui.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.ui.platform.LocalFocusManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onOpenFavorite: (favoriteId: String) -> Unit = {}
) {
    val favorites by viewModel.favorites.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()


    var input by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var favoriteToDelete by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                actions = {
                    TextButton(onClick = { viewModel.toggleEditing() }) {
                        Text(if (isEditing) "Done" else "Edit")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded && suggestions.isNotEmpty(),
                        onExpandedChange = { expanded = it }
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = input,
                                onValueChange = {
                                    input = it
                                },
                                label = { Text("Add county (e.g., Monterey County, CA)") },
                                singleLine = true
                            )

                            if (suggestions.isNotEmpty() && input.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Card {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 220.dp)
                                    ) {
                                        items(suggestions) { suggestion ->
                                            ListItem(
                                                headlineContent = { Text(suggestion) },
                                                modifier = Modifier.clickable {
                                                    viewModel.addFavorite(suggestion)
                                                    input = ""
                                                    viewModel.clearQuery()
                                                    focusManager.clearFocus()
                                                }
                                            )
                                            Divider()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            viewModel.addFavorite(input)
                            input = ""
                            viewModel.clearQuery()
                        },
                        enabled = input.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (favorites.isEmpty()) {
                Text("No favorites yet.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favorites, key = { it.id }) { fav ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenFavorite(fav.id) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = fav.name,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleMedium
                                )

                                // Delete only in edit mode
                                if (isEditing) {
                                    IconButton(
                                        onClick = { favoriteToDelete = fav.id }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    favoriteToDelete?.let { favId ->
        AlertDialog(
            onDismissRequest = { favoriteToDelete = null },
            title = { Text("Remove favorite") },
            text = {
                val name = favorites.firstOrNull { it.id == favId }?.name ?: ""
                Text("Remove \"$name\" from favorites?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeFavorite(favId)
                        favoriteToDelete = null
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { favoriteToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

}
