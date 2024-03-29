package com.rf.taskmodule.ui.cart

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.rocketflow.sdk.RocketFlyer
import com.rf.taskmodule.R
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.request.LinkInventoryRequest
import com.rf.taskmodule.data.model.request.SelectedProduct
import com.rf.taskmodule.data.model.response.config.*
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.ActivityCartSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkActivity
import com.rf.taskmodule.ui.newcreatetask.NewCreateTaskActivity
import com.rf.taskmodule.ui.selectorder.CatalogProduct
import com.rf.taskmodule.ui.selectorder.SelectOrderActivity
import com.rf.taskmodule.utils.*
import com.trackthat.lib.models.BaseResponse
import com.rf.taskmodule.BR
import com.rf.taskmodule.ui.selectorder.DimensionProduct
import com.rf.taskmodule.ui.selectorder.SubUnit
import kotlin.random.Random

class CartActivity : BaseSdkActivity<ActivityCartSdkBinding, CartViewModel>(),
    CartItemAdapter.onCartProductAddListener,
    View.OnClickListener, CartNavigator {
    private var flavourId: String? = null
    private var promocode: String? = null
    private var couponcode: String? = null
    private var totalPayableAmount: Float = 0F
    private var totalSaving: Float = 0F
    private var copytotalPayableAmount: Float = 0F
    private var copytotalSaving: Float = 0F
    private lateinit var cartItemAdapter: CartItemAdapter
    private lateinit var binding: ActivityCartSdkBinding
    private var linkingType: TaggingType? = null
    private var linkOption: LinkOptions? = null

    lateinit var mPref: PreferencesHelper
    lateinit var httpManager: HttpManager

    var listTemp = ArrayList<CatalogProduct>()

    open var savedOrderMap: HashMap<String, CatalogProduct>? = null

    lateinit var cartViewModel: CartViewModel

    private var deliveryChargeAmount: Float? = 0F
    private var deliverMode: String? = null

    private var ctaId: String? = null
    private var taskId: String? = null
    private var categoryId: String? = null
    private var buddyName: String? = null
    private var buddyId: String? = null
    private var fleetId: String? = null
    private var target: String? = null
    private var dynamicPricing: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setToolbar(binding.toolbar, "Cart")
        cartViewModel.navigator = this

        httpManager = RocketFlyer.httpManager()!!
        mPref = RocketFlyer.preferenceHelper()!!

        if (intent.hasExtra(AppConstants.DELIVERY_CHARGE)) {
            deliveryChargeAmount = intent.getFloatExtra(AppConstants.DELIVERY_CHARGE, 0F)
        }
        if (intent.hasExtra(AppConstants.DELIVERY_MODE)) {
            deliverMode = intent.getStringExtra(AppConstants.DELIVERY_MODE)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET)) {
            target = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
            taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_CTA_ID))
            ctaId = intent.getStringExtra(AppConstants.Extra.EXTRA_CTA_ID)

        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORY_ID))
            categoryId = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORY_ID)
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID)) {
            flavourId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING)) {
            dynamicPricing =
                intent.getBooleanExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING, false)
        }

        if (deliverMode != null) {
            var mode: FullFillSettingMode? = FullFillSettingMode.valueOf(deliverMode!!)
            if (mode == FullFillSettingMode.DELIVERY) {
                binding.rlDelivery.visibility = View.VISIBLE

                if (deliveryChargeAmount != 0F) {
                    binding.tvDelivery.text =
                        "₹ $deliveryChargeAmount"
                } else {
                    binding.tvDelivery.text = "FREE"

                }
            } else {
                binding.rlDelivery.visibility = View.GONE

            }
        } else {
            binding.rlDelivery.visibility = View.VISIBLE

            if (deliveryChargeAmount != 0F) {
                binding.tvDelivery.text =
                    "₹ " + deliveryChargeAmount
            } else {
                binding.tvDelivery.text = "FREE"

            }
        }
        binding.btnPlaceOrder.setOnClickListener(this)
        binding.tvApplyCoupon.setOnClickListener(this)
        binding.btnApplyCoupon.setOnClickListener(this)
        binding.tvCancelCoupon.setOnClickListener(this)
        binding.btnClearCart.setOnClickListener(this)
        cartItemAdapter = CartItemAdapter(this)
        cartItemAdapter.setDynamicPricing(dynamicPricing)
        /*  var config = CommonUtils.getFlavourConfigFromFlavourId(flavourId, mPref)
          if (config != null && config!!.dynamicPricing!!){
              dynamicPricing=config!!.dynamicPricing!!
              cartItemAdapter.setDynamicPricing(config!!.dynamicPricing!!)
          }*/
        if (categoryId != null) {
            getInventoryConfig(categoryId!!)
            cartItemAdapter.setLinkOption(linkOption!!)
            if (linkOption != null && linkOption == LinkOptions.DIRECT) {
                binding.llSubTotal.visibility = View.GONE
                binding.cvPriceCard.visibility = View.GONE
            } else {
                binding.llSubTotal.visibility = View.VISIBLE
                binding.cvPriceCard.visibility = View.VISIBLE
            }

        }
        binding.adapter = cartItemAdapter

        if (mPref.userDetail != null && mPref.userDetail!!.userId != null) {
            if (CommonUtils.getTotalItemCount(mPref.userDetail.userId!!, mPref) > 0) {
                if (mPref.productInCartWRC != null && mPref.productInCartWRC!!
                        .containsKey(mPref.userDetail.userId!!)
                ) {
                    savedOrderMap = mPref.productInCartWRC!![mPref.userDetail.userId!!]
                }
            }
        }
        getCart()


    }

    private fun getSavedMap() {
        Log.e("inMap", "1")
        if (mPref.userDetail != null && mPref.userDetail.userId != null) {
            Log.e("inMap", "2")
            if (CommonUtils.getTotalItemCount(mPref.userDetail.userId!!, mPref) > 0) {
                if (mPref.productInCartWRC != null && mPref.productInCartWRC!!
                        .containsKey(mPref.userDetail.userId)
                ) {
                    Log.e("inMap", "3")
                    savedOrderMap = mPref.productInCartWRC!![mPref.userDetail.userId]
                }

            } else {
                savedOrderMap = HashMap()
            }
        }
        Log.e("savedMap", "Size - ${savedOrderMap?.size}")
        if (savedOrderMap!!.size < 1) {
            TrackiToast.Message.showShort(this, "Please add items")
            return
        }
    }


    fun getCart() {
        if (savedOrderMap == null || savedOrderMap!!.isEmpty())
            return
        val hm1 = savedOrderMap!!

        val list = hm1
        if (list.isNotEmpty()) {
            showLoading()
            val map = HashMap<String, Int>()
            val list = ArrayList<CartProduct>()
            for (data in savedOrderMap!!.values) {
                val cart = CartProduct()
                cart.productId = data.pid!!
                cart.quantity = data.noOfProduct
                if (data.enableDimension == true) {
                    cart.dimensionEnabled = data.enableDimension
                    val unitList = ArrayList<SubUnitC>()
                    for (i in 0 until data.subUnit!!.size) {
                        val dimension = data.subUnit!![i].dimension
                        val subUnit = SubUnitC()

                        val dimensionProductC = DimensionProductC()
                        dimensionProductC.area = dimension!!.area
                        dimensionProductC.length = dimension.length
                        dimensionProductC.width = dimension.width

                        subUnit.dimension = dimensionProductC
                        subUnit.actualPrice = data.subUnit!![i].actualPrice
                        subUnit.sellingPrice = data.sellingPrice

                        unitList.add(subUnit)
                    }
                    cart.subUnit = unitList
                }
                map[data.pid!!] = data.noOfProduct
                list.add(cart)
            }
            val cartrequest = CartRequest()
            //cartrequest.data = map
            cartrequest.data = list
            cartViewModel.getCart(httpManager, cartrequest)
        }

    }


    fun setTotalAmount(totalAmount: Float?) {
        var totalAmount1 = 0F
        for (item in listTemp ){
            Log.e("amtCheck","$item")
            if(item.enableDimension == true || item.dimensionEnabled == true) {
                totalAmount1 += item.sellingPrice!!
            }
        }




        if (totalAmount != null) {
            totalAmount1 += totalAmount
            totalPayableAmount = totalAmount1
            copytotalPayableAmount = totalAmount1
            binding.tvSubTotal.text =
                "₹ $totalAmount1"
            binding.tvGrantTotalValue.text =
                "₹ $totalAmount1"

        }

        if (totalAmount != null)
            binding.tvBillAmountValue.text =
                "₹ $totalAmount1"
        binding.tvTotalItemCount.text =
            CommonUtils.getTotalItemCount(mPref.userDetail!!.userId!!, mPref).toString()


    }

    fun getTotalAmount(): Float {
        var amount = 0F
        if (mPref.userDetail != null && mPref.userDetail!!.userId != null) {
            if (CommonUtils.getTotalItemCount(mPref.userDetail!!.userId!!, mPref) > 0) {
                if (mPref.productInCartWRC != null && mPref.productInCartWRC!!
                        .containsKey(mPref.userDetail!!.userId!!)
                ) {
                    val userCartMap = mPref.productInCartWRC!![mPref.userDetail!!.userId!!]
                    for (data in userCartMap!!.values) {
                        if (data != null) {


                                if (data.noOfProduct != null && data.sellingPrice != null) {
                                    if (data.dimensionEnabled == true || data.enableDimension == true){}
                                    else {
                                        amount += (data.noOfProduct.times(data?.sellingPrice!!)!!)
                                        Log.e("amtTotal", "dime else")
                                    }
                                }

                            Log.e("amtTotal","$amount")
                        }
                    }
                }
            }
        }
        return amount
    }

    override fun addProduct(data: CatalogProduct, position: Int) {
        if (savedOrderMap == null) {
            savedOrderMap = HashMap()
        }
        if (data.addInOrder)
            savedOrderMap!![data.pid!!] = data
        else {
            if (savedOrderMap!!.contains(data.pid)) {
                savedOrderMap!!.remove(data.pid)
//                Log.e("remove product", data.name)
            }
        }
        val saveOrderInCart = HashMap<String, Map<String, CatalogProduct>>()
        if (mPref.productInCartWRC != null)
            saveOrderInCart.putAll(mPref.productInCartWRC!!)
        saveOrderInCart[mPref.userDetail!!.userId!!] = savedOrderMap!!
        mPref.saveProductInCartWRC(saveOrderInCart)
        val jsonConverter2 =
            JSONConverter<Map<String, Map<String, CatalogProduct>>>()
        val str2 = jsonConverter2.objectToJson(mPref.productInCartWRC)
        Log.e("map_addProduct", str2)

        invalidateOptionsMenu()
//        var jsonConverter = JSONConverter<HashMap<String, CatalogProduct>>()
//        var str = jsonConverter.objectToJson(savedOrderMap)
//        Log.e("map", str)
        setTotalAmount(getTotalAmount())
//        if (binding.tvApplyCoupon.visibility == View.GONE)
        //removeCoupon()
    }

    override fun viewProduct(data: CatalogProduct) {

    }

    override fun removeProduct(data: CatalogProduct, position: Int) {
    }

    override fun deleteDimensionProduct(data: CatalogProduct, order: Int) {
        //remove Dimension Product
        for (item in listTemp){
            if (item.subUnit!![0].order!! == order){
                listTemp.remove(item)
                cartItemAdapter.clearList()
            }
        }
        cartItemAdapter.addItems(listTemp)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view, menu)
        val myActionMenuItem = menu!!.findItem(R.id.action_search)
        myActionMenuItem.isVisible = false
        val menuItem: MenuItem = menu.findItem(R.id.action_cart)
        menuItem.isVisible = false
        var count = 0
        var jsonConverter =
            JSONConverter<Map<String, Map<String, CatalogProduct>>>()
        var str = jsonConverter.objectToJson(mPref.getProductInCartWRC())
//        Log.e("map_menu", str)
        if (mPref.userDetail != null && mPref.userDetail!!.userId != null)
            count = CommonUtils.getTotalItemCount(mPref.userDetail!!.userId!!, mPref)
        menuItem.icon = buildCounterDrawable(count, R.drawable.ic_cart)
        return true
    }

    private fun buildCounterDrawable(count: Int, backgroundImageId: Int): Drawable? {
        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.counter_menu_item_layout_sdk, null)
        view.setBackgroundResource(backgroundImageId)
        if (count == 0) {
            val counterTextPanel = view.findViewById<View>(R.id.counterValuePanel)
            counterTextPanel.visibility = View.GONE
        } else {
            val textView = view.findViewById<View>(R.id.count) as TextView
            textView.text = "$count"
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.isDrawingCacheEnabled = true
        view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return BitmapDrawable(resources, bitmap)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.tvApplyCoupon -> {
                binding.tvApplyCoupon.visibility = View.GONE
                binding.rlCouponCode.visibility = View.VISIBLE

            }
            R.id.tvCancelCoupon -> {
                removeCoupon()
            }

            R.id.btnApplyCoupon -> {
                applyCoupon()
            }
            R.id.btnClearCart -> {
                onSuccess(true)
            }

            R.id.btnPlaceOrder -> {
                getSavedMap()
                linkInventory()
            }
        }
    }

    private fun removeCoupon() {
        binding.tvApplyCoupon.visibility = View.VISIBLE
        binding.rlCouponCode.visibility = View.GONE
        binding.rlCouponCodeApplied.visibility = View.GONE
        couponcode = null
        promocode = null
        totalPayableAmount = copytotalPayableAmount
        totalSaving = copytotalSaving
        binding.tvSubTotal.text =
            "₹ " + totalPayableAmount.toString()

        binding.tvBillAmountValue.text =
            "₹ " + totalPayableAmount.toString()

        binding.tvGrantTotalValue.text =
            "₹ " + totalPayableAmount.toString()

        binding.tvTotalSaving.text =
            "₹ $totalSaving"
        binding.tvDiscountCode.text = ""
        binding.tvDiscount.text = "₹ 0"
        binding.etCoupon.setText("")
    }

    private fun applyCoupon() {

        promocode = binding.etCoupon.text.toString().trim()
        if (promocode!!.isNotEmpty()) {
            showLoading()
            cartViewModel.applyCoupon(
                httpManager,
                ApplyCouponRequest(promocode, totalPayableAmount)
            )
        } else {
            TrackiToast.Message.showShort(this, "Please enter promo code")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cart -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_cart_sdk
    }


    fun onSuccess(set: Boolean = false) {
        if (set) {
            mPref.saveProductInCartWRC(null)
            mPref.saveProductInCart2(null)
            val intent = Intent(this, SelectOrderActivity::class.java)
            val dashBoardBoxItem = DashBoardBoxItem()
            dashBoardBoxItem.categoryId = categoryId
            intent.putExtra(
                AppConstants.Extra.EXTRA_CATEGORIES,
                Gson().toJson(dashBoardBoxItem)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        } else {
            if (mPref.userDetail != null && mPref.userDetail!!.userId != null) {
                if (CommonUtils.getTotalItemCount(mPref.userDetail!!.userId!!, mPref) > 0) {
                    if (mPref.productInCartWRC != null && mPref.productInCartWRC!!
                            .containsKey(mPref.userDetail!!.userId!!)
                    ) {
                        val saveOrderInCart = HashMap<String, Map<String, CatalogProduct>>()
                        if (mPref.productInCartWRC != null)
                            saveOrderInCart.putAll(mPref.productInCartWRC!!)

                        saveOrderInCart.remove(mPref.userDetail!!.userId!!)
                        mPref.saveProductInCartWRC(saveOrderInCart)
                        val jsonConverter2 =
                            JSONConverter<Map<String, Map<String, CatalogProduct>>>()
                        var str2 = jsonConverter2.objectToJson(mPref.productInCartWRC)
                    }
                }
            }

            val returnIntent = Intent()
            if (taskId != null)
                returnIntent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskId)
            if (buddyId != null)
                returnIntent.putExtra(AppConstants.Extra.EXTRA_BUDDY_ID, buddyId)
            if (fleetId != null)
                returnIntent.putExtra(AppConstants.Extra.EXTRA_FLEET_ID, fleetId)
            if (buddyName != null)
                returnIntent.putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, buddyName)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    fun onCancel() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }


    override fun onBackPressed() {
        val intent = Intent(this, SelectOrderActivity::class.java)
        val dashBoardBoxItem = DashBoardBoxItem()
        dashBoardBoxItem.categoryId = categoryId
        intent.putExtra(
            AppConstants.Extra.EXTRA_CATEGORIES,
            Gson().toJson(dashBoardBoxItem)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): CartViewModel {
        val factory = RocketFlyer.dataManager()?.let { CartViewModel.Factory(it) } // Factory
        if (factory != null) {
            cartViewModel = ViewModelProvider(this, factory)[CartViewModel::class.java]
        }
        return cartViewModel!!
    }

    open fun getRandomNumberString(): String? {
        val number = Random.nextInt(999999)
        return String.format("%06d", number)
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun handleCartResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@CartActivity)) {
            val jsonConverter =
                JSONConverter<CartResponse>()
            val responseMain: CartResponse = jsonConverter.jsonToObject(
                result.toString(),
                CartResponse::class.java
            ) as CartResponse
            if (responseMain.data != null && responseMain.data!!.isNotEmpty()) {
                val list = responseMain.data!!

                if (savedOrderMap != null && savedOrderMap!!.isNotEmpty()) {
                    for (data in list) {
                        Log.e("dataCheck","$data in for loop cart activity")
                        if (savedOrderMap!!.contains(data.pid)) {
                            val newdata = savedOrderMap!![data.pid]!!
                            data.addInOrder = newdata.addInOrder
                            data.noOfProduct = newdata.noOfProduct
                        }

                        if (data.enableDimension == true || data.dimensionEnabled == true) {
                            val subUnit = data.subUnit!!
                            for (i in 0 until subUnit.size) {
                                val lista1 = ArrayList<SubUnit>()
                                val un = subUnit[i]
                                val uid = "${data.subId}-${getRandomNumberString()}"
                                lista1.add(un)
                                val obj1 = data.copy(pid = uid, sellingPrice = un.sellingPrice, price = un.sellingPrice, unitValue = un.dimension!!.area, subUnit = lista1, deleteOption = true)

                                listTemp.add(obj1)
                            }
                        }
                        else{
                            listTemp.add(data)
                        }
                    }
                }

                if (responseMain.totalQty != null)
                    binding.tvTotalItemCount.text = responseMain.totalQty.toString()

                if (responseMain.totalAmount != null) {
                    setTotalAmount(responseMain.totalAmount)

                }

                /*  if(responseMain.totalDiscount!=null)
                  binding.tvDiscount.text="₹ "+responseMain.totalDiscount.toString()*/
                if (responseMain.totalSaving != null) {
                    totalSaving = responseMain.totalSaving!!
                    copytotalSaving = responseMain.totalSaving!!
                    binding.tvTotalSaving.text =
                        "₹ " + responseMain.totalSaving.toString()
                }


                cartItemAdapter.clearList()
                cartItemAdapter.addItems(listTemp)


            }
        }
    }

    override fun handleCreateOrderResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@CartActivity)) {
            val jsonConverter =
                JSONConverter<BaseResponse>()
            val responseMain: BaseResponse = jsonConverter.jsonToObject(
                result.toString(),
                BaseResponse::class.java
            ) as BaseResponse

            if (responseMain.responseMsg != null) {
                TrackiToast.Message.showShort(this, responseMain.responseMsg)
                onSuccess()
            } else {
                onSuccess()
            }
        }
    }

    override fun handleApplyCouponResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@CartActivity)) {


            var jsonConverter =
                JSONConverter<ApplyCouponResponse>()
            var responseMain: ApplyCouponResponse = jsonConverter.jsonToObject(
                result.toString(),
                ApplyCouponResponse::class.java
            ) as ApplyCouponResponse
            if (responseMain.discount != null) {

                binding.tvDiscount.text = "₹ " + responseMain.discount.toString()
                totalPayableAmount -= responseMain.discount!!
                totalSaving += responseMain.discount!!
            }
            if (responseMain.couponId != null) {
                couponcode = responseMain.couponId
                if (promocode != null)
                    binding.tvDiscountCode.text = "(${promocode} Applied) "
                binding.rlCouponCodeApplied.visibility = View.VISIBLE
                binding.rlCouponCode.visibility = View.GONE
                binding.tvApplyCoupon.visibility = View.GONE
                if (promocode != null)
                    binding.tvCouponCode.text = "${promocode} Applied"
                binding.tvCouponCode.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorGreenAmount
                    )
                )
                binding.tvCancelCoupon.text = "Remove"
            }
            binding.tvSubTotal.text =
                "₹ " + totalPayableAmount.toString()
            binding.tvGrantTotalValue.text =
                "₹ " + totalPayableAmount.toString()

            binding.tvBillAmountValue.text =
                "₹ " + totalPayableAmount.toString()

            binding.tvTotalSaving.text =
                "₹ " + totalSaving.toString()
        }
    }

    override fun linkInventoryResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            onSuccess()

        }
    }

    fun linkInventory() {
        if (linkOption != null && linkOption == LinkOptions.DIRECT) {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_CTA_ID)) {
                ///Code for order tagging
                return
            } else {
                val list = cartItemAdapter.getAllList()?.filter { it.addInOrder }
                if (list?.isNotEmpty() == true) {
                    val invIds = ArrayList<String>()
                    for (data in list) {
                        invIds.add(data.pid!!)
                    }
                    val intent = NewCreateTaskActivity.newIntent(this)
                    intent.putExtra(AppConstants.Extra.FROM, "taskListing")
                    val dashBoardBoxItem = DashBoardBoxItem()
                    dashBoardBoxItem.categoryId = categoryId
                    intent.putExtra(
                        AppConstants.Extra.EXTRA_CATEGORIES,
                        Gson().toJson(dashBoardBoxItem)
                    )
                    intent.putStringArrayListExtra(
                        "invIds",
                        invIds
                    )
                    intent.putExtra(
                        "directMapping",
                        true
                    )
                    intent.putExtra(
                        AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU,
                        true
                    )
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK_DIRECT)
                } else {
                    TrackiToast.Message.showShort(this, "Please add items")
                }
            }
        } else {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_CTA_ID)) {
                if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
                    taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
                }
                val ctaId = intent.getStringExtra(AppConstants.Extra.EXTRA_CTA_ID)


                val linkRequest = LinkInventoryRequest()
                linkRequest.categoryId = categoryId
                linkRequest.action = target
                linkRequest.taskId = taskId
                if (savedOrderMap != null && savedOrderMap!!.isNotEmpty()) {
//                    var map = HashMap<String, Int>()
                    var map = ArrayList<SelectedProduct>()
                    for (data in savedOrderMap!!.values) {
                        // map[data.pid!!] = data.noOfProduct
                        val product = SelectedProduct()
                        product.productId = data.pid
                        product.quantity = data.noOfProduct
                        product.price = data.sellingPrice

                        if (data.dimensionEnabled == true || data.enableDimension == true) {
                            val subUnitC = SubUnitC()
                            subUnitC.sellingPrice = data.subUnit!![0].sellingPrice
                            subUnitC.order = data.subUnit!![0].order
                            subUnitC.actualPrice = data.subUnit!![0].actualPrice
                            val dimensionProductC = DimensionProductC()
                            val dimension = data.subUnit!![0].dimension
                            dimensionProductC.area = dimension!!.area
                            dimensionProductC.length = dimension.length
                            dimensionProductC.width = dimension.width
                            subUnitC.dimension = dimensionProductC
                            val listSub = ArrayList<SubUnitC>()
                            listSub.add(subUnitC)
                            product.subUnit = listSub
                        }
//                        product.dynamicPricing=dynamicPricing
                        map.add(product)
                    }
                    linkRequest.dynamicPricing = dynamicPricing
                    linkRequest.products = map
                }

                linkRequest.ctaId = ctaId
                val listCategory = mPref.workFlowCategoriesList
                if (categoryId != null) {
                    val workFlowCategories = WorkFlowCategories()
                    workFlowCategories.categoryId = categoryId
                    if (listCategory.contains(workFlowCategories)) {
                        val position: Int = listCategory.indexOf(workFlowCategories)
                        if (position != -1) {
                            val myCatData: WorkFlowCategories = listCategory.get(position)
                            // userGeoReq=myCatData.getAllowGeography();
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.approvalType != null && myCatData.inventoryConfig!!.approvalType == ApprovalType.MANUAL
                                && myCatData.inventoryConfig!!.approvalOn != null && myCatData.inventoryConfig!!.approvalOn == ApprovalOn.SUB_TASK
                            ) {
                                linkRequest.createSubTask = true
                                val subTaskConfig = myCatData.subTaskConfig
                                if (subTaskConfig != null && subTaskConfig.categories!!.isNotEmpty()) {
                                    //linkRequest.subCategoryId = subTaskConfig.categories!![0]
                                    linkRequest.subTaskCategory = subTaskConfig.categories!![0]
                                    linkRequest.parentTaskId = taskId
                                }
                            }
                            if (flavourId != null) {
                                linkRequest.flavorId = flavourId
                            } else if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.flavorId != null && myCatData.inventoryConfig!!.flavorId!!.isNotEmpty()) {
                                linkRequest.flavorId = myCatData.inventoryConfig!!.flavorId
                            }
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.availabilityType != null)
                                linkRequest.type = myCatData.inventoryConfig!!.availabilityType
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.approvalType != null) {
                                linkRequest.autoApprove =
                                    myCatData.inventoryConfig!!.approvalType == ApprovalType.AUTO
                            }
                            linkRequest.type = myCatData.inventoryConfig!!.availabilityType
                        }
                    }
                }
                val jsonConverter: JSONConverter<LinkInventoryRequest> =
                    JSONConverter()
                val strRequest = jsonConverter.objectToJson(linkRequest)
                CommonUtils.showLogMessage("e", "strRequest", strRequest);
                showLoading()
                cartViewModel.linkInventory(httpManager, linkRequest)
            } else {
                getCart()
                val list = cartItemAdapter.getAllList()
                if (list?.isNotEmpty() == true) {
                    val intent = NewCreateTaskActivity.newIntent(this)
                    intent.putExtra(AppConstants.Extra.FROM, "taskListing")
                    val dashBoardBoxItem = DashBoardBoxItem()
                    dashBoardBoxItem.categoryId = categoryId
                    intent.putExtra(
                        AppConstants.Extra.EXTRA_CATEGORIES,
                        Gson().toJson(dashBoardBoxItem)
                    )
                    intent.putExtra(
                        AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU,
                        true
                    )
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK)
                } else {
                    TrackiToast.Message.showShort(this, "Please add items")
                }
            }
        }
    }

    private fun getInventoryConfig(categoryId: String?) {
        if (categoryId == null)
            return
        val listCategory = mPref.workFlowCategoriesList
        val workFlowCategories = WorkFlowCategories()
        workFlowCategories.categoryId = categoryId
        if (listCategory.contains(workFlowCategories)) {
            val position: Int = listCategory.indexOf(workFlowCategories)
            if (position != -1) {
                val myCatData: WorkFlowCategories = listCategory.get(position)
                // userGeoReq=myCatData.getAllowGeography();


                if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.linkingType != null) {
                    linkingType = myCatData.inventoryConfig!!.linkingType
                }

                if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.linkOption != null) {
                    linkOption = myCatData.inventoryConfig!!.linkOption
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_CREATE_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                taskId = data!!.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
                buddyName = data!!.getStringExtra(AppConstants.Extra.EXTRA_BUDDY_NAME)
                buddyId = data!!.getStringExtra(AppConstants.Extra.EXTRA_BUDDY_ID)
                fleetId = data!!.getStringExtra(AppConstants.Extra.EXTRA_FLEET_ID)
                var linkRequest = LinkInventoryRequest()
                if (savedOrderMap != null && savedOrderMap!!.isNotEmpty()) {
//                    var map = HashMap<String, Int>()
                    var map = ArrayList<SelectedProduct>()
                    for (data in savedOrderMap!!.values) {
                        // map[data.pid!!] = data.noOfProduct
                        var product = SelectedProduct()
                        product.productId = data.pid
                        product.quantity = data.noOfProduct
                        product.price = data.sellingPrice
                        if (data.dimensionEnabled == true || data.enableDimension == true) {
                            val subUnitC = SubUnitC()
                            subUnitC.sellingPrice = data.subUnit!![0].sellingPrice
                            subUnitC.order = data.subUnit!![0].order
                            subUnitC.actualPrice = data.subUnit!![0].actualPrice
                            val dimensionProductC = DimensionProductC()
                            val dimension = data.subUnit!![0].dimension
                            dimensionProductC.area = dimension!!.area
                            dimensionProductC.length = dimension.length
                            dimensionProductC.width = dimension.width
                            subUnitC.dimension = dimensionProductC
                            val listSub = ArrayList<SubUnitC>()
                            listSub.add(subUnitC)
                            product.subUnit = listSub
                        }
//                        product.dynamicPricing=dynamicPricing
                        map.add(product)
                    }
                    linkRequest.dynamicPricing = dynamicPricing
                    linkRequest.products = map
                }

                linkRequest.categoryId = categoryId
                linkRequest.taskId = taskId
                linkRequest.action = target
//                linkRequest.autoApprove = true
                val listCategory = mPref.workFlowCategoriesList
                if (categoryId != null) {
                    val workFlowCategories = WorkFlowCategories()
                    workFlowCategories.categoryId = categoryId
                    if (listCategory.contains(workFlowCategories)) {
                        val position: Int = listCategory.indexOf(workFlowCategories)
                        if (position != -1) {
                            val myCatData: WorkFlowCategories = listCategory[position]
                            // userGeoReq=myCatData.getAllowGeography();
                             if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.approvalType != null && myCatData.inventoryConfig!!.approvalType == ApprovalType.MANUAL
                                && myCatData.inventoryConfig!!.approvalOn != null && myCatData.inventoryConfig!!.approvalOn == ApprovalOn.SUB_TASK
                            ) {
                                linkRequest.createSubTask = true
                                val subTaskConfig = myCatData.subTaskConfig
                                if (subTaskConfig != null && subTaskConfig.categories!!.isNotEmpty()) {
                                    //linkRequest.subCategoryId = subTaskConfig.categories!![0]
                                    linkRequest.subTaskCategory = subTaskConfig.categories!![0]
                                    linkRequest.parentTaskId = taskId
                                }
                            }
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.flavorId != null && myCatData.inventoryConfig!!.flavorId!!.isNotEmpty()) {
                                linkRequest.flavorId = myCatData.inventoryConfig!!.flavorId
                            }
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.invAction != null && myCatData.inventoryConfig!!.invAction!!.name.isNotEmpty()) {
                                linkRequest.action = myCatData.inventoryConfig!!.invAction!!.name
                            }

                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.availabilityType != null)
                                linkRequest.type = myCatData.inventoryConfig!!.availabilityType
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.approvalType != null) {
                                linkRequest.autoApprove =
                                    myCatData.inventoryConfig!!.approvalType == ApprovalType.AUTO
                            }
                        }
                    }
                }
                val jsonConverter: JSONConverter<LinkInventoryRequest> =
                    JSONConverter()
                var strRequest = jsonConverter.objectToJson(linkRequest)
                CommonUtils.showLogMessage("e", "strRequest", strRequest);
                showLoading()
                cartViewModel.linkInventory(httpManager, linkRequest)

            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        } else if (requestCode == AppConstants.REQUEST_CODE_CREATE_TASK_DIRECT) {
            if (resultCode == Activity.RESULT_OK) {
                onSuccess()
            }
        }

    }


}