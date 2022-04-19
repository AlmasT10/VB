package com.example.veganbuddy.Common

import com.example.veganbuddy.Remote.IGoogleAPIService
import com.example.veganbuddy.Remote.RetrofitClient
import com.example.veganbuddy.Remote.RetrofitScalarsClient
import com.example.veganbuddy.Model.Result

object Common {
    private val GOOGLE_API_URL = "https://maps.googleapis.com/"

    var currentResult:Result? = null

    val googleApiService: IGoogleAPIService
            get()= RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)

    val googleApiServiceScalars:IGoogleAPIService
        get()= RetrofitScalarsClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}