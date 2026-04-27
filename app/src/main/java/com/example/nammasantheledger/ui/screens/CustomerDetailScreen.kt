package com.example.nammasantheledger.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammasantheledger.data.Customer
import com.example.nammasantheledger.ui.components.TransactionItem
import com.example.nammasantheledger.ui.theme.ErrorTomato
import com.example.nammasantheledger.ui.theme.PaidGreen
import com.example.nammasantheledger.viewmodel.TransactionViewModel
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Int,
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val transactions by viewModel.transactionsForCustomer(customerId).collectAsState()
    val balance by viewModel.netBalance(customerId).collectAsState()
    var customer by remember { mutableStateOf<Customer?>(null) }

    val animatedBalance by animateFloatAsState(targetValue = balance.toFloat(), label = "balance")

    LaunchedEffect(customerId) {
        customer = viewModel.getCustomerById(customerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Customer Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Customer Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = customer?.phone ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Current Balance",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                    Text(
                        text = "₹${String.format("%.2f", animatedBalance)}",
                        style = MaterialTheme.typography.displayMedium,
                        color = if (balance > 0) ErrorTomato else PaidGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Transaction History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⏳", style = MaterialTheme.typography.displayLarge)
                        Text("No transactions yet", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    customer?.let {
                        sendWhatsAppReminder(context, it.name, it.phone, balance)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)) // WhatsApp Green
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send WhatsApp Reminder")
            }
        }
    }
}

private fun sendWhatsAppReminder(context: Context, name: String, phone: String, amount: Double) {
    val message = "Hello $name, you have a pending due of ₹${String.format("%.2f", amount)} at Namma Santhe. Please clear when convenient. Thank you!"
    val i = Intent(Intent.ACTION_VIEW)
    try {
        val url = "https://api.whatsapp.com/send?phone=91$phone&text=" + URLEncoder.encode(message, "UTF-8")
        i.setPackage("com.whatsapp")
        i.data = Uri.parse(url)
        context.startActivity(i)
    } catch (e: Exception) {
        val url = "https://api.whatsapp.com/send?phone=91$phone&text=" + URLEncoder.encode(message, "UTF-8")
        i.data = Uri.parse(url)
        context.startActivity(i)
    }
}
