package com.parcelsanta.base.ktx.view.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.parcelsanta.base.ktx.data.net.ApiResultException
import com.parcelsanta.base.ktx.view.IBaseView
import com.parcelsanta.base.ktx.view.LayoutIdInf
import com.parcelsanta.base.ktx.vm.VMResult
import com.parcelsanta.base.ktx.vm.onFailure
import com.parcelsanta.base.ktx.vm.onLoading
import com.parcelsanta.base.ktx.vm.onSuccess
import com.parcelsanta.base.ktx.widget.ProgressBarHandler
import org.jetbrains.anko.toast
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

/**
 * @author : yan
 * @date   : 2019/9/11 15:19
 * @desc   : BaseMvvmActivity, 结合viewmodel使用
 */
abstract class BaseMvvmActivity<VM : ViewModel> : BaseActivity(), IBaseView, LayoutIdInf {

    protected lateinit var defaultVM: VM
        private set

    private var progressBarHandler: ProgressBarHandler? = null
    private var controlledButtons = arrayListOf<View>()
    //只对单个请求有效
    protected var isLoading = false

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
                return ViewModelProvider(this).get(clazz)
//                return ViewModelProviders.of(this).get(clazz)
            }
            tmpClazz = tmpClazz.superclass
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (progressbarEnable()) {
            progressBarHandler = ProgressBarHandler(this, contentView)
        }
        defaultVM = obtainViewModel()
        setContentView(layoutId)
    }

    protected open fun progressbarEnable() = true

    protected fun addButtonControl(vararg buttons: View) {
        buttons.forEach {
            if (!controlledButtons.contains(it)) {
                controlledButtons.add(it)
            }
        }
    }

    override fun showLoading() {
        isLoading = true
        progressBarHandler?.show()

        controlledButtons.forEach {
            it.isEnabled = false
        }
    }

    override fun hideLoading() {
        isLoading = false
        progressBarHandler?.hide()
        controlledButtons.forEach {
            it.isEnabled = true
        }
        controlledButtons.clear()
    }

    override fun onError(throwable: Throwable) {
        if (throwable is ApiResultException) {
            toast(throwable.toString())
        } else {
            toast(throwable.message.orEmpty())
        }
    }

    protected fun <T> LiveData<VMResult<T>>.observeVMResult(success: (T) -> Unit) {
        observe(this@BaseMvvmActivity) {
            it.onLoading {
                showLoading()
            }.onFailure { throwable ->
                onError(throwable)
                hideLoading()
            }.onSuccess { result ->
                success(result)
                hideLoading()
            }
        }
    }

    protected fun <T> LiveData<T>.observeInActivity(onChanged: (T) -> Unit) {
        observe(this@BaseMvvmActivity, onChanged)
    }

    protected fun <T> LiveData<Result<T>>.observeResult(success: (T) -> Unit) {
        observe(this@BaseMvvmActivity) { result ->
            result.onSuccess(success)
                .onFailure {
                    onError(it)
                }
        }
    }

}