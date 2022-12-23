package taskmodule.ui.scanqrcode
import com.google.gson.annotations.SerializedName
import taskmodule.data.model.BaseResponse


data class QrCodeResponse(

    @SerializedName("id")
    var id: String?,
    @SerializedName("type")
    var type: QrCodeValueType?
):BaseResponse()
enum class QrCodeValueType{
    TASK,USER,PRODUCT
}