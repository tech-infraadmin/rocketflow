package com.rf.taskmodule.ui.dynamicform

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rf.taskmodule.data.model.response.config.FormData


class FormAudioViewModel(val formData: FormData) {
    val title = ObservableField("")
    val position = ObservableInt(0)

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
    }
}