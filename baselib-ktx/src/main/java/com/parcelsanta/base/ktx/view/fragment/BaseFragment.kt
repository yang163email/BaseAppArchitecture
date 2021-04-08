package com.parcelsanta.base.ktx.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.AnkoLogger

/**
 * @author : yan
 * @date : 2019/7/2 9:47
 * @desc : BaseFragment
 *          与 BaseActivity 设计类似
 */
abstract class BaseFragment : Fragment(), AnkoLogger, CoroutineScope by MainScope() {

    protected val TAG = javaClass.simpleName

    //此变量只有在 onActivityCreated 调用后才有值
    protected var contentView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        contentView = view
    }

    protected open fun useEventBus(): Boolean = false

    override fun onDestroyView() {
        super.onDestroyView()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }
}
