package com.thedevwolf.locationlivedata

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.lang.ref.WeakReference
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class CurrentLocation private constructor(val appContext: Activity) : LiveData<Location>() {
    private val mFusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    private var weakActivity: WeakReference<Activity>? = null


    private var mLocationRequest: LocationRequest? = null

    private var mLocationCallback: LocationCallback? = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (location != null)
                    value = location
            }
        }
    }


    init {
        weakActivity = WeakReference(appContext)


        Dexter.withActivity(weakActivity!!.get())
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        getLocation()
                    } else {
                        //Dialog for required permission
                        Log.e("Permission", "permission is required")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            })
            .onSameThread().check()
        //getLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        createLocationRequest()
        checkDeviceSettings()
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 10000
        mLocationRequest!!.fastestInterval = 5000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback!!, null)
    }

    override fun onInactive() {
        super.onInactive()
        if (mLocationCallback != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback!!)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: CurrentLocation? = null

        fun getInstance(appContext: Activity): CurrentLocation {


            if (instance == null) {
                instance = CurrentLocation(appContext)
            }
            return instance as CurrentLocation
        }
    }


    private fun checkDeviceSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)
        val client: SettingsClient = LocationServices.getSettingsClient(appContext)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)



            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{
                        showDialog(exception)
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }
                    LocationSettingsStatusCodes.SUCCESS -> {
                        getLocation()
                    }

                }// Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.

            }
        }


    }

    private fun showDialog(exception: ApiException) {
        try {
            val resolvable = exception as ResolvableApiException
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            resolvable.startResolutionForResult(
                weakActivity!!.get(),
                100
            )

            // weakActivity!!.get()!!.startActivityForResult(weakActivity!!.get()!!.intent,100)

        } catch (e: IntentSender.SendIntentException) {
            // Ignore the error.
        } catch (e: ClassCastException) {
            // Ignore, should be an impossible error.
        }
    }

}