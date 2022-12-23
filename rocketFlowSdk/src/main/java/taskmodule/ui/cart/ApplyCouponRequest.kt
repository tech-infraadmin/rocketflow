package taskmodule.ui.cart

data class ApplyCouponRequest(
    var promo: String?,
    var totalAmt: Float?
)