package com.example.veganbuddy


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm

class UserDbOperations :ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        val realm = Realm.getDefaultInstance()
        val user = realm.where(User::class.java).findFirst()
        _user.postValue(user)
    }

    fun onSubmit(firstName:String,lastName:String){
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        var u: User? = realm.where(User::class.java).equalTo("firstName",firstName).findFirst()
        if(u?.isLoaded() == true){
            u.firstName = firstName
            u.lastName = lastName
            realm.insertOrUpdate(u)

        }else{
            var currentUser: User = realm.createObject(User::class.java)
            currentUser.firstName = firstName
            currentUser.lastName = lastName
            realm.insertOrUpdate(currentUser)
        }

        realm.commitTransaction()
        realm.close()
//
    }

}