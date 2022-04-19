package com.example.veganbuddy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.veganbuddy.databinding.ActivityRideDetailsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*

class RideDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRideDetailsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var lastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRideDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.CANADA)





        var intent = intent
        val code =  intent.getStringExtra("CODE")

        getLastLocation(code)


        binding.txtDest.setText(code)
//        val desti = findViewById<EditText>(R.id.txtDest)
//        desti.text = "CODE"+code+"";
        binding.btnMap.setOnClickListener {
            val intent = Intent(this,MapsActivity::class.java)
            intent.putExtra("postalCode",code)
            startActivity(intent)
        }
    }

    private fun getDistance(
        startLat: Double,
        startLang: Double,
        endLat: Double,
        endLang: Double
    ): Float {
        val locStart = Location("")
        locStart.latitude = startLat
        locStart.longitude = startLang
        val locEnd = Location("")
        locEnd.latitude = endLat
        locEnd.longitude = endLang
        return locStart.distanceTo(locEnd)
    }

    private fun getLastLocation(code: String?) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this){ location ->
            if(location != null){
                lastLocation = location
                val currentLatLng = LatLng(location.latitude,location.longitude)
                println(location.latitude)
                println(location.longitude)
                try{
                    val addresses: List<Address> = geocoder.getFromLocationName(code,1)
                    val address = addresses.get(0)
                    println(address.latitude)
                    println(address.longitude)
                    var dist = getDistance(address.latitude,address.longitude,location.latitude,location.longitude) / 1000
                    val fDist: Double = String.format("%.2f",dist).toDouble()
                    findEstmatedPrice(fDist)
                    binding.txtDist.setText("$fDist Km")
                    println(dist)

                }catch(exception: IOException){
                    exception.printStackTrace()
                }
            }
        }
    }

    private fun findEstmatedPrice(fDist: Double) {
        var eP: Double = 0.00
        var perKm:Double = 1.00

        eP = fDist * perKm
        val fEP: Double = String.format("%.2f",eP).toDouble()
        binding.txtPrice.setText("CAD $fEP")
    }
}