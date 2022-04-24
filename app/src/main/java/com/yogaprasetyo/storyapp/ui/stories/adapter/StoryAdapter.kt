package com.yogaprasetyo.storyapp.ui.stories.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.data.remote.response.ListStoryItem
import com.yogaprasetyo.storyapp.databinding.ItemStoryBinding
import com.yogaprasetyo.storyapp.ui.stories.DetailStoryActivity
import com.yogaprasetyo.storyapp.util.loadImage
import com.yogaprasetyo.storyapp.util.withDateFormat

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(mDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class StoryViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Setup data to the UI item recycler view
         * */
        fun bind(storyItem: ListStoryItem) {
            val (photoUrl, createdAt, name, description, _, _, _) = storyItem
            binding.apply {
                itemStoryName.text = name
                itemStoryDescription.text = description
                itemStoryImage.loadImage(itemStoryImage.context, photoUrl)
                itemStoryCreated.text =
                    itemView.resources.getString(R.string.created_at, createdAt.withDateFormat())

                cardItemStory.setOnClickListener {
                    // Setup transition animation using shared element
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(itemStoryImage, "photo"),
                            Pair(itemStoryCreated, "date"),
                            Pair(itemStoryName, "name"),
                            Pair(itemStoryDescription, "description")
                        )
                    val extras = ActivityNavigatorExtras(optionsCompat)
                    val bundle = bundleOf(DetailStoryActivity.EXTRA_STORY to storyItem)

                    // Move to detail activity
                    it.findNavController().navigate(
                        R.id.action_storiesFragment_to_detailStoryActivity,
                        args = bundle,
                        navOptions = null,
                        navigatorExtras = extras
                    )
                }
            }
        }
    }

    /**
     * Anonymous object for determine is list changing or not
     * */
    companion object {
        val mDiffCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}