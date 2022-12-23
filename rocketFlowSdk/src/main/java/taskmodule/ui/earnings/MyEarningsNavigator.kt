package taskmodule.ui.earnings

import taskmodule.ui.base.BaseSdkNavigator

/**
 * Created by Rahul Abrol on 27/12/19.
 */
interface MyEarningsNavigator : BaseSdkNavigator {
    fun viewDetails()
    fun search()
    fun selectDateRange()

}