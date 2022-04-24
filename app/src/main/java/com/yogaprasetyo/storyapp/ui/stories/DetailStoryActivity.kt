package com.yogaprasetyo.storyapp.ui.stories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.navArgs
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.data.local.UserPreferences
import com.yogaprasetyo.storyapp.databinding.ActivityDetailStoryBinding
import com.yogaprasetyo.storyapp.model.UserViewModel
import com.yogaprasetyo.storyapp.model.ViewModelFactory
import com.yogaprasetyo.storyapp.ui.WelcomeActivity
import com.yogaprasetyo.storyapp.util.loadImage
import com.yogaprasetyo.storyapp.util.withDateFormat

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var preference: UserPreferences

    private val viewModel: UserViewModel by viewModels { ViewModelFactory.getInstance() }
    private val args: DetailStoryActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = UserPreferences.getInstance(dataStore)

        val receiveIntent = args.extraStory
        val (photoUrl, createdAt, name, description, _, _, _) = receiveIntent

        binding.apply {
            ivStory.loadImage(this@DetailStoryActivity, photoUrl)
            tvStoryCreated.text = getString(R.string.created_at, createdAt.withDateFormat())
            tvStoryName.text = name
            tvStoryDescription.text = description
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /**
             * Remove token and state login on datastore
             * then move to activity welcome and clear stack activity
             * */
            R.id.logout -> {
                viewModel.logout(preference)
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
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

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}