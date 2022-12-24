package com.rf.taskmodule.data.model

import com.rf.taskmodule.utils.NextScreen


/**
 * Created by rahul on 13/11/18
 */
open class BaseResponse {
    var successful: Boolean = false
    var responseCode: String? = null
    var responseMsg: String? = null
    var nextScreen: NextScreen? = null
    var loginToken: String? = null
    var verificationId: String? = null
    var accessId: String? = null
    var sdkAccessId: String? = null
    var punchId: String? = null
    var taskId: String? = null
    var roleId: String? = null
    var hubId: String? = null
    var mobile: String? = null
    /*This is used for trip sharing purpose inside class @taskmodule.ui.sharetrip.ShareTripActivity*/
    var trackingUrl: String? = null
    var time: String? = null
    var online: Boolean = false
    var paymentUrl: String? = null
}