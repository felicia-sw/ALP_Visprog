package com.example.alp_visprog.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alp_visprog.uiStates.ProfileStatusUIState
import com.example.alp_visprog.viewModel.ProfileViewModel

@Composable
fun ProfileView() {
    val vm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
    var showEdit by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.fetchProfile() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = vm.profileStatus) {
            is ProfileStatusUIState.Loading -> CircularProgressIndicator()
            is ProfileStatusUIState.Failed -> Text(state.message)
            is ProfileStatusUIState.Success -> {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = state.profile.fullName,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "@${state.profile.username}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(state.profile.location)
                    state.profile.bio?.let { Text(it) }

                    Spacer(Modifier.height(16.dp))

                    Button(onClick = { showEdit = true }) {
                        Text("Edit Profil")
                    }
                }
            }
            else -> {}
        }
    }
}

/* ===============================
   PREVIEW DUMMY (UI ONLY)
   =============================== */

@Preview(showBackground = true)
@Composable
fun ProfileViewDummyPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "John Doe",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "@johndoe",
                style = MaterialTheme.typography.bodyMedium
            )
            Text("Surabaya, Indonesia")
            Text("Suka barter barang koleksi & elektronik.")

            Spacer(Modifier.height(16.dp))

            Button(onClick = {}) {
                Text("Edit Profil")
            }
        }
    }
}
