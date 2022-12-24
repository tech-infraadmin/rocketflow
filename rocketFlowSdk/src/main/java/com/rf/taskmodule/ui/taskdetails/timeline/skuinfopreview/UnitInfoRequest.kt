package com.rf.taskmodule.ui.taskdetails.timeline.skuinfopreview
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rf.taskmodule.data.model.response.config.DynamicFormData
import com.rf.taskmodule.ui.category.ProductDescription
import com.rf.taskmodule.utils.ShiftTime
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UnitInfoRequest(
    var id: String? = null,
    var filterBy: String? = null,
    var pageNumber: Int? = null,
    var limit: Int? = null,
    var pids: ArrayList<String>? = null,
    ):Parcelable
