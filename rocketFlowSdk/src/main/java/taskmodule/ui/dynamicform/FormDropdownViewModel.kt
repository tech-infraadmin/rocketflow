package taskmodule.ui.dynamicform

import androidx.databinding.ObservableField
import taskmodule.data.model.response.config.FormData

/**
 * Created by Rahul Abrol on 15/1/20.
 */
class FormDropdownViewModel(formData: FormData) {

    val title = ObservableField("")

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
    }
}