package com.example.simbirsoftmobile.presentation.models.settingTest

import android.os.Parcelable
import com.example.simbirsoftmobile.domain.models.CategorySettingModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategorySettingUi(
    val id: String,
    val name: String,
    val nameEn: String,
    var isSelected: Boolean,
) : Parcelable {
    fun toPrefs(): CategorySettingPrefs {
        return CategorySettingPrefs(id, isSelected)
    }
}

fun CategorySettingModel.toUi(): CategorySettingUi {
    return CategorySettingUi(id, name, nameEn, isSelected)
}
