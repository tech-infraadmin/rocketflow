package com.rf.taskmodule.ui.cart

import android.os.Parcelable
import com.rf.taskmodule.ui.selectorder.SubUnit
import kotlinx.android.parcel.Parcelize

class CartRequest {
  //  var data: Map<String,Int>?=null
    var data:List<CartProduct>?=null
}

class CartProduct{
    var dimensionEnabled: Boolean? = false
    var price: Float? = 0F
    var subUnit: ArrayList<SubUnitC>? = null
    var productId: String? = ""
    var quantity: Int = 0
}

class SubUnitC {
    var actualPrice: Float? = 0F
    var dimension: DimensionProductC? = null
    var order: Int? = 0
    var sellingPrice: Float? = 0F
}

class DimensionProductC {
    var area: Float? = 0F
    var length: Float? = 0F
    var width: Float? = 0F
}