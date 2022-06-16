package com.students.ameer.smoothcaranimation

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.Settings
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MainActivity : FragmentActivity(), OnMapReadyCallback {
    //google map object
    private var mMap: GoogleMap? = null
    var locationPermission = false
    var myLocation: Location? = null
    var myUpdatedLocation: Location? = null
    var Bearing = 0f
    var AnimationStatus = false
    var BitMapMarker: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermision()
        val bitmapdraw = resources.getDrawable(R.drawable.car_marker) as BitmapDrawable
        val b = bitmapdraw.bitmap
        BitMapMarker = Bitmap.createScaledBitmap(b, 110, 60, false)
    }

    //to get user location
    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap!!.isMyLocationEnabled = true
        mMap!!.setOnMyLocationChangeListener { location ->
            if (AnimationStatus) {
                myUpdatedLocation = location
            } else {
                myLocation = location
                myUpdatedLocation = location
                val updatedLatLng = LatLng(51.1247649, 71.4375257)
                val latlng = LatLng(location.latitude, location.longitude)
                carMarker =
                    mMap!!.addMarker(
                        MarkerOptions().position(latlng).flat(true).icon(
                            BitmapDescriptorFactory.fromBitmap(
                                BitMapMarker!!
                            )
                        )
                    )
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    updatedLatLng, 17f
                )
                mMap!!.animateCamera(cameraUpdate)
            }
            Bearing = location.bearing
            val updatedLatLng = LatLng(51.1247649, 71.4375257)
            val calendar = Calendar.getInstance()
            calendar[2022, 6, 15, 20, 38] = 17
            val startTime = calendar.timeInMillis
            calendar[2022, 6, 15, 20, 38] = 43
            val stoptime = calendar.timeInMillis
            val startPosition = LatLng(51.1247649, 71.4375257)
            val stopPosition = LatLng(51.1292898, 71.4393926)
            changePositionSmoothly(
                carMarker,
                startPosition,
                stopPosition,
                Bearing,
                startTime,
                stoptime
            )

            /*startTime = stoptime;
                    startPosition = stopPosition;
                    stopPosition = new LatLng(51.1308249,71.4295006);
                    calendar.set(2022, 06, 15, 20, 39, 19);
                    stoptime = calendar.getTimeInMillis();
                    changePositionSmoothly(carMarker, startPosition, stopPosition, Bearing, startTime, stoptime);
                    */
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()
    }

    fun changePositionSmoothly(
        myMarker: Marker?,
        oldLatLng: LatLng,
        newLatLng: LatLng,
        bearing: Float?,
        fakeStart: Long,
        fakeStop: Long
    ) {

        //final LatLng startPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val interpolator: Interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = (fakeStop - fakeStart).toFloat()
        val hideMarker = false
        handler.post(object : Runnable {
            var elapsed: Long = 0
            var t = 0f
            var v = 0f
            override fun run() {
                myMarker!!.rotation = bearing!!
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                v = interpolator.getInterpolation(t)
                val currentPosition = LatLng(
                    oldLatLng.latitude * (1 - t) + newLatLng.latitude * t,
                    oldLatLng.longitude * (1 - t) + newLatLng.longitude * t
                )
                myMarker.position = currentPosition

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                } else {
                    if (hideMarker) {
                        myMarker.isVisible = false
                    } else {
                        myMarker.isVisible = true
                    }
                }
                myLocation!!.latitude = newLatLng.latitude
                myLocation!!.longitude = newLatLng.longitude
            }
        })
    }

    private fun requestPermision() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            LocationstatusCheck()
            locationPermission = true
            //init google map fragment to show map.
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    LocationstatusCheck()
                    //if permission granted.
                    locationPermission = true
                    //init google map fragment to show map.
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment?
                    mapFragment!!.getMapAsync(this)
                    // getMyLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    fun LocationstatusCheck() {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    companion object {
        var carMarker: Marker? = null
        private const val LOCATION_REQUEST_CODE = 23
    }
}