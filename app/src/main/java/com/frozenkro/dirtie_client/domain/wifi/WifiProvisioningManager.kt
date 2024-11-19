package com.frozenkro.dirtie_client.domain.wifi
// Visit this feature later

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class WifiProvisioningManager(private val context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    suspend fun connectToDeviceAP(ssid: String): Boolean {
        if (!hasLocationPermission()) {
            return false
        }

        return connectToWifiAndroid10Plus(ssid)
    }

    private suspend fun connectToWifiAndroid10Plus(ssid: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            val specifier = WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .build()

            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build()

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    // Bind this network to get the device's traffic
                    connectivityManager.bindProcessToNetwork(network)
                    continuation.resume(true)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    continuation.resume(false)
                }
            }

            // Request the network
            connectivityManager.requestNetwork(request, networkCallback)

            // Cancel the request if the coroutine is cancelled
            continuation.invokeOnCancellation {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }
        }

    suspend fun sendProvisioningData(
        ssid: String,
        password: String,
        provisioningToken: String
    ): Boolean {
        return try {
            // Assuming device hosts a simple HTTP server at 192.168.4.1
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("http://192.168.4.1/provision")
                .post(
                    JSONObject().apply {
                        put("ssid", ssid)
                        put("password", password)
                        put("token", provisioningToken)
                    }.toString().toRequestBody("application/json".toMediaType())
                )
                .build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
