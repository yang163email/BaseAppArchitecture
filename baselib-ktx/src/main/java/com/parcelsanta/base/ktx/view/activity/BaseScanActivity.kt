package com.parcelsanta.base.ktx.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.parcelsanta.base.ktx.utils.checkSinglePermission
import org.jetbrains.anko.error
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author : yan
 * @date : 2019/3/25 10:39
 * @desc : 扫码base类，抽象重复部分
 */
abstract class BaseScanActivity<VM : ViewModel> : BaseMvvmActivity<VM>(), QRCodeView.Delegate {

    protected abstract val zxingView: QRCodeView

    private var mExecutorService: ExecutorService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        zxingView.setDelegate(this)

        mExecutorService = Executors.newSingleThreadExecutor()
    }

    @SuppressLint("CheckResult")
    protected fun requestPermissions() {
        checkSinglePermission(Manifest.permission.CAMERA, ::openCamera)
    }

    override fun onStart() {
        super.onStart()

        requestPermissions()
        //        openCamera();
        //        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        //        mZXingView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
    }

    private fun openCamera() {
        //异步执行，考虑的是 Camera.open() 是耗时任务
        mExecutorService!!.execute {
            //                mZXingView.startCamera();
            zxingView.startSpotAndShowRect()
        }
    }

    override fun onStop() {
        zxingView.stopCamera() // 关闭摄像头预览，并且隐藏扫描框
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        zxingView.startSpotAndShowRect() // 显示扫描框，并且延迟0.5秒后开始识别

        //   if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
        //  final String picturePath = BGAPhotoPickerActivity.getSelectedPhotos(data).get(0);
        // 本来就用到 QRCodeView 时可直接调 QRCodeView 的方法，走通用的回调
        //mZXingView.decodeQRCode(picturePath);
        //      }
        //      }
    }

    override fun onDestroy() {
        zxingView.onDestroy() // 销毁二维码扫描控件
        super.onDestroy()
    }

    override fun onScanQRCodeOpenCameraError() {
        error("二维码结果: 打开相机出错")
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

    }

    /**
     * 震动
     */
    protected fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }

}
