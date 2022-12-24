package com.rf.taskmodule.ui.taskdetails.timeline.skuinfopreview
import com.rf.taskmodule.data.model.BaseResponse
import com.rf.taskmodule.data.model.request.SkuSpecification
import com.rf.taskmodule.data.model.response.config.TaskInfoData

class UnitInfoResponse : BaseResponse() {
    var units: ArrayList<UnitInfoItem>?=null
    var config: ArrayList<TaskInfoData>?=null
    var count: Int?=null
}

class UnitInfoItem(
    var uid: String?=null,
    var refId: String?=null,
    var creationTime: String?=null,
    var prodId: String?=null,
    var specifications: ArrayList<SkuSpecification>?=null
)

