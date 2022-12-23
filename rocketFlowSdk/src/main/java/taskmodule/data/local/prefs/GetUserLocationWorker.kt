package taskmodule.data.local.prefs

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import taskmodule.data.network.HttpManager
//import taskmodule.ui.placelist.MyPlaceNavigator
//import taskmodule.ui.placelist.MyPlacesViewModel
import javax.inject.Inject

class GetUserLocationWorker(var context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

//    @Inject
//    lateinit var mMyPlaceViewModel: MyPlacesViewModel

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper


    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
//    init {
//
//        //mMyPlaceViewModel = ViewModelProviders.of(context as AppCompatActivity, mViewModelFactory).get(MyPlacesViewModel::class.java)
//        mMyPlaceViewModel = MyPlacesViewModel(dataManager,AppSchedulerProvider())
//    }

    override fun doWork(): Result {

        //val api = TrackiApplication.getApiMap()[ApiType.USER_LOCATIONS]
//        if(api!=null) {
//            mMyPlaceViewModel.navigator=this
//            mMyPlaceViewModel.getLocation(httpManager, api!!)
//            return Result.success()
//        }else{
//            return Result.failure()
//        }
        return Result.failure()
    }

//    override fun showTimeOutMessage(callback: ApiCallback) {
//    }

//    override fun handleMyPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
//        if (CommonUtils.handleResponse(callback, error, result, context)) {
//            var jsonConverter = JSONConverter<LocationListResponse>()
//            var response = jsonConverter.jsonToObject(result.toString(), LocationListResponse::class.java)
//            if (response.successful!!) {
//                if(response.hubs!=null && response.hubs!!.isNotEmpty()){
//                    var hubs= response.hubs
//                    preferencesHelper.saveUserHubList(hubs)
//
//                }
//            }
//        }
//    }

//    override fun handleDeleteMyPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
//    }
//
//    override fun openAddPlaceActivity() {
//    }
//
//    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
//    }




}