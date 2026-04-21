package com.example.truckmate.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.truckmate.data.model.LocationObject
import com.example.truckmate.data.model.ObjectType
import com.example.truckmate.data.repository.ObjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ObjectViewModel : ViewModel() {
    private val repository = ObjectRepository()

    private val _objects = MutableStateFlow<List<LocationObject>>(emptyList())
    val objects: StateFlow<List<LocationObject>> = _objects

    var selectedObject by mutableStateOf<LocationObject?>(null)
        private set

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

    fun selectObject(obj: LocationObject) {
        selectedObject = obj
    }
}