package com.example.nammasantheledger.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammasantheledger.data.Customer
import com.example.nammasantheledger.ui.components.BottomNavigationBar
import com.example.nammasantheledger.ui.theme.ErrorTomato
import com.example.nammasantheledger.ui.theme.PaidGreen
import com.example.nammasantheledger.viewmodel.CustomerViewModel

@Composable
fun CustomerListScreen(
    viewModel: CustomerViewModel,
    onCustomerClick: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCustomers: () -> Unit
) {
    val customers by viewModel.filteredCustomers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "customer_list",
                onNavigateToHome = onNavigateToHome,
                onNavigateToCustomers = onNavigateToCustomers
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "My Customers",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search customers...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            if (customers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👥", fontSize = 48.sp)
                        Text("No customers found", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(customers) { customer ->
                        val balance by viewModel.getCustomerBalance(customer.id).collectAsState(initial = 0.0)
                        CustomerCard(customer, balance, onClick = { onCustomerClick(customer.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerCard(customer: Customer, balance: Double, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = customer.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (balance > 0) "DUE" else "CLEARED",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (balance > 0) ErrorTomato else PaidGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹${String.format("%.2f", balance)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (balance > 0) ErrorTomato else PaidGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
