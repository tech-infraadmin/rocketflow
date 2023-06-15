package com.rf.taskmodule.ui.dynamicform

import android.os.Handler
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.rf.taskmodule.data.model.response.config.FormData
import com.rf.taskmodule.utils.Log

class FormButtonViewModel(val formData: FormData, onButtonClickListener: OnButtonClickListener) :  ViewModel() {
    val title = ObservableField("Proceed")
    val isEnable = ObservableBoolean(true)

    private var onButtonClickListener: OnButtonClickListener? = onButtonClickListener

    init {
        if (formData.value != null) {
            title.set(formData.value)
            Log.d("formId", "FormButtonViewModel >>>>>> >>>>>> $formData")
        }
    }

    fun onButtonClick() {
        try {
            if (isEnable.get()) {
                isEnable.set(false)
            }
            Handler().postDelayed({
                isEnable.set(true)
            }, 3000)
            onButtonClickListener?.onClickButton(formData)
            Log.d("formId", "FormButtonViewModel click >>>>>> >>>>>> $formData")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnButtonClickListener {
        fun onClickButton(formData: FormData)
    }
}
