package com.example.storyapp.ui.feature.story

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.storyapp.R
import com.example.storyapp.data.remote.ListStoryItem
import com.example.storyapp.data.story.StoryResult
import com.example.storyapp.data.user.UserPreferences
import com.example.storyapp.databinding.FragmentStoryMapBinding
import com.example.storyapp.ui.feature.factory.StoryMapViewModelFactory
import com.example.storyapp.ui.feature.newstory.NewStoryActivity
import com.example.storyapp.utils.toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class StoryMapFragment : Fragment(), OnMapReadyCallback {

    private val storyMapViewModel: StoryMapViewModel by viewModels {
        StoryMapViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var mMap: GoogleMap

    private var _binding: FragmentStoryMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStoryMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupObservers()

        binding.fab.setOnClickListener {
            val intent = Intent(requireContext(), NewStoryActivity::class.java)
            startActivity(intent)
        }

        val token = UserPreferences(requireContext()).getToken()
        token?.let {
            storyMapViewModel.getAllStoriesWithMap(it)
        } ?: run {
            toast(requireContext(), "Token not available")
        }
    }

    private fun setupObservers() {
        storyMapViewModel.stories.observe(viewLifecycleOwner) { storyResult ->
            when (storyResult) {
                is StoryResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is StoryResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val stories = storyResult.data
                    if (stories.isNotEmpty()) {
                        addManyMarker(stories)
                    } else {
                        Toast.makeText(context, "No story available", Toast.LENGTH_SHORT).show()
                    }
                }

                is StoryResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    toast(requireContext(), storyResult.error)
                }
            }
        }

        storyMapViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun addManyMarker(stories: List<ListStoryItem>) {
        if (::mMap.isInitialized) {
            val boundsBuilder = LatLngBounds.Builder()

            stories.forEach { story ->
                val lat = story.lat
                val lon = story.lon
                if (lat != null && lon != null) {
                    val latLng = LatLng(lat as Double, lon as Double)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(story.name)
                            .snippet(story.description)
                    )
                    boundsBuilder.include(latLng)
                }
            }

            val bounds = boundsBuilder.build()
            setMapStyle()
            val padding = 100
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        } else {
            Toast.makeText(context, "Map cannot appear", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style
                    )
                )
            if (!success) {
                Log.e("StoryMapFragment", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("StoryMapFragment", "Can't find style. Error: ", exception)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}