package com.rf.taskmodule.ui.dynamicform.dynamicfragment

import com.rf.taskmodule.data.model.response.config.DynamicActionConfig
import com.rf.taskmodule.data.model.response.config.FormData

/**
 * Created by Rahul Abrol on 14/7/19.
 */
interface FormSubmitListener {

    fun onProcessClick(list: ArrayList<FormData>, dynamicActionConfig: DynamicActionConfig?, currentFormId: String?,dfdid:String?)
    fun showLoading()
}