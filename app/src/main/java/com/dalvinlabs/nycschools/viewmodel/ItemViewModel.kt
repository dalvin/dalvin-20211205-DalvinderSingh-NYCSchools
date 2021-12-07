package com.dalvinlabs.nycschools.viewmodel

import android.content.Context
import androidx.databinding.ObservableField
import com.dalvinlabs.nycschools.R
import com.dalvinlabs.nycschools.model.SchoolWithDetails

class ItemViewModel constructor(
    private val schoolWithDetails: SchoolWithDetails
) {
    val title: String = schoolWithDetails.name
    val isDetailsVisible: ObservableField<Boolean> = ObservableField(false)

    fun onClickTitle() {
        isDetailsVisible.set(isDetailsVisible.get()?.not())
    }

    fun getDetails(context: Context): String {
        return context.getString(R.string.details).format(
            schoolWithDetails.reading,
            schoolWithDetails.writing,
            schoolWithDetails.math
        )
    }
}