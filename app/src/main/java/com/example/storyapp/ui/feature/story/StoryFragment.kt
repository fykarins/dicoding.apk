package com.example.storyapp.ui.feature.story

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.data.user.UserPreferences
import com.example.storyapp.databinding.FragmentStoryBinding
import com.example.storyapp.ui.adapter.LoadingAdapter
import com.example.storyapp.ui.adapter.StoryPaging
import com.example.storyapp.ui.feature.factory.StoryViewModelFactory
import com.example.storyapp.ui.feature.newstory.NewStoryActivity
import com.example.storyapp.utils.ShowToast

class StoryFragment : Fragment() {

    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var storyAdapter: StoryPaging
    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!

    private val addStoryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            storyAdapter.refresh()
            storyAdapter.addLoadStateListener { loadState ->
                if (loadState.source.refresh is LoadState.NotLoading) {
                    binding.rvStory.smoothScrollToPosition(0)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()


        binding.fab.setOnClickListener {
            val intent = Intent(requireContext(), NewStoryActivity::class.java)
            addStoryLauncher.launch(intent)
        }


    }

    private fun setupRecyclerView() {
        binding.rvStory.layoutManager = LinearLayoutManager(requireContext())
        storyAdapter = StoryPaging()
        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingAdapter {
                storyAdapter.retry()
            }
        )
        storyAdapter.addLoadStateListener { loadState ->
            binding.progressBar.visibility =
                if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

            binding.imgEmpty.visibility =
                if (loadState.source.refresh is LoadState.NotLoading && storyAdapter.itemCount == 0) View.VISIBLE else View.GONE


            val errorState = loadState.source.refresh as? LoadState.Error
                ?: loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error

            errorState?.let {
                it.error.localizedMessage?.let { it1 -> ShowToast(requireContext(), it1) }
            }
        }


        observeViewModel()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        val token = UserPreferences(requireContext()).getToken()
        storyViewModel.stories(token = token!!)
            .observe(viewLifecycleOwner) { pagingData ->
                storyAdapter.submitData(lifecycle, pagingData)
            }
    }
}