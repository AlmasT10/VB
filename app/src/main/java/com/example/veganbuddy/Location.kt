package com.example.veganbuddy

import androidx.annotation.DrawableRes
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId

open class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,

    ):RealmObject() {
}