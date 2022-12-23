package taskmodule.ui.taskdetails.timeline.skuinfopreview
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import taskmodule.data.model.response.config.DynamicFormData
import taskmodule.ui.category.ProductDescription
import taskmodule.utils.ShiftTime
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UnitInfoRequest(
    var id: String? = null,
    var filterBy: String? = null,
    var pageNumber: Int? = null,
    var limit: Int? = null,
    var pids: ArrayList<String>? = null,
    ):Parcelable
