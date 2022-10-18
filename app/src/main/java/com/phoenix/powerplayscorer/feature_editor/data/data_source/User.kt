package com.phoenix.powerplayscorer.feature_editor.data.data_source

import androidx.annotation.Keep

@Keep
data class User(
    val matchesIds: List<String> = emptyList()
)
