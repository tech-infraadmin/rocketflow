package com.rf.taskmodule.ui.custom

import android.net.ParseException
import android.os.Handler
import android.os.Message
import com.google.gson.Gson
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.model.response.config.FileUrlsResponse
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.Log
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.Callable


/**
 * Created by Rahul Abrol on 26/7/19.
 *
 * Class works as a thread with custom implementation to hit
 * api and get the response.
 */
internal class Worker(private val key: String,
                      private val files: List<File>,
                      private val httpManager: com.rf.taskmodule.data.network.HttpManager,
                      private val api: Api,
                      private val mHandler: Handler?
) : Callable<FileUrlsResponse> {

    override fun call(): FileUrlsResponse {
        var message: Message? = null
        var fileResponse: FileUrlsResponse? = FileUrlsResponse()
        try {
            val hashMapFileRequest = HashMap<String, List<File>>()
            hashMapFileRequest[key] = files
            val response = httpManager.uploadMultipleFiles(api, hashMapFileRequest)
            //add the response
            fileResponse = Gson().fromJson(response, FileUrlsResponse::class.java)
            if (fileResponse.successful) {
                //update the data after getting response
                com.rf.taskmodule.utils.CommonUtils.updateData(fileResponse)
            } else {
                com.rf.taskmodule.utils.Log.e("Worker", "call(): response not success")
                message = mHandler?.obtainMessage(1, APIError.ErrorType.SERVER_DOWN.name)
            }
        } catch (var5: SocketTimeoutException) {
            com.rf.taskmodule.utils.Log.e("Worker", "call(): $var5")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.TIMEOUT.name)
        } catch (var6: SocketException) {
            com.rf.taskmodule.utils.Log.e("Worker", "call(): $var6")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.NETWORK_FAIL.name)
        } catch (var6: UnknownHostException) {
            com.rf.taskmodule.utils.Log.e("Worker", "call(): $var6")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.NETWORK_FAIL.name)
        } catch (var6: UnsupportedEncodingException) {
            com.rf.taskmodule.utils.Log.e("Worker", "call(): $var6")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.NETWORK_FAIL.name)
        } catch (e: ParseException) {
            com.rf.taskmodule.utils.Log.e("Worker", "call(): $e")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.PARSE_FAILED.name)
        } catch (ex: InterruptedException) {
            com.rf.taskmodule.utils.Log.e("Worker", "call():Thread got Interrupted: $ex")
        } catch (e: Exception) {
            com.rf.taskmodule.utils.Log.e("Worker", "call(): $e")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.UNKNOWN_ERROR.name)
        }
        message?.sendToTarget()
        return fileResponse!!
    }
}