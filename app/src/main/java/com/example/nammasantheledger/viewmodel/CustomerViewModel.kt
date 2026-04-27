package com.example.nammasantheledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nammasantheledger.data.Customer
import com.example.nammasantheledger.data.LedgerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: LedgerRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredCustomers: StateFlow<List<Customer>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allCustomers
            } else {
                repository.searchCustomers(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addCustomer(name: String, phone: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertCustomer(Customer(name = name, phone = phone))
            onComplete()
        }
    }
    
    fun getCustomerBalance(customerId: Int): Flow<Double> {
        return repository.getCustomerBalance(customerId).map { it ?: 0.0 }
    }
}

class CustomerViewModelFactory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
