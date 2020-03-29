package com.udacity.project4

import android.Manifest
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


/**
 *
 * Created by Mohamed Ibrahim on 3/24/20.
 */
fun FragmentActivity.checkLocationPermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit = {}
) {
    Dexter.withActivity(this)
        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        .withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                onGranted()
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest?,
                token: PermissionToken?
            ) {

            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                onDenied()
            }
        }).check()
}

fun FragmentActivity.checkGPS(isEnabledAction: (Boolean) -> Unit) {
    isEnabledAction(LocationManagerCompat.isLocationEnabled((this.getSystemService(Context.LOCATION_SERVICE) as LocationManager)))
}

fun FragmentActivity.enableGPS(
    startActivityRequestId: Int,
    onEnabled: (Boolean) -> Unit,
    onErrorAction: (Exception, Int) -> Unit = { e, i -> handleGPSRequestError(e, i) }
) {
    val locationRequest = with(LocationRequest.create()) {
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        this
    }
    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .build()
    LocationServices.getSettingsClient(this).checkLocationSettings(builder)
        .addOnSuccessListener {
            onEnabled(true)
        }
        .addOnFailureListener { onErrorAction(it, startActivityRequestId) }


}

private fun FragmentActivity.handleGPSRequestError(
    e: Exception,
    i: Int
) {
    when ((e as ApiException).statusCode) {
        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try { // Show the dialog by calling startResolutionForResult(), and check the
            // result in onActivityResult().
            val rae = e as ResolvableApiException
            rae.startResolutionForResult(this, i)
        } catch (sie: SendIntentException) {
            Log.i("TAG", "PendingIntent unable to execute request.")
        }
        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
            val errorMessage =
                "Location settings are inadequate, and cannot be " +
                        "fixed here. Fix in Settings."
            Log.e("TAG", errorMessage)
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}

fun Fragment.retrieveLocation(onSuccess: (Location) -> Unit, onError: (Exception) -> Unit) {
    LocationServices.getFusedLocationProviderClient(activity!!)
        .lastLocation.addOnSuccessListener { onSuccess(it) }.addOnFailureListener { onError(it) }
}