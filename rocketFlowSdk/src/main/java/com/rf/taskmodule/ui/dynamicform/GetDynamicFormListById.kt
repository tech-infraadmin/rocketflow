package com.rf.taskmodule.ui.dynamicform

import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.response.config.DataType
import com.rf.taskmodule.data.model.response.config.FormData
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.Log
import java.io.File

class GetDynamicFormListById {
    var preferencesHelper: com.rf.taskmodule.data.local.prefs.PreferencesHelper? = null
//    init {
//        preferencesHelper= AppPreferencesHelper(TrackiApplication.instance, AppConstants.PREF_NAME);
//    }

    fun getDynamicFormListById(preferencesHelper: com.rf.taskmodule.data.local.prefs.PreferencesHelper, formId: String): ArrayList<FormData> {
        com.rf.taskmodule.utils.CommonUtils.showLogMessage("e", "formId", formId)
        this.preferencesHelper = preferencesHelper;
        var formDataList = ArrayList<FormData>()
        var dynamicFormsNew = com.rf.taskmodule.utils.CommonUtils.getFormByFormId(formId!!)
        if (dynamicFormsNew != null) {
//                        var jsonConverter= JSONConverter<DynamicFormsNew>()
//                        var json=jsonConverter.objectToJson(dynamicFormsNew)
//                        Log.e("form json",json)
//            Log.e("form_id",formId)
            val dat = dynamicFormsNew?.fields!!
            formDataList = ArrayList()
            for (i in dat.indices) {
                val formData = FormData()
                formData.type = dat[i].type
                formData.name = dat[i].name
                formData.title = dat[i].title
                formData.placeHolder = dat[i].placeHolder
                formData.required = dat[i].required
                formData.operation = dat[i].operation
                formData.includeForCalculation = dat[i].includeForCalculation
                formData.errorMessage = dat[i].errorMessage
                //formData.errorMessage = "Enter "+dat[i].title
                formData.maxLength = dat[i].maxLength
                formData.maxRange = dat[i].maxRange
                formData.minRange = dat[i].minRange
                formData.actionConfig = dat[i].actionConfig
                formData.file = dat[i].file
                formData.widgetData = dat[i].widgetData
                formData.value = dat[i].value
                formData.readOnly = dat[i].readOnly
                formData.minLength = dat[i].minLength
                if (preferencesHelper != null && preferencesHelper!!.formDataMap != null && preferencesHelper!!.formDataMap.isNotEmpty()) {
                    var previousDataList = preferencesHelper!!.formDataMap[formId]
                    com.rf.taskmodule.utils.Log.e("preferencesHelper", "notnull")
                    if (previousDataList != null && previousDataList.isNotEmpty()) {
                        for (j in previousDataList.indices) {
                            if (formData.name!! == previousDataList[j].name) {
                                formData.value = previousDataList[j].value
                                formData.maxRange = previousDataList[j].maxRange
                                formData.minRange = previousDataList[j].minRange
                                formData.widgetData = previousDataList[j].widgetData
                                if (formData.type == DataType.SIGNATURE || formData.type == DataType.CAMERA || formData.type == DataType.VIDEO || formData.type == DataType.FILE) {
                                    if (previousDataList[j].file != null) {
                                        var listOfFile = ArrayList<File>()
                                        for (dfile in previousDataList[j].file!!) {
                                            if (dfile.exists()) {
                                                listOfFile.add(dfile)
                                            }
                                        }
                                        if(listOfFile.isNotEmpty()){
                                            formData.enteredValue = previousDataList[j].enteredValue
                                            formData.file = listOfFile
                                        }

                                    }
                                }else{
                                    formData.enteredValue = previousDataList[j].enteredValue
                                }
                            }

                        }
                    }

                } else {
                    formData.maxRange = dat[i].maxRange
                    formData.minRange = dat[i].minRange
                    formData.value = dat[i].value
                    formData.widgetData = dat[i].widgetData
                }

                formData.isembded = true
                formData.embeddedDfId = formId
                formData.weight = dat[i].weight
                formData.roles = dat[i].roles
                formData.dynamicSelectLookup = dat[i].dynamicSelectLookup
                formDataList.add(formData)
            }
        }
        return formDataList

    }
}