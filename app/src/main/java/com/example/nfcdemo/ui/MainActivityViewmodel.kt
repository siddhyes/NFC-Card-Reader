package com.example.nfcdemo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainActivityViewmodel : ViewModel() {
    var stateFlow by mutableStateOf(CardData())
        private set

    fun upDateTheValue(data: CardData) {
        stateFlow = data
    }
}

data class CardData(
    val cradNumber: String? = "",
    val experyDate: String? = "",
    val isLoading: Boolean = true,
    val isNFCPresent:Boolean=true,
    val isNFCEnable:Boolean=false,
    val cardType: String = ""
)
