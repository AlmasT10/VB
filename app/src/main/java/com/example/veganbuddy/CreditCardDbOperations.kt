package com.example.veganbuddy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import java.util.*

class CreditCardDbOperations: ViewModel() {
    private val _creditCard = MutableLiveData<CreditCard?>()
    val creditCard: LiveData<CreditCard?> = _creditCard

    init {
        val realm = Realm.getDefaultInstance()
        val creditCard = realm.where(CreditCard::class.java).findFirst()
        _creditCard.postValue(creditCard)
    }

    fun onSubmit(creditCardNumber:Long, expiryDate: String){
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        var c: CreditCard? = realm.where(CreditCard::class.java).equalTo("cardNumber",creditCardNumber).findFirst()
        if(c?.isLoaded() == true){
            c.cardNumber = creditCardNumber
            c.expiryDate = expiryDate
            realm.insertOrUpdate(c)

        }else{
            var newCard: CreditCard = realm.createObject(CreditCard::class.java)
            newCard.cardNumber = creditCardNumber
            newCard.expiryDate = expiryDate
            realm.insertOrUpdate(newCard)
        }

        realm.commitTransaction()
        realm.close()
//
    }
}