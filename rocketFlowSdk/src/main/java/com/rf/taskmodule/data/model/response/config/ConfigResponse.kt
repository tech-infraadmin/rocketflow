package com.rf.taskmodule.data.model.response.config

import com.google.gson.annotations.SerializedName
import com.rf.taskmodule.data.model.BaseResponse
import com.rf.taskmodule.utils.*
import java.io.File
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by rahul on 12/11/18
 */

data class ConfigResponse (
    var sdkConfig: SdkConfig? = null,
    var appConfig: AppConfig? = null,
    var appVersionInfo: DeprecationAndExpiration? = null,

    //    var taskCancellationReasons: List<String>? = null
    var userInfo: ProfileInfo? = null,
    var services: List<Service>? = null,
    //    var dynamicForms: List<DynamicForms>? = null
    var lookups: List<LookUps>? = null,
    //    var dynamicFormConfig: List<DynamicFormConfig>? = null
    var dynamicForms: List<DynamicFormsNew>? = null,
    var refreshConfig: Boolean? = null
): BaseResponse()

open class Subscription {
    var message: String? = ""
    var title: String? = ""
    var packageId: String? = ""
    var packageName: String? = ""
    var payUrl: String? = ""
    var subscriptionExpired: Boolean? = false
    var trialExpired: Boolean? = false
    var type: SubscriptionPlan? = SubscriptionPlan.TRIAL
    var ucId: String? = ""
    var ucName: String? = ""
}

enum class SubscriptionPlan {
    TRIAL, BUSINESS
}

class LookUps {
    var type: String? = null
    var key: String? = null
    var value: String? = null
}

class DynamicFormConfig {
    var taskType: String? = null
    var action: TaskAction? = null
    var form: String? = null
}

class DynamicFormsNew {
    var formId: String? = null
    var name: String? = null
    var version: String? = null
    var init: Boolean? = false
    var subform: Boolean? = false
    var dfAutopopulate: Boolean = false
    var fields: ArrayList<FormData>? = null
}

data class FoundWidgetItem(
    var isPresent: Boolean = false,
    var postion: Int = -1,
    var name: String? = null
)

class DynamicForms {
    var formType: FormType? = null
}

class WidgetData : Serializable {
    var title: String? = null
    var selected = false
    var value: String? = null
    var weight: Double = 0.0
    var target: String? = null
    var formDataList: List<FormData>? = null
    override fun toString() = /*title + "" */"{ " +
            "title: " + title +
            "," +
            "selected: " + selected +
            "," +
            "value: " + value +
            "," +
            "weight: " + weight +
            " }"
}

class FormData : Serializable {
    //here
    var type: DataType? = null
    var `field`: String? = null
    var label: String? = null
    var name: String? = null
    var value: String? = null
    var readOnly = false
    var title: String? = null
    var placeHolder: String? = null
    var required = false
    var errorMessage: String? = null
    var minLength = 0
    var formItemKey: String? = null
    var maxLength = 0
    var maxRange = 0L
    var minRange = 0L
    var isembded = false
    var embeddedDfId: String? = null
    var widgetData: List<WidgetData>? = null
    var dynamicSelectLookup: LinkedHashMap<String, FormData>? = null
    var actionConfig: DynamicActionConfig? = null
    var enteredValue: String? = null
    var file: ArrayList<File>? = null
    var isChecked = false
    var selectedValues: String? = null
    var apiMap: Map<String, String>? = null
    var weight: Double = 0.0


    //this form item position is used for dynamic select lookup to check which item
    //is selected.
    var formItemPosition = -1
    //used for selected item in map

    var uniqueID: String? = UUID.randomUUID().toString()
    var includeForCalculation: Boolean = false
    var operation: String? = null
    var roles: ArrayList<String>? = null

    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val formData: FormData = obj as FormData
        return formData.uniqueID == this.uniqueID
    }

    override fun hashCode(): Int {
        return this.uniqueID.hashCode()
    }

    override fun toString(): String {

        return " { " +
                " type:" + type +
                " " +
                "name: " + name +
                " " +
                "title: " + title +
                " " +
                "placeHolder: " + placeHolder +
                " " +
                "required: " + required +
                " " +
                "errorMessage: " + errorMessage +
                " " +
                "maxLength: " + maxLength +
                " " +
                "maxRange: " + maxRange +
                " " +
                "minRange: " + minRange +
                " " +
                "actionConfig: " + actionConfig +
                " " +
                "enteredValue: " + enteredValue +
                " " +
                "file: " + file +
                " " +
                "isChecked: " + isChecked +
                " " +
                "value: " + value +
                " " +
                "readOnly: " + readOnly +
                " " +
                "widgetData: " + widgetData +
                " " +
                "dynamicSelectLookup: " + dynamicSelectLookup +
                " " +
                "minLength: " + minLength +
                " " +
                "weight: " + weight +
                " }"
    }
}

class DynamicActionConfig : Serializable {
    var action: Type? = null
    var target: String? = null
    var formula: String? = null
    var skipTarget: Boolean? = null

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("{ ")
        builder.append("action: ")
        builder.append(action)
        builder.append(",target: ")
        builder.append(target)
        builder.append(" }")
        builder.append(",formula: ")
        builder.append(formula)
        builder.append(" }")
        builder.append(",skipTarget: ")
        builder.append(skipTarget)
        builder.append(" }")
        return builder.toString()
    }
}

enum class Type {
    FORM, API, DISPOSE
}

//ASSIGN_SUBORDINATE
//PASSWORD
//USER_NAME
//PORT
//RTSP
enum class DataType {
    SIGNATURE, NUMBER, DATE_RANGE, DATE, DATE_TIME, TIME, DAY, EMAIL,
    FILE, TEXT, FILES, SOUND, RATING, TEXT_AREA, BOOLEAN, LIST,
    GEO, MULTI_SELECT, CAMERA, TOGGLE, BUTTON, DROPDOWN, CONDITIONAL_DROPDOWN_STATIC,
    CONDITIONAL_DROPDOWN_API, RADIO, CHECKBOX, LABLE, LABEL, DROPDOWN_API, CALCULATE,
    AUDIO, VIDEO, VERIFY_OTP, IMAGE, IP_ADDRESS, SELECT_EXECUTIVE, SELECT_EXECUTIVE_BY_PLACE, SELECT_NEAR_BY_EXECUTIVE, SELECT_GROUP, ASSIGN_SUBORDINATE, PASSWORD,
    USER_NAME, PORT, RTSP, AMOUNT, EVENT, SCANNER, MEDIA
}

data class AppConfig (
    var apis: List<Api>? = null,
    var roles: List<RoleConfigData>? = null,
    var navigations: List<Navigation>? = null,
    var projectCategories: java.util.ArrayList<ProjectCategories>? = null,
    var autoStart: Boolean? = false,
    var userGeoFilters: Boolean? = false,
    var chatServerUrl: String? = null,
    var allowArrival: Boolean = false,
    var enableWallet: Boolean = false,
    var allowArrivalOnGeoIn: Boolean = false,
    var locationRequired: Boolean = false,
    var buddyListing: Boolean = false,
    var manager: Boolean = false,
    var autoCancelThresholdInMin: Int = 0,
    var allowFingerPrint: Boolean = false,
    var enablePunchGeofencing: Boolean = false,
    var servicePref: Boolean = false,
    var insights: Boolean = false,
    var shifts: Map<Int, List<ShiftTime>>? = null,
    var workflowCategories: List<WorkFlowCategories>? = null,
    var flavors: List<Flavour>? = null,
    var idleTrackingInfo: IdleTrackingInfo? = null,
    var userTypes: List<UserType>? = null,
    var status: String? = null,
    var defDateRange: Int = 0,
    var maxDateRange: Int = 0,
    var maxPastDaysAllowed: Int = 0,
    var configVersion: String? = null,
    var splashImage: String? = null,
)

data class ProjectCategories(
    var projectId: String? = null,
    var categories: ArrayList<Category>? = null,
    var icon: String? = null,
    var name: String? = null,
)

data class Category(
    var categoryId: String? = null,
    var name: String? = null,
    var icon: String? = null,
)

data class Flavour(
    @SerializedName("config")
    var config: Config?,
    @SerializedName("flavourId")
    var flavourId: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("type")
    var type: String?
)

data class Config(
    @SerializedName("allowedFields")
    var allowedFields: List<AllowedField>?,
    @SerializedName("catLabel")
    var catLabel: String?,
    @SerializedName("df")
    var df: Boolean = false,
    @SerializedName("dfId")
    var dfId: String?,
    @SerializedName("parentCatLabel")
    var parentCatLabel: String?,
    @SerializedName("prodLabel")
    var prodLabel: String?,
    @SerializedName("stock")
    var stock: Boolean?,
    /*  @SerializedName("dynamicPricing")
      var dynamicPricing: Boolean?,*/
    @SerializedName("subCatLabel")
    var subCatLabel: String?,
    @SerializedName("subCategory")
    var subCategory: Boolean?
)

data class AllowedField(
    @SerializedName("errMsg")
    var errMsg: String? = null,
    @SerializedName("field")
    var `field`: String? = null,
    @SerializedName("label")
    var label: String? = null,
    @SerializedName("required")
    var required: Boolean = false,
    @SerializedName("skipVisibilty")
    var skipVisibilty: Boolean? = null,
    @SerializedName("type")
    var type: FieldType? = null,
    @SerializedName("validator")
    var validator: String? = null,
    @SerializedName("visible")
    var visible: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AllowedField

        if (field != other.field) return false

        return true
    }

    override fun hashCode(): Int {
        return field?.hashCode() ?: 0
    }
}

data class RoleConfigData(
    var dfId: String? = null,
    var fields: List<Field>? = null,
    var roleId: String? = null,
    var roleName: String? = null,
    var type: String? = null,
    var isSelected: Boolean = false,
    var enableWallet: Boolean = false,
    var enableDocument: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoleConfigData

        if (roleId != other.roleId) return false

        return true
    }

    override fun hashCode(): Int {
        return roleId?.hashCode() ?: 0
    }

}


data class ChannelConfig(
    var channelSetting: ChannelSetting? = null,
    var fields: List<Field>? = null,
    var settingJson: Any? = null
)

data class ChannelSetting(
    var allowCreation: Boolean? = false,
    var creationMode: CreationMode = CreationMode.DIRECT,
    var allowGoogleLocation: Boolean? = null,
    var allowedFieldFirst: Boolean? = null,
    var exeLocation: Boolean? = null,
    var locType: String? = null,
    var creationConfig: CreationConfig? = null,
//        var creationTitle: String? = "I have Assigned",
//        var executionTitle: String? = "Assigned to Me",
    var creationTitle: String? = null,
    var executionTitle: String? = null,
    var selfAllocate: Boolean? = null,
    var taskExecution: Boolean? = null,
    var validateLocation: Boolean? = null,
    var merchantTaskLabel: String? = null

)

enum class CreationMode {
    IN_DIRECT, DIRECT
}

enum class FieldType {
    TEXT, NUMBER, RADIO, DROPDOWN
}

enum class AdvancedConfigSource(val source: String?) {
    NEAR_BY_HUBS("NEAR_BY_HUBS"), LOGGED_IN_USER_HUBS("LOGGED_IN_USER_HUBS"), ALL_HUBS("ALL_HUBS"),
    TASK_SCOPE_HUB_SLOTS("TASK_SCOPE_HUB_SLOTS"), LOGGED_IN_USER_HUBS_SLOTS("LOGGED_IN_USER_HUBS_SLOTS"),
    TASK_SCOPE_PRODUCT_SLOTS("TASK_SCOPE_PRODUCT_SLOTS")
}

enum class Target(val target: String?) {
    SOURCE("SOURCE"), DESTINATION("DESTINATION")
}

data class AdvanceConfig(
    var source: AdvancedConfigSource? = null,
    var maxRadius: Float? = 0F,
    var target: Target? = Target.SOURCE,
    var maxLimit: Int? = 0
)

data class Field(
    var `field`: String? = null,
    var type: DataType? = null,
    var errMsg: String? = null,
    var label: String? = null,
    var validator: String? = null,
    var skipVisibilty: Boolean = true,
    var visible: Boolean = false,
    var required: Boolean = false,
    var value: String? = null,
    var advanceConfig: AdvanceConfig? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Field

        if (`field` != other.`field`) return false

        return true
    }

    override fun hashCode(): Int {
        return `field`?.hashCode() ?: 0
    }

}

data class CreationConfig(
    var allocation: String? = null,
    var assignmentType: String? = null,
    var requestedBy: String? = null,
    var requestedByLabel: String? = null,
    var requestUserType: List<String>? = null,
    var assigneeLabel: String? = null,
    var buddyLabel: String? = null,
    var autoAccept: Boolean? = null,
    var googleAccess: Boolean? = null
)

class IdleTrackingInfo {
    var enableIdleTracking = false
    var mode: String? = null
}

enum class TaggingTypeService {
    INDIRECT, DIRECT
}

class ServiceConfig {
    var taggingType: TaggingTypeService = TaggingTypeService.DIRECT
    var label: String? = null
    var referredCategories: List<String>? = null
}

class WorkFlowCategories {
    var id: String? = null
    var categoryId: String? = null
    var name: String? = null
    var dynamicFormId: String? = null
    var serviceTagging: Boolean? = false
    var allowSubTask: Boolean? = null
    var serviceConfig: ServiceConfig? = null
    var allowGeography: Boolean = false
    var enableExpiry: Boolean = false
    var allowedFields: List<String>? = null
    var channelConfig: ChannelConfig? = null
    var inventoryConfig: InventoryConfig? = null
    var subTaskConfig: SubTaskConfig? = null
    var taskReferencingEnabled: Boolean? = false
    var multiTaskReferencingEnabled: Boolean? = false
    var stageNameMap: LinkedHashMap<String, String>? = null
    var allowCreation: Boolean = false
    var showMerchantTasks: Boolean = false
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkFlowCategories

        if (categoryId != other.categoryId) return false

        return true
    }

    override fun hashCode(): Int {
        return categoryId?.hashCode() ?: 0
    }

}


class DeprecationAndExpiration {
    var appVersion: String? = null
    var deprecated = false
    var blockerUrl: String? = null
    var expired = false
}

class Api {
    var name: ApiType? = null
    var url: String? = null
    var version: String? = null
    var timeOut: Int = 0
    var cacheable = false
    var appendWithKey: String = ""
}

data class SdkApi (
    var name: String? = null,
    var url: String? = null,
    var version: String? = null,
    var timeOut: Int = 0,
    var cacheable: Boolean = false
    )


class Navigation : Serializable, Comparable<Navigation> {
    var title: String? = null
    var primary: Boolean = false
    var newItem: Boolean = false
    var order: Int = 0
    var thumbnailURL: String? = null
    var actionConfig: ActionConfig? = null
    var menuType: MenuType? = null
    var nestedMenu: List<Navigation>? = null
    override fun compareTo(other: Navigation): Int {
        return this.order - other.order
    }

    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val formData: Navigation = obj as Navigation
        return formData.actionConfig == this.actionConfig
    }

    override fun hashCode(): Int {
        return this.actionConfig.hashCode()
    }
}

class ActionConfig : Serializable {
    var screenType: ScreenType? = null
    var actionUrl: String? = null
    var properties: Map<String, String>? = null

    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val formData: ActionConfig = obj as ActionConfig
        return formData.actionUrl == this.actionUrl
    }

    override fun hashCode(): Int {
        return this.actionUrl.hashCode()
    }
}


data class SdkConfig (
    var successful: Boolean = false,
    val baseUrl: String? = null,
    val accessId: String? = null,
    val features: Features? = null,
    val vendorConfig: VendorConfig? = null,
    val apis: List<SdkApi>? = null,
    val geofences: List<GeoFenceData>? = null,
    val appVersion: String? = null,
    val sdkConfigVersion: String? = null,
    val virtualGeofenceRadius: Float? = null
)

data class Features (
    val CAMERA: Boolean = false,
    val GYROSCOPE: Boolean = false,
    val PROXIMITY: Boolean = false,
    val ACCELROMETER: Boolean = false
)

//data class GeoCoordinate(
//        var latitude: Double? = null,
//        var locationId: String? = null,
//        var longitude: Double? = null
//)

data class VendorConfig (
    var onTripConfig: OnTripConfig? = null,
    var onActiveConfig: OnTripConfig? = null,
    var  trackingHours: Int? = 0
)

data class OnTripConfig (
    val overspeedLimit: Int = 0,
    var phoneUsageLimit: Int = 0,
    val overstoppingConfig: OverstoppingConfig? = null,
    val harshCorneringConfig: HarshCorneringConfig? = null,
    val harshBreakAndAccelerationConfig: HarshBrakeAndAccelerationConfig? = null,
    val outOfNetworkLimit: Int = 0,
    var postFrequency: Int = 0,
    var locationCaptureFrequency: Int = 0,
    var speedCaptureFrequency: Int = 0,
    var active: Boolean = false
)

data class OverstoppingConfig (
    val timeInMinute: Int = 0,
    val distanceinMeter: Int = 0
)

data class HarshCorneringConfig (
    val speed: Int = 0,
    val angle: Int = 0,
    val timeInSec: Int = 0
)

data class HarshBrakeAndAccelerationConfig (
    val speedThreshold: Int = 0,
    val timeThreshold: Int = 0
)


class GeoFences {
    var geofenceId: String? = null
    var geofenceType: String? = null
    var geofenceName: String? = null
    var polygonData: PolygonData? = null
    var circleData: CircleData? = null
}

class PolygonData {
    var geoCoordinates: List<GeoCoordinates>? = null
}

//class CircleData {
//    var geoCoordinates: List<GeoCoordinates>? = null
//    var radius: Float = 0f
//}

class ProfileInfo : Serializable {

    var userId: String? = null
    var email: String? = null
    var roleId: String? = null
    var vendorId: String? = null
    var name: String? = null
    var shortCode: String? = null
    var mobile: String? = null
    var profileImg: String? = null
    var status: String? = null
    var statistics: Statistics? = null
}

class Statistics {
    var totalTrips: Int? = null
    var eventMap: HashMap<String, Int>? = null
    var totalDistanceInKms: Double? = null
    var totalTimeInHours: Float? = null
}

class Reasons(var id: Int, var reason: String? = null) {
    var isSelected = false
}

enum class FormType : Serializable {
    START_TRIP, END_TRIP, UNKNOWN
}
