package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Customer
import com.example.model.Transaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KhataViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("khata_db_promax")

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _totalOutstanding = MutableStateFlow(0.0)
    val totalOutstanding: StateFlow<Double> = _totalOutstanding.asStateFlow()
    
    init {
        fetchCustomers()
    }

    private fun fetchCustomers() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Customer>()
                var outstanding = 0.0
                for (childSnapshot in snapshot.children) {
                    val customer = childSnapshot.getValue(Customer::class.java)
                    if (customer != null) {
                        list.add(customer)
                        if (customer.balance > 0) {
                            outstanding += customer.balance // Udhaar is positive
                        } else {
                            outstanding += customer.balance // Jama is negative
                        }
                    }
                }
                _customers.value = list
                _totalOutstanding.value = outstanding
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    fun addCustomer(name: String, phone: String): String {
        val id = "u_${System.currentTimeMillis()}"
        val customer = Customer(id = id, name = name, phone = phone, balance = 0.0, history = emptyList())
        val index = _customers.value.size
        myRef.child(index.toString()).setValue(customer)
        return id
    }

    fun addTransaction(customerId: String, amount: Double, desc: String, type: String) {
        val customerIndex = _customers.value.indexOfFirst { it.id == customerId }
        val customer = _customers.value.find { it.id == customerId } ?: return
        
        val timeFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date())
        
        val transaction = Transaction(
            id = System.currentTimeMillis(),
            amt = amount,
            desc = desc,
            type = type, // "udhaari" or "jama"
            time = timeStr
        )
        
        val newHistory = customer.history.toMutableList()
        newHistory.add(0, transaction) // Add to top usually, but let's just append or add to index 0. Actually firebase arrays are lists, we should just overwrite the history.
        
        val newBalance = if (type == "udhaari") {
            customer.balance + amount
        } else {
            customer.balance - amount
        }
        
        val updatedCustomer = customer.copy(balance = newBalance, history = newHistory)
        myRef.child(customerIndex.toString()).setValue(updatedCustomer)
    }

    fun deleteTransaction(customerId: String, transactionId: Long) {
        val customerIndex = _customers.value.indexOfFirst { it.id == customerId }
        val customer = _customers.value.find { it.id == customerId } ?: return
        
        val transaction = customer.history.find { it.id == transactionId } ?: return
        
        val newHistory = customer.history.filter { it.id != transactionId }
        
        val newBalance = if (transaction.type == "udhaari") {
            customer.balance - transaction.amt
        } else {
            customer.balance + transaction.amt
        }
        
        val updatedCustomer = customer.copy(balance = newBalance, history = newHistory)
        myRef.child(customerIndex.toString()).setValue(updatedCustomer)
    }
}
