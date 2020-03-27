package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.*
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import kotlinx.android.synthetic.main.fragment_select_location.*
import org.koin.android.ext.android.inject


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private val GPS_SETTINGS_REQUEST_ID = 30303
    private var selectedMarker: Marker? = null
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val findFragmentById = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        findFragmentById.getMapAsync(this)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            googleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            googleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.googleMap = map
        styleMap(map)
        selectLocationListener(map)
        activity?.checkLocationPermission({
            activity?.checkGPS { isEnabled ->
                if (isEnabled) {
                    onGPSEnabled(map)
                } else {
                    activity?.enableGPS(GPS_SETTINGS_REQUEST_ID, {
                        onGPSEnabled(map)
                    })
                }
            }
        }, {
            //Permission Denied
        })
    }

    private fun styleMap(map: GoogleMap?) {
        map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style))

    }

    private fun selectLocationListener(map: GoogleMap?) {
        map?.setOnMapClickListener { latLng ->
            selectPositionOnMap(latLng, map)
        }
    }

    private fun selectPositionOnMap(
        latLng: LatLng,
        map: GoogleMap?
    ) {
        selectedMarker?.remove()
        selectedMarker?.position = latLng
        map?.addMarker(MarkerOptions().position(latLng)).also {
            selectedMarker = it
        }
        map?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        updateViewModelSelectedPosition(latLng)
    }

    private fun updateViewModelSelectedPosition(latLng: LatLng) {
        _viewModel.latitude.postValue(latLng.latitude)
        _viewModel.longitude.postValue(latLng.longitude)
    }

    private fun onGPSEnabled(map: GoogleMap?) {
        retrieveLocation({
            selectPositionOnMap(LatLng(it.latitude, it.longitude), map)
        }, {

        })
    }


}
