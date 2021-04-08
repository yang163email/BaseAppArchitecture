package com.yan.myapplication.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.parcelsanta.base.ktx.common.AppContext
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors

/**
 * @author : yan
 * @date   : 2019/7/12 14:13
 * @desc   : 使用google location service
 *           在 app gradle 中配置 implementation 'com.google.android.gms:play-services-location:16+'
 */
object GoogleLocationHelper {

    private val TAG = javaClass.simpleName
    var neededAddress: Boolean = true

    private val fusedLocationClient: FusedLocationProviderClient
    private var geocoder: Geocoder

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    var currentLocation: Location? = null

    private val addressCallbacks = arrayListOf<AddressCallback>()

    private val executors by lazy { Executors.newSingleThreadExecutor() }

    const val NORMAL_FAILED = -1    //不可控原因
    const val CUSTOM_FAILED = 1     //位置服务没有打开

    private const val SUCCESS_RESULT = 0
    private const val FAILURE_RESULT = 1

    init {
        val context = AppContext
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        geocoder = Geocoder(context, Locale.getDefault())

        initLocationCallback()

        initLocationSettings(context)
    }

    private fun initLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.size > 0) {
                    //有值
                    val location = locationResult.locations[0]
                    currentLocation = location
                    if (neededAddress) {
                        fetchAddressDataAsync(location)
                    } else {
                        invokeSuccessCallback(location.latitude, location.longitude, "")
                    }
                } else {
                    val e = LocationException("cannot find your location!", NORMAL_FAILED)
                    invokeFailureCallback(e)
                }
            }
        }
    }

    private fun initLocationSettings(context: Context) {
        locationRequest = createLocationRequest()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    val e = LocationException(exception.message, CUSTOM_FAILED)
                    invokeFailureCallback(e)
                    if (context is Activity) {
                        exception.startResolutionForResult(context, 100)
                    } else {
                        exception.printStackTrace()
                    }
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    fun setInterval(interval: Long) {
        locationRequest.interval = interval
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @SuppressLint("MissingPermission")
    fun requestLastLocation() {
        fusedLocationClient.lastLocation?.addOnSuccessListener { location: Location? ->
            if (location == null) return@addOnSuccessListener

            currentLocation = location
            if (!Geocoder.isPresent()) {
                val e = LocationException("no geocoder available", NORMAL_FAILED)
                invokeFailureCallback(e)
                return@addOnSuccessListener
            }
            if (neededAddress) {
                fetchAddressDataAsync(location)
            } else {
                invokeSuccessCallback(location.latitude, location.longitude, "")
            }
        }
    }

    private fun invokeSuccessCallback(latitude: Double, longitude: Double, address: String) {
        addressCallbacks.forEach {
            it.onSuccess(latitude, longitude, address)
        }
    }

    private fun invokeFailureCallback(exception: LocationException) {
        addressCallbacks.forEach {
            it.onFailure(exception)
        }
    }

    private fun fetchAddressDataAsync(location: Location) {
        executors.execute {
            val data = fetchAddressData(location)
            MainHandler.post {
                deliverResult(location, data.first, data.second)
            }
        }

    }

    private fun fetchAddressData(location: Location): Pair<Int, String> {
        var errorMessage = ""
        var addresses: List<Address> = emptyList()
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = "service not available"
            Log.e(TAG, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "invalid lat long used"
            Log.e(
                TAG, "$errorMessage. Latitude = $location.latitude , " +
                        "Longitude =  $location.longitude", illegalArgumentException
            )
        }

        // Handle case where no address was found.
        val resultCode: Int
        val output: String
        if (addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no address found"
                Log.e(TAG, errorMessage)
            }
            resultCode = NORMAL_FAILED
            output = errorMessage
        } else {
            val address = addresses[0]
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            val addressFragments = with(address) {
                (0..maxAddressLineIndex).map { getAddressLine(it) }
            }
            resultCode = SUCCESS_RESULT
            output = addressFragments.joinToString(separator = "\n")
        }
        return Pair(resultCode, output)
    }

    private fun deliverResult(location: Location, resultCode: Int, output: String) {
        if (resultCode == SUCCESS_RESULT && currentLocation != null) {
            Log.e(TAG, "onReceiveResult(): address found")
            invokeSuccessCallback(location.latitude, location.longitude, output)
        } else {
            val e = LocationException(output, NORMAL_FAILED)
            invokeFailureCallback(e)
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun addAddressCallback(addressCallback: AddressCallback) {
        if (addressCallbacks.contains(addressCallback)) return

        addressCallbacks.add(addressCallback)
    }

    fun removeAddressCallback(addressCallback: AddressCallback) {
        addressCallbacks.remove(addressCallback)
    }

    fun onResume() {
        // 获取上一个位置，共用 onSuccess() 回调方法
        requestLastLocation()
        startLocationUpdates()
    }

    fun onPause() {
        stopLocationUpdates()
    }

    private object MainHandler : Handler(Looper.getMainLooper())

    interface AddressCallback {
        fun onSuccess(latitude: Double, longitude: Double, address: String)

        fun onFailure(exception: LocationException)
    }

    class LocationException(message: String?, val type: Int) : Exception(message)
}