package taskmodule.ui.userlisting

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.rocketflow.sdk.RocketFlyer
import taskmodule.BR
import taskmodule.R
import taskmodule.data.local.prefs.PreferencesHelper
import taskmodule.data.model.ResponseBasic2
import taskmodule.data.model.request.ExecuteUpdateRequest
import taskmodule.data.model.request.TaskData
import taskmodule.data.model.response.config.DynamicFormData
import taskmodule.data.model.response.config.UserData
import taskmodule.data.model.response.config.UserListResponse
import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.data.network.HttpManager
import taskmodule.databinding.ActivityUserListNewSdkBinding
import taskmodule.ui.base.BaseSdkActivity
import taskmodule.utils.CommonUtils
import taskmodule.utils.JSONConverter
import taskmodule.utils.Log
import taskmodule.utils.TrackiToast
import kotlin.collections.ArrayList

class UserListNewActivity : BaseSdkActivity<ActivityUserListNewSdkBinding, UserListNewViewModel>(), UserListNewNavigator, UserListNewAdapter.onUserSelected{

    lateinit var mUserListViewModel: UserListNewViewModel

    lateinit var preferencesHelper: PreferencesHelper
    lateinit var httpManager: HttpManager

    lateinit var adapter: UserListNewAdapter

    lateinit var searchView: SearchView
    
    var listSelectedUsers = ArrayList<String>()

    private var mLayoutManager: LinearLayoutManager? = null

    lateinit var binding: ActivityUserListNewSdkBinding
    var roleIds: String = "na"
    var users: String = ""
    lateinit var request: ExecuteUpdateRequest
    lateinit var usersList: ArrayList<UserData>

    private var rvUserList: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        usersList = ArrayList()
        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!
        searchView = binding.svSearchUsers
        adapter = UserListNewAdapter(ArrayList())
        setRecyclerView()
        adapter.setListener(this)

        mUserListViewModel.navigator = this
        
        if (intent.hasExtra("roleIds"))
        {
            roleIds = intent.getStringExtra("roleIds").toString()
            getUsers()
            request = intent.getSerializableExtra("request") as ExecuteUpdateRequest
        }
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null || newText != "")
                    adapter.addFilter(newText.toString())
                else
                    adapter.addFilter("")
                return true
            }
        })
        binding.btnUsersSubmit.setOnClickListener {

            if (listSelectedUsers.size > 0){
                for (i in 0 until listSelectedUsers.size){
                    users += if (i > 0)
                        ",${listSelectedUsers[i]}"
                    else
                        listSelectedUsers[i]
                }
                var dynamicFormData = DynamicFormData()
                dynamicFormData.key = "SELECT_BUDDY"
                dynamicFormData.value = users
                var list = ArrayList<DynamicFormData>()
                list.add(dynamicFormData)
                val taskData = TaskData(request.ctaId,list,request.timestamp)
                request.taskData = taskData
                showLoading()
                mUserListViewModel.executeUpdates(httpManager,request)

            }
            else{
                TrackiToast.Message.showShort(this,"Please select atleast one user")
            }
        }


    }

    private fun getUsers(){
        Log.e("getUsers","$roleIds")
        mUserListViewModel.getUserList(httpManager,roleIds,null,null,true)
    }

    private fun setRecyclerView() {
        if (rvUserList == null) {
            rvUserList = binding.rvUserList
            //  mLayoutManager= (LinearLayoutManager) rvAttendance.getLayoutManager();
            try {
                mLayoutManager = LinearLayoutManager(this)
                mLayoutManager!!.orientation = RecyclerView.VERTICAL
                rvUserList!!.layoutManager = mLayoutManager
                rvUserList!!.itemAnimator = DefaultItemAnimator()

            } catch (e: Exception) {
            }
        }
        rvUserList!!.adapter = adapter
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_list_new_sdk
    }

    override fun getViewModel(): UserListNewViewModel {
        val factory = RocketFlyer.dataManager()?.let { UserListNewViewModel.Factory(it) } // Factory
        if (factory != null) {
            mUserListViewModel = ViewModelProvider(this, factory)[UserListNewViewModel::class.java]
        }
        return mUserListViewModel!!
    }




    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@UserListNewActivity)) {
            val jsonConverter: JSONConverter<UserListResponse> = JSONConverter()
            var response: UserListResponse = jsonConverter.jsonToObject(result.toString(), UserListResponse::class.java) as UserListResponse
            if (response.data != null) {
                setRecyclerView()
                val userList = response.data as ArrayList<UserData>
                Log.e("listUserList","$userList")
                usersList = response.data as ArrayList<UserData>
                adapter.addItems(usersList)
            } else {
                setRecyclerView()
                adapter.addItems(ArrayList())
            }
        } else {
            setRecyclerView()
            adapter.addItems(ArrayList())
        }
    }

    override fun handleExecuteUpdateResponse(
        apiCallback: ApiCallback?,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        val gson = Gson()
        val responseBasic = gson.fromJson(
            result.toString(),
            ResponseBasic2::class.java
        )
        if (responseBasic != null){
            if (responseBasic.successful == true){
                finish()
                //startActivity(Intent(this,MainActivity::class.java))
            }
        }
        else{
            TrackiToast.Message.showShort(this,"Problem in Server Please try later")
        }
    }

    override fun addUser(userId: String) {
        listSelectedUsers.add(userId)
    }

    override fun removeUser(userId: String) {
        listSelectedUsers.remove(userId)
    }
}