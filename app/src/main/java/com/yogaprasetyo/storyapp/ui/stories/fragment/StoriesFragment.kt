package com.yogaprasetyo.storyapp.ui.stories.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.data.local.UserPreferences
import com.yogaprasetyo.storyapp.databinding.FragmentStoriesBinding
import com.yogaprasetyo.storyapp.model.UserViewModel
import com.yogaprasetyo.storyapp.model.ViewModelFactory
import com.yogaprasetyo.storyapp.ui.WelcomeActivity
import com.yogaprasetyo.storyapp.ui.stories.adapter.LoadingStateAdapter
import com.yogaprasetyo.storyapp.ui.stories.adapter.StoryAdapter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class StoriesFragment : Fragment() {

    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding

    private lateinit var preference: UserPreferences
    private val viewModel: UserViewModel by viewModels { ViewModelFactory.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Using options menu
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStoriesBinding.inflate(layoutInflater, container, false)

        // Add FAB action to create new story
        binding?.fbAddStory?.setOnClickListener {
            findNavController().navigate(R.id.action_storiesFragment_to_newStoryActivity)
        }

        // Add FAB action to show location user
        binding?.fbLocationStory?.setOnClickListener {
            findNavController().navigate(R.id.action_storiesFragment_to_userStoryLocationActivity)
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preference = UserPreferences.getInstance(requireContext().dataStore)
        val storyAdapter = StoryAdapter()

        /**
         * Observe datastore on local storage to retrieve token
         * */
        viewModel.loadPreferences(preference).observe(viewLifecycleOwner) { pref ->

            viewModel.loadAllStories(pref.token).observe(viewLifecycleOwner) { pagingData ->
                storyAdapter.submitData(lifecycle, pagingData)
            }
        }

        storyAdapter.addLoadStateListener { loadState ->
            val isError =
                loadState.refresh is LoadState.Error && loadState.source.refresh is LoadState.Error
            val isLoading =
                loadState.refresh is LoadState.Loading && loadState.source.refresh is LoadState.Loading

            binding?.ivEmptyStory?.isVisible = isError
            binding?.tvEmpty?.isVisible = isError
            binding?.progressBar?.isVisible = isLoading
        }

        binding?.rvStories?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /**
             * Remove token and state login on datastore
             * then move to activity welcome and clear stack activity
             * */
            R.id.logout -> {
                viewModel.logout(preference)
                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                requireActivity().startActivity(intent)
                requireActivity().finish()
                true
            }

            /**
             * Launch intent to global setting language
             * */
            R.id.changeLanguage -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}