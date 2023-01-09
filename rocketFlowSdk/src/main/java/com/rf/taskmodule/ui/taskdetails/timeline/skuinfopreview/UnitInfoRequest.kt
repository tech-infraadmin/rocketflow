package com.rf.taskmodule.ui.taskdetails.timeline.skuinfopreview
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UnitInfoRequest(
    var id: String? = null,
    var filterBy: String? = null,
    var pageNumber: Int? = null,
    var limit: Int? = null,
    var pids: ArrayList<String>? = null,
    ):Parcelable
