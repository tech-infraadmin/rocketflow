package com.rf.taskmodule.ui.cart

class CartRequest {
  //  var data: Map<String,Int>?=null
    var data:List<CartProduct>?=null
}

class CartProduct{
    var productId:String?=null
    var quantity:Int=0
}