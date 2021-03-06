package xyz.arnau.muvicat.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import xyz.arnau.muvicat.BuildConfig.APPLICATION_ID
import xyz.arnau.muvicat.MuvicatApplication
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.utils.QueuedSnack
import xyz.arnau.muvicat.ui.utils.SnackQueue
import javax.inject.Inject

abstract class LocationAwareActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    lateinit var app: MuvicatApplication
    @Inject
    lateinit var snackQueue: SnackQueue

    var lastLocation: Location? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        app = application as MuvicatApplication
    }

    override fun onStart() {
        super.onStart()

        if (!app.isInFirebaseTestLab) {
            if (!checkPermissions())
                requestPermissions()
            else
                getLastLocationFromClient()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Timber.i("User location was cancelled.")
                (grantResults[0] == PERMISSION_GRANTED) -> {
                    getLastLocationFromClient()
                    FirebaseAnalytics.getInstance(this)
                        .logEvent("location_permission_granted", null)
                }
                else -> {
                    FirebaseAnalytics.getInstance(this)
                        .logEvent("location_permission_denied", null)
                    showSnackbar(
                        R.string.permission_denied_explanation,
                        R.string.settings,
                        View.OnClickListener {
                            val intent = Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                            app.hasRequestedForLocationPermission = true
                        })
                }
            }
        }
    }

    abstract fun processLastLocation(location: Location)

    @SuppressLint("MissingPermission")
    private fun getLastLocationFromClient() {
        fusedLocationClient.lastLocation
            .addOnCompleteListener(this) {
                if (it.isSuccessful && it.result != null) {
                    processLastLocation(it.result)
                    lastLocation = it.result
                } else {
                    Timber.w(it.exception)
                    showSnackbar(R.string.no_location_detected)
                }
            }
    }

    private fun checkPermissions() =
        ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        if (!app.hasRequestedForLocationPermission) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
                showSnackbar(
                    R.string.permission_rationale,
                    android.R.string.ok,
                    View.OnClickListener {
                        startLocationPermissionRequest()
                    }, SnackQueue.COULDNT_GET_LOCATION)
            } else {
                startLocationPermissionRequest()
            }
        }
    }

    private fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int? = null,
        listener: View.OnClickListener? = null,
        code: Int? = null
    ) {
        snackQueue.enqueueSnack(QueuedSnack(
            this,
            getString(snackStrId),
            Snackbar.LENGTH_LONG,
            actionStrId?.let{ getString(it) },
            listener,
            5
        ), code)
    }
}