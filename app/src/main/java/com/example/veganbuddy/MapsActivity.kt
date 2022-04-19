package com.example.veganbuddy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.veganbuddy.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.example.veganbuddy.Common.Common
import com.example.veganbuddy.Model.MyPlaces
import com.example.veganbuddy.Remote.IGoogleAPIService
import com.example.veganbuddy.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var latitude: Double= 0.toDouble()
    private var longitude: Double= 0.toDouble()

    private lateinit var mLastLocation:Location
    private var mMarker: Marker? = null

    //location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    lateinit var locationCallback: LocationCallback

    companion object{
        private const val MY_PERMISSION_CODE: Int = 1000
    }

    lateinit var mService:IGoogleAPIService

    private var currentPlace:MyPlaces?= null
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
          setContentView(binding.root)
//        setContentView(com.example.veganbuddy.R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(com.example.veganbuddy.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //init service
        mService = Common.googleApiService

        binding.fltBtn.setOnClickListener {
            val intent = Intent(this,RestaurantActivity::class.java)
            startActivity(intent)
        }

        //request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkLocationPermission()){
                buildLocationRequest()
                buildLocationCallback()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                    Looper.myLooper()!!
                )
            }

        }else{
            buildLocationRequest()
            buildLocationCallback()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                Looper.myLooper()!!
            )
        }

//               nearByPlace("restaurant")

    }

    private fun nearByPlace(typePlace: String){
        //clear all marker on maps
        mMap.clear()

        //build URL request base on location
        val url = getUrl(latitude, longitude, typePlace)

        mService.getNearbyPlaces(url).enqueue(object : Callback<MyPlaces>{
            override fun onFailure(call: Call<MyPlaces>, t: Throwable) {
                Toast.makeText(baseContext, "${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<MyPlaces>, response: Response<MyPlaces>) {
                currentPlace = response.body()

                if (response.isSuccessful){
                    for (i in 0 until response.body()!!.results.size){
                        val markerOptions = MarkerOptions()
                        val googlePlace = response.body()!!.results[i]
                        val lat = googlePlace.geometry.location.lat
                        val lng = googlePlace.geometry.location.lng
                        val placeName = googlePlace.name
                        val latLng = LatLng(lat, lng)

                        markerOptions.position(latLng)
                        markerOptions.title(placeName)
                        if (typePlace.equals("restaurant"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(com.example.veganbuddy.R.drawable.ic_restaurant))


                        markerOptions.snippet(i.toString()) //asign index for marker

                        mMap.addMarker(markerOptions)


                    }

                    //move camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude,longitude)))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))


                }
            }

        })
    }

    private fun getUrl(latitude: Double, longitude: Double, typePlace: String): String {

        val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?location=$latitude,$longitude")
        googlePlaceUrl.append("&radius=20000")
        googlePlaceUrl.append("&type=$typePlace")
        googlePlaceUrl.append("&keyword=cruise&key=AIzaSyConCQqZx8pAOX7loj7RnHDYdbC0B-ntxk")

        Log.d("URL DEBUG", googlePlaceUrl.toString())

        return googlePlaceUrl.toString()

    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                mLastLocation = p0!!.locations.get(p0.locations.size-1) //get last location

                if (mMarker != null){
                    mMarker!!.remove()
                }

                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)

                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Your Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                mMarker = mMap.addMarker(markerOptions)

                //move camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
            }
        }

    }

    private fun buildLocationRequest() {
        locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f

    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE)
            else
                ActivityCompat.requestPermissions(this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE)

            return false
        }else
            return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MY_PERMISSION_CODE->{
                if (grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        if (checkLocationPermission()){
                            buildLocationRequest()
                            buildLocationCallback()

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                            Looper.myLooper()?.let {
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                                    it
                                )
                            }

                            mMap.isMyLocationEnabled = true
                        }

                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mMap.isMyLocationEnabled = true
            }
        }else{
            mMap.isMyLocationEnabled = true

            //enable zoom output
            mMap.uiSettings.isZoomControlsEnabled = true
        }

        nearByPlace("restaurant")

        mMap.setOnMarkerClickListener {

            val intent = Intent(this,RideDetailsActivity::class.java)
            startActivity(intent)
////           startActivity(Intent(this@MapsActivity, ViewPlaceActivity::class.java)
            true
        }


    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }


}
