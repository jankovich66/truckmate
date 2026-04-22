package com.example.truckmate.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.truckmate.data.model.LocationObject
import com.example.truckmate.data.model.ObjectType
import com.example.truckmate.data.repository.ObjectRepository
import com.google.android.gms.maps.model.BitmapDescriptor
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ObjectViewModel : ViewModel() {
    private val repository = ObjectRepository()

    private val _objects = MutableStateFlow<List<LocationObject>>(emptyList())
    val objects: StateFlow<List<LocationObject>> = _objects

    var selectedObject by mutableStateOf<LocationObject?>(null)
        private set

    var markerIcons by mutableStateOf<Map<ObjectType, BitmapDescriptor>>(emptyMap())
        private set

    private val _selectedType = MutableStateFlow<ObjectType?>(null)
    val selectedType: StateFlow<ObjectType?> = _selectedType

    private val _radiusFilter = MutableStateFlow<Float?>(null)
    val radiusFilter: StateFlow<Float?> = _radiusFilter

    init {
        repository.getObjectsRealtime {
            _objects.value = it
        }
    }

    fun addObject(title: String, description: String, type: ObjectType, latitude: Double, longitude: Double) {
        val objectItem = LocationObject(
            title = title,
            description = description,
            type = type,
            latitude = latitude,
            longitude = longitude
        )

        viewModelScope.launch {
            repository.addObject(objectItem)
        }
    }

    fun getObjectById(id: String): LocationObject? {
        return objects.value.find { it.id == id }
    }

    fun selectObject(obj: LocationObject?) {
        selectedObject = obj
    }

    fun loadIcons(context: android.content.Context) {
        if(markerIcons.isNotEmpty()) return

        val resources = context.resources
        val density = resources.displayMetrics.density
        val size = (32 * density).toInt()

        fun makeIcon(resId: Int): BitmapDescriptor {
            val bitmap = BitmapFactory.decodeResource(resources, resId)
            val scaled = Bitmap.createScaledBitmap(bitmap, size, size, true)
            return BitmapDescriptorFactory.fromBitmap(scaled)
        }

        markerIcons = mapOf(
            ObjectType.PARKING to makeIcon(com.example.truckmate.R.drawable.parking),
            ObjectType.RESTAURANT to makeIcon(com.example.truckmate.R.drawable.restaurant),
            ObjectType.GAS_STATION to makeIcon(com.example.truckmate.R.drawable.gas_station),
            ObjectType.SERVICE to makeIcon(com.example.truckmate.R.drawable.service),
            ObjectType.POLICE_PATROL to makeIcon(com.example.truckmate.R.drawable.police),
            ObjectType.ROADWORKS to makeIcon(com.example.truckmate.R.drawable.roadworks),
            ObjectType.RESTRICTION to makeIcon(com.example.truckmate.R.drawable.restriction),
            ObjectType.REST_AREA to makeIcon(com.example.truckmate.R.drawable.rest_area)
        )
    }

    fun setFilter(type: ObjectType?) {
        _selectedType.value = type
    }

    fun setRadiusFilter(radius: Float?) {
        _radiusFilter.value = radius
    }
}