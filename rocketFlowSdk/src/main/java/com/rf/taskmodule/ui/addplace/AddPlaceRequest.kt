package com.rf.taskmodule.ui.addplace

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddPlaceRequest(
    var cityId: String?,
    var hubId: String?,
    var hubLocation: HubLocation?,
    var name: String?,
    var regionId: String?
):Parcelable

@Parcelize
data class HubLocation(
    var location: Location?,
    var radius: Float? = 0.0F
):Parcelable{
    var address:String?=null
}

@Parcelize
data class Location(
    var latitude: Double?,
    var longitude: Double?
):Parcelable{
    var locationId: String? =""
    var coordinates: ArrayList<Double>? = ArrayList()
}