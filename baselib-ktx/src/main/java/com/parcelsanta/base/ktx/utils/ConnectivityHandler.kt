package app.santaone.customer.voip.handler

import android.content.Context
import android.net.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import org.jetbrains.anko.connectivityManager

/**
 * @author : yan
 * @date   : 2020/6/3 17:22
 * @desc   : ConnectivityHandler
 */
class ConnectivityHandler(val context: Context, val availableCallback: () -> Unit) :
    LifecycleObserver {

    private val TAG = "ConnectivityHandler"

    private val connectivityManager = context.connectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onLost(network: Network) {
            Log.d(TAG, "onLost: ")
        }

        override fun onAvailable(network: Network) {
            Log.d(TAG, "onAvailable: ")
            availableCallback()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun configNetworkListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request =
                    NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun unregisterListener() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

}