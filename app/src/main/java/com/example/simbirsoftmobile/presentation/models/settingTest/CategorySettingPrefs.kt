package com.example.simbirsoftmobile.presentation.models.settingTest

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategorySettingPrefs(
    val id: String,
    val isSelected: Boolean,
) : Parcelable
