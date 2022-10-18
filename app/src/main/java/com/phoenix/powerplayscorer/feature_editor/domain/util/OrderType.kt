package com.phoenix.powerplayscorer.feature_editor.domain.util

sealed class OrderType{
    object Ascending: OrderType()
    object Descending: OrderType()
}
