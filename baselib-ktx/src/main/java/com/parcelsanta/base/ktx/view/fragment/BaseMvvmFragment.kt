package com.parcelsanta.base.ktx.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.parcelsanta.base.ktx.data.net.ApiResultException
import com.parcelsanta.base.ktx.view.IBaseView
import com.parcelsanta.base.ktx.view.LayoutIdInf
import com.parcelsanta.base.ktx.vm.VMResult
import com.parcelsanta.base.ktx.vm.onFailure
import com.parcelsanta.base.ktx.vm.onLoading
import com.parcelsanta.base.ktx.vm.onSuccess
import com.parcelsanta.base.ktx.widget.ProgressBarHandler
import org.jetbrains.anko.support.v4.toast
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

/**
 * @author : yan
 * @date   : 2019/10/23 15:13
 * @desc   : BaseMvvmFragment
 *          与 BaseMvvmActivity 类似
 */
abstract class BaseMvvmFragment<VM : ViewModel> : BaseFragment(), IBaseView, LayoutIdInf {

    protected lateinit var defaultVM: VM
        private set

    private var progressBarHandler: ProgressBarHandler? = null
    private var controlledButtons: Array<out View>? = null
    //只对单个请求有效
    protected var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultVM = obtainViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (progressbarEnable()) {
            if (contentView is ViewGroup) {
                progressBarHandler = ProgressBarHandler(requireContext(), contentView as ViewGroup)
            }
        }
    }

    /**
     * 生成一个ViewModel
     * 通过遍历父类是否是带泛型的类来进行获取
     */
    private fun obtainViewModel(): VM {
        var tmpClazz: Class<*>? = javaClass
        while (true) {
            if (tmpClazz == null || tmpClazz == Any::class.java) throw IllegalArgumentException("cannot initialize view model instance in ${javaClass.name}")

            val genericSuperclass = tmpClazz.genericSuperclass
            if (genericSuperclass is ParameterizedType) {
                val clazz = genericSuperclass.actualTypeArguments[0] as Class<VM>

                if (clazz == ViewModel::class.java || Modifier.isAbstract(clazz.modifiers)) {
                    // ViewModel 抽象类无法实例化
                    throw IllegalArgumentException("cannot initialize the abstract ViewModel instance when obtainViewModel in ${clazz.name}")
                }
                return ViewModelProviders.of(this).get(clazz)
            }
            tmpClazz = tmpClazz.superclass
        }
    }

    protected open fun progressbarEnable() = true

    protected fun setButtonControl(vararg buttons: View) {
        controlledButtons = buttons
    }

    override fun showLoading() {
        isLoading = true
        progressBarHandler?.show()

        controlledButtons?.forEach {
            it.isEnabled = false
        }
    }

    override fun hideLoading() {
        isLoading = false
        progressBarHandler?.hide()
        controlledButtons?.forEach {
            it.isEnabled = true
        }
    }

    override fun onError(throwable: Throwable) {
        if (throwable is ApiResultException) {
            toast(throwable.toString())
        } else {
            toast(throwable.message.orEmpty())
        }
    }

    protected fun <T> LiveData<VMResult<T>>.observeVMResult(success: (T) -> Unit) {
        observe(viewLifecycleOwner) {
            it.onLoading {
                showLoading()
            }.onFailure {
                onError(it)
                hideLoading()
            }.onSuccess { result ->
                success(result)
                hideLoading()
            }
        }
    }

    protected fun <T> LiveData<T>.observeInFragment(observer: (T) -> Unit) {
        observe(viewLifecycleOwner, observer)
    }
}