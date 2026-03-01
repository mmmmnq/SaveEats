package com.example.saveeats.ui.map

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView

class MapComponents {
    var userLocationLayer: UserLocationLayer? = null
    var searchCircle: CircleMapObject? = null
    var userLocationPoint: Point? = null
}

private fun drawOrUpdateCircle(
    point: Point,
    radiusKm: Float,
    mapView: MapView,
    mapComponents: MapComponents
) {
    val radiusMeters = radiusKm * 1000f
    if (mapComponents.searchCircle == null) {
        mapComponents.searchCircle = mapView.mapWindow.map.mapObjects
            .addCircle(Circle(point, radiusMeters))
            .apply {
                strokeColor = Color(0xFFE5B02E).copy(alpha = 0.8f).toArgb()
                strokeWidth = 2f
                fillColor = Color(0xFFE5B02E).copy(alpha = 0.3f).toArgb()
            }
    } else {
        mapComponents.searchCircle?.geometry = Circle(point, radiusMeters)
    }
}

@Composable
fun MapFilterScreen(
    viewModel: MapFilterViewModel = viewModel(),
    onClose: () -> Unit,
    onApplySettings: (radiusKm: Int, userLat: Double, userLon: Double) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val searchRadius by viewModel.searchRadius.collectAsState()

    val currentRadius by rememberUpdatedState(searchRadius)

    val mapView = remember { MapView(context) }
    val mapComponents = remember { MapComponents() }

    // Храним locationManager чтобы потом убрать listener
    val locationManager = remember {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    val locationListener = remember {
        object : android.location.LocationListener {
            override fun onLocationChanged(location: android.location.Location) {
                val point = Point(location.latitude, location.longitude)
                android.util.Log.d("MAP_DEBUG", "LocationManager: lat=${point.latitude}, lon=${point.longitude}")

                mapComponents.userLocationPoint = point

                if (mapComponents.searchCircle == null) {
                    mapView.mapWindow.map.move(
                        CameraPosition(point, 14f, 0f, 0f),
                        Animation(Animation.Type.SMOOTH, 1.5f),
                        null
                    )
                }
                drawOrUpdateCircle(point, currentRadius, mapView, mapComponents)
            }

            override fun onProviderDisabled(provider: String) {}

            override fun onProviderEnabled(provider: String) {}

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) {
            mapComponents.userLocationLayer?.isVisible = true
            // 👇 Запускаем LocationManager как только получили разрешение
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L,
                    1f,
                    locationListener
                )
                // Также пробуем NETWORK_PROVIDER — он быстрее даёт первую координату
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000L,
                    1f,
                    locationListener
                )
            } catch (e: SecurityException) {
                android.util.Log.e("MAP_DEBUG", "Нет разрешения: ${e.message}")
            }
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Обновляем круг когда меняется ползунок
    LaunchedEffect(searchRadius) {
        mapComponents.userLocationPoint?.let { point ->
            drawOrUpdateCircle(point, searchRadius, mapView, mapComponents)
        }
    }

    // Убираем listener когда экран закрывается
    DisposableEffect(Unit) {
        onDispose {
            locationManager.removeUpdates(locationListener)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    MapKitFactory.getInstance().onStart()
                    mapView.onStart()
                }
                Lifecycle.Event.ON_STOP -> {
                    mapView.onStop()
                    MapKitFactory.getInstance().onStop()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Выберите зону поиска",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${searchRadius.toInt()} км",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE5B02E)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("1 км", color = Color.Gray, fontSize = 12.sp) // подпись слева
                        Slider(
                            value = searchRadius,
                            onValueChange = { newRadius -> viewModel.updateRadius(newRadius) },
                            valueRange = 1f..10f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFE5B02E),
                                activeTrackColor = Color(0xFFE5B02E)
                            )
                        )
                        Text("10 км", color = Color.Gray, fontSize = 12.sp) // подпись справа
                    }



                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val lat = mapComponents.userLocationPoint?.latitude?: 0.0
                            val lon = mapComponents.userLocationPoint?.longitude?: 0.0
                            onApplySettings(searchRadius.toInt(), lat, lon)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5B02E))
                    ) {
                        Text(
                            "Выбрать",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    mapView.apply {
                        val mapKit = MapKitFactory.getInstance()
                        mapWindow.map.move(
                            CameraPosition(Point(56.9972, 40.9714), 11f, 0f, 0f)
                        )
                        mapComponents.userLocationLayer =
                            mapKit.createUserLocationLayer(mapWindow)
                        mapComponents.userLocationLayer?.isVisible = true
                        mapComponents.userLocationLayer?.setObjectListener(
                            object : UserLocationObjectListener {
                                // Yandex listener оставляем только для синей точки,
                                // круг теперь рисует LocationManager выше
                                override fun onObjectAdded(userLocationView: UserLocationView) {}
                                override fun onObjectRemoved(view: UserLocationView) {}
                                override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}
                            }
                        )
                    }
                },
                update = {
                    mapComponents.userLocationPoint?.let { point ->
                        drawOrUpdateCircle(point, currentRadius, mapView, mapComponents)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            SmallFloatingActionButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = Color.White
            ) {
                Text("X", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}