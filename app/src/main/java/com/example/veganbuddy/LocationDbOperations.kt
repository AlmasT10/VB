package com.example.veganbuddy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm

class LocationDbOperations: ViewModel() {
    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    init {
        val realm = Realm.getDefaultInstance()
        val location = realm.where(Location::class.java).findFirst()
        _location.postValue(location)
    }

    fun onSubmit(latitude:Double,longitude:Double){
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        var l: Location? = realm.where(Location::class.java).equalTo("latitude",latitude).findFirst()
        if(l?.isLoaded() == true){
            l.latitude = latitude
            l.longitude = longitude
            realm.insertOrUpdate(l)

        }else{
            var newLocation: Location = realm.createObject(Location::class.java)
            newLocation.latitude = latitude
            newLocation.longitude = longitude
            realm.insertOrUpdate(newLocation)
        }

        realm.commitTransaction()
        realm.close()
//
    }
}