package com.parcelsanta.base.ktx.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * @author : yan
 * @date   : 2019/9/6 16:29
 * @desc   : PermissionUtil
 */
object PermissionUtil {

    fun isGranted(context: Context, permission: String) =
        Build.VERSION.SDK_INT < 23 ||
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED

}

@SuppressLint("CheckResult")
fun FragmentActivity.checkSinglePermission(
    permission: String,
    permissionGrantedAction: () -> Unit
) {
    val rxPermissions = RxPermissions(this)
    if (rxPermissions.isGranted(permission)) {
        permissionGrantedAction()
        return
    }
    //点击申请权限
    rxPermissions
        .requestEach(permission)
        .subscribe { requestPermission ->
            if (requestPermission.granted) {
                // 用户已经同意该权限
                permissionGrantedAction()
            } else if (requestPermission.shouldShowRequestPermissionRationale) {
                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                Log.e(
                    "checkSinglePermission",
                    "${requestPermission.name} is denied. More info should be provided."
                )
            } else {
                // 用户拒绝了该权限，并且选中『不再询问』
                Log.e("checkSinglePermission", "${requestPermission.name} is denied.")
            }
        }
}

@SuppressLint("CheckResult")
fun FragmentActivity.checkMultiPermission(
    permissions: Array<String>,
    permissionGrantedAction: () -> Unit
) {
    val rxPermissions = RxPermissions(this)
    val allGranted = permissions.all {
        rxPermissions.isGranted(it)
    }

    if (allGranted) {
        permissionGrantedAction()
        return
    }
    val size = permissions.size
    var grantedCount = 0
    //点击申请权限
    rxPermissions
        .requestEach(*permissions)
        .subscribe { permission ->
            if (permission.granted) {
                // 用户已经同意该权限
                if (++grantedCount == size) {
                    permissionGrantedAction()
                }
            } else if (permission.shouldShowRequestPermissionRationale) {
                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                Log.e(
                    "checkMultiPermission",
                    "${permission.name} is denied. More info should be provided."
                )
            } else {
                // 用户拒绝了该权限，并且选中『不再询问』
                Log.e("checkMultiPermission", "${permission.name} is denied.")
            }
        }
}