package com.tracki.ui.taskdetails.timeline.skuinfopreview
import com.tracki.data.model.BaseResponse
import com.tracki.data.model.request.SkuSpecification
import com.tracki.data.model.response.config.TaskInfoData

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

