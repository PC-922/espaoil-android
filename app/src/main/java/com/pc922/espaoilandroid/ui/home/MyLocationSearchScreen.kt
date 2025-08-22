package com.pc922.espaoilandroid.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.ui.res.stringResource
import com.pc922.espaoilandroid.data.FakeGasStationRepository
import com.pc922.espaoilandroid.location.FakeLocationProvider
import com.pc922.espaoilandroid.model.AuthorizationState
import com.pc922.espaoilandroid.model.FuelType
import com.pc922.espaoilandroid.model.SortOption
import com.pc922.espaoilandroid.ui.components.DistanceTextField
import com.pc922.espaoilandroid.ui.components.GasStationRow
import com.pc922.espaoilandroid.ui.components.SortSegmentedControl
import com.pc922.espaoilandroid.ui.components.StatusIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLocationSearchRoute(
    modifier: Modifier = Modifier,
    vm: MyLocationSearchViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                MyLocationSearchViewModel(
                    repository = FakeGasStationRepository(),
                    locationProvider = FakeLocationProvider(authorize = true)
                )
            }
        }
    )
    ) {
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(state.title, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { padding ->
        Content(
            state = state,
            onFuelTypeSelected = vm::onFuelTypeSelected,
            onRadiusChanged = vm::onRadiusChanged,
            onSearchClick = vm::search,
            onSortChange = vm::onSortOptionSelected,
            modifier = modifier,
            innerPadding = padding
        )
    }
}

@Composable
private fun Content(
    state: MyLocationSearchUiState,
    onFuelTypeSelected: (FuelType) -> Unit,
    onRadiusChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSortChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Top insets from Scaffold and reduced spacing under title
        Spacer(Modifier.height(innerPadding.calculateTopPadding()))
        Spacer(Modifier.height(4.dp))

        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Header(
                state = state,
                onFuelTypeSelected = onFuelTypeSelected,
                onRadiusChanged = onRadiusChanged,
                onSearchClick = onSearchClick
            )
        }

        Spacer(Modifier.height(12.dp))
        if (state.authorizationState != AuthorizationState.AUTHORIZED) {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                StatusRow(state)
            }
        }

        Spacer(Modifier.height(12.dp))

        Crossfade(
            targetState = Triple(state.isLoadingLocation, state.isLoadingStations, state.stations.isNotEmpty()),
            label = "content"
        ) { (loadingLocation, loadingStations, hasResults) ->
            when {
                loadingLocation -> LoadingLocationView()
                loadingStations -> LoadingStationsView()
                state.locationErrorMessage != null -> ErrorView(state.locationErrorMessage!!)
                state.stationsErrorMessage != null -> ErrorView(state.stationsErrorMessage!!)
                hasResults -> ResultsList(
                    state = state,
                    onSortChange = onSortChange,
                    bottomInset = innerPadding.calculateBottomPadding()
                )
                else -> EmptyView()
            }
        }
    }
}

@Composable
private fun Header(
    state: MyLocationSearchUiState,
    onFuelTypeSelected: (FuelType) -> Unit,
    onRadiusChanged: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(id = com.pc922.espaoilandroid.R.string.fuel_type_label), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            FuelTypeInlineSelector(
                selected = state.selectedFuelType,
                onSelected = onFuelTypeSelected
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(id = com.pc922.espaoilandroid.R.string.search_radius_label), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            DistanceTextField(
                value = state.radiusKmInput,
                onValueChange = onRadiusChanged,
                modifier = Modifier.width(84.dp),
                showLabel = false
            )
        }

        Button(
            onClick = onSearchClick,
            enabled = !state.isLoadingLocation && !state.isLoadingStations,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Icon(Icons.Outlined.Navigation, contentDescription = null)
            Text(
                text = if (state.isLoadingLocation) stringResource(id = com.pc922.espaoilandroid.R.string.verifying_location) else stringResource(id = com.pc922.espaoilandroid.R.string.search_gas_stations),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun FuelTypeInlineSelector(
    selected: FuelType,
    onSelected: (FuelType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = selected.displayName + " \u25BE",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            FuelType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.displayName) },
                    onClick = {
                        onSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusRow(state: MyLocationSearchUiState) {
    val statusText = when (state.authorizationState) {
        AuthorizationState.AUTHORIZED -> stringResource(id = com.pc922.espaoilandroid.R.string.ready_to_search)
        AuthorizationState.DENIED -> stringResource(id = com.pc922.espaoilandroid.R.string.maps_not_found)
        AuthorizationState.NOT_DETERMINED -> stringResource(id = com.pc922.espaoilandroid.R.string.verifying_location)
        AuthorizationState.LOADING -> stringResource(id = com.pc922.espaoilandroid.R.string.verifying_location)
    }
    StatusIndicator(state.authorizationState, statusText, modifier = Modifier.padding(horizontal = 8.dp))
}

@Composable
private fun ColumnScope.ResultsList(
    state: MyLocationSearchUiState,
    onSortChange: (SortOption) -> Unit,
    bottomInset: Dp
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(0.dp),
        shadowElevation = 0.dp,
        modifier = Modifier.weight(1f)
    ) {
        Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp + bottomInset)) {
            Text(stringResource(id = com.pc922.espaoilandroid.R.string.sort_by), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SortSegmentedControl(
                    selected = state.sortOption,
                    onSelected = onSortChange,
                    modifier = Modifier.weight(1f)
                )
                val helperText = when (state.sortOption) {
                    SortOption.PRICE -> stringResource(id = com.pc922.espaoilandroid.R.string.helper_price)
                    SortOption.DISTANCE -> stringResource(id = com.pc922.espaoilandroid.R.string.helper_distance)
                }
                AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                    Text(helperText, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(state.stations, key = { it.id }) { station ->
                    GasStationRow(station = station, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun LoadingLocationView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Buscando tu ubicación...")
    }
}

@Composable
private fun LoadingStationsView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Buscando gasolineras cercanas...")
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun EmptyView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No hay datos para mostrar")
    }
}