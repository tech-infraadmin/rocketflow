package taskmodule.ui.dynamicform.dynamicfragment

import androidx.databinding.ObservableField
import taskmodule.data.model.response.config.TaskData


/**
 * Created by Vikas Kesharvani on 29/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class FormFileViewModel {

    val title: ObservableField<String> = ObservableField("")
    val formData: TaskData

    constructor(formData: TaskData) {
        this.formData = formData
        if (formData.label != null) {
            title.set(formData.label)
        }
    }
}