package com.phoenix.powerplayscorer.feature_editor.presentation.list

import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.util.Order
import com.phoenix.powerplayscorer.feature_editor.domain.util.OrderType

data class ListState(
    val list: List<Match> = emptyList(),
    val order: Order = Order.Date(OrderType.Descending),
    val selectedItems: List<String> = emptyList()
)
