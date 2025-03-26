//
//package com.example.mobiledictionary
//
//import android.content.Context
//import android.content.Intent
//import android.media.MediaPlayer
//import android.net.Uri
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.mobiledictionary.databinding.ActivityMainBinding
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var meaningAdapter: MeaningAdapter
//    private var currentAudioUrl: String? = null
//    private var mediaPlayer: MediaPlayer? = null
//
//    // SharedPreferences for favorites and recent words
//    private val favoritesPref by lazy { getSharedPreferences("favorites", Context.MODE_PRIVATE) }
//    private val recentPref by lazy { getSharedPreferences("recent", Context.MODE_PRIVATE) }
//    private val RECENT_WORDS_KEY = "recentWords"
//    private var recentWordsList: MutableList<String> = mutableListOf()
//    private lateinit var recentWordsAdapter: RecentWordsAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Search button click: save recent word and perform search
//        binding.searchBtn.setOnClickListener {
//            val word = binding.searchInput.text.toString().trim()
//            if (word.isNotEmpty()) {
//                saveRecentWord(word)
//                getMeaning(word)
//            }
//        }
//
//        // Button to open the Favorites page
//        binding.favoritesBtn.setOnClickListener {
//            startActivity(Intent(this, FavoritesActivity::class.java))
//        }
//
//        // Set up Meaning RecyclerView
//        meaningAdapter = MeaningAdapter(emptyList())
//        binding.meaningRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.meaningRecyclerView.adapter = meaningAdapter
//
//        // Set up Recent Words RecyclerView
//        recentWordsAdapter = RecentWordsAdapter(loadRecentWords()) { word ->
//            binding.searchInput.setText(word)
//            getMeaning(word)
//        }
//        binding.recentWordsRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.recentWordsRecyclerView.adapter = recentWordsAdapter
//
//        // Initially show recent words if available
//        updateRecentWordsUI()
//
//        // Toggle favorite button click
//        binding.favoriteBtn.setOnClickListener {
//            val word = binding.wordTextview.text.toString()
//            if (word.isNotEmpty()) {
//                toggleFavorite(word)
//            }
//        }
//
//        // Play audio button click
//        binding.playAudioBtn.setOnClickListener {
//            currentAudioUrl?.let {
//                playAudio(it)
//            } ?: Toast.makeText(this, "No audio available", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun getMeaning(word: String) {
//        setInProgress(true)
//        // Hide recent searches when a search is initiated
//        binding.recentWordsRecyclerView.visibility = View.GONE
//
//        GlobalScope.launch {
//            try {
//                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
//                if (response.body() == null || response.body()?.isEmpty() == true) {
//                    throw Exception("No data found")
//                }
//                runOnUiThread {
//                    setInProgress(false)
//                    response.body()?.first()?.let {
//                        setUI(it)
//                    }
//                }
//            } catch (e: Exception) {
//                runOnUiThread {
//                    setInProgress(false)
//                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
//                        .show()
//                    // Show recent searches if search fails
//                    binding.recentWordsRecyclerView.visibility = View.VISIBLE
//                }
//            }
//        }
//    }
//
//    private fun setUI(response: WordResult) {
//        binding.wordTextview.text = response.word
//
//        // Display phonetic text if available from the first phonetic object that has text
//        val phoneticText = response.phonetics.firstOrNull { !it.text.isNullOrEmpty() }?.text ?: ""
//        binding.phoneticTextview.text = phoneticText
//
//        // Set audio URL if available from the first phonetic object that has audio
//        currentAudioUrl = response.phonetics.firstOrNull { !it.audio.isNullOrEmpty() }?.audio
//
//        // Update favorite toggle button state
//        updateFavoriteToggleText(response.word)
//
//        // Update meanings list
//        meaningAdapter.updateNewData(response.meanings)
//    }
//
//    private fun setInProgress(inProgress: Boolean) {
//        if (inProgress) {
//            binding.searchBtn.visibility = View.INVISIBLE
//            binding.progressBar.visibility = View.VISIBLE
//        } else {
//            binding.searchBtn.visibility = View.VISIBLE
//            binding.progressBar.visibility = View.INVISIBLE
//        }
//    }
//
//    private fun toggleFavorite(word: String) {
//        val isFavorite = favoritesPref.getBoolean(word, false)
//        favoritesPref.edit().putBoolean(word, !isFavorite).apply()
//        updateFavoriteToggleText(word)
//        val message = if (!isFavorite) "$word added to favorites" else "$word removed from favorites"
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
//
//    // Set text to "Add to Favorites" if not favorited, or "Remove from Favorites" if already favorited
//    private fun updateFavoriteToggleText(word: String) {
//        val isFavorite = favoritesPref.getBoolean(word, false)
//        binding.favoriteBtn.text = if (isFavorite) "Remove from Favorites" else "Add to Favorites"
//    }
//
//    // Play audio using MediaPlayer with a proper URI data source
//    private fun playAudio(url: String) {
//        try {
//            mediaPlayer?.release()
//            mediaPlayer = MediaPlayer().apply {
//                setDataSource(this@MainActivity, Uri.parse(url))
//                setOnPreparedListener { start() }
//                prepareAsync()
//            }
//        } catch (e: Exception) {
//            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
//    }
//
//    // Save searched word to recent words (max 10, most recent first)
//    private fun saveRecentWord(word: String) {
//        recentWordsList = loadRecentWords().toMutableList()
//        recentWordsList.remove(word)
//        recentWordsList.add(0, word)
//        if (recentWordsList.size > 10) {
//            recentWordsList = recentWordsList.subList(0, 10)
//        }
//        val joined = recentWordsList.joinToString(",")
//        recentPref.edit().putString(RECENT_WORDS_KEY, joined).apply()
//        updateRecentWordsUI()
//    }
//
//    private fun loadRecentWords(): List<String> {
//        val joined = recentPref.getString(RECENT_WORDS_KEY, "") ?: ""
//        return if (joined.isNotEmpty()) joined.split(",") else listOf()
//    }
//
//    private fun updateRecentWordsUI() {
//        val recent = loadRecentWords()
//        if (recent.isEmpty()) {
//            binding.recentWordsRecyclerView.visibility = View.GONE
//        } else {
//            binding.recentWordsRecyclerView.visibility = View.VISIBLE
//            recentWordsAdapter.updateData(recent)
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer?.release()
//    }
//}
//
//

package com.example.mobiledictionary

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledictionary.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var meaningAdapter: MeaningAdapter
    private var currentAudioUrl: String? = null
    private var mediaPlayer: MediaPlayer? = null

    // SharedPreferences for favorites and recent words
    private val favoritesPref by lazy { getSharedPreferences("favorites", Context.MODE_PRIVATE) }
    private val recentPref by lazy { getSharedPreferences("recent", Context.MODE_PRIVATE) }
    private val RECENT_WORDS_KEY = "recentWords"
    private var recentWordsList: MutableList<String> = mutableListOf()
    private lateinit var recentWordsAdapter: RecentWordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Search button click: save recent word and perform search
        binding.searchBtn.setOnClickListener {
            val word = binding.searchInput.text.toString().trim()
            if (word.isNotEmpty()) {
                saveRecentWord(word)
                getMeaning(word)
            }
        }

        // Button to open the Favorites page
        binding.favoritesBtn.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        // Set up Meaning RecyclerView
        meaningAdapter = MeaningAdapter(emptyList())
        binding.meaningRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.meaningRecyclerView.adapter = meaningAdapter

        // Set up Recent Words RecyclerView
        recentWordsAdapter = RecentWordsAdapter(loadRecentWords()) { word ->
            binding.searchInput.setText(word)
            getMeaning(word)
        }
        binding.recentWordsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recentWordsRecyclerView.adapter = recentWordsAdapter

        // Set up Clear Recent Searches button
        binding.clearRecentBtn.setOnClickListener {
            clearRecentWords()
        }

        // Initially show recent words if available
        updateRecentWordsUI()

        // Toggle favorite button click (for add/remove favorite)
        binding.favoriteBtn.setOnClickListener {
            val word = binding.wordTextview.text.toString() // Corrected reference
            if (word.isNotEmpty()) {
                toggleFavorite(word)
            }
        }

        // Play audio button click
        binding.playAudioBtn.setOnClickListener {
            currentAudioUrl?.let {
                playAudio(it)
            } ?: Toast.makeText(this, "No audio available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMeaning(word: String) {
        setInProgress(true)
        // Hide recent searches when a search is initiated
        binding.recentWordsRecyclerView.visibility = View.GONE
        binding.clearRecentBtn.visibility = View.GONE
        binding.recentLabel.visibility = View.GONE

        GlobalScope.launch {
            try {
                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                if (response.body() == null || response.body()?.isEmpty() == true) {
                    throw Exception("No data found")
                }
                runOnUiThread {
                    setInProgress(false)
                    response.body()?.first()?.let {
                        setUI(it)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    setInProgress(false)
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    // Show recent searches if search fails
                    binding.recentWordsRecyclerView.visibility = View.VISIBLE
                    binding.clearRecentBtn.visibility = View.VISIBLE
                    updateRecentWordsUI()
                }
            }
        }
    }

    private fun setUI(response: WordResult) {
        binding.wordTextview.text = response.word

        // Display phonetic text if available from the first phonetic object that has text
        val phoneticText = response.phonetics.firstOrNull { !it.text.isNullOrEmpty() }?.text ?: ""
        binding.phoneticTextview.text = phoneticText

        // Set audio URL if available from the first phonetic object that has audio
        currentAudioUrl = response.phonetics.firstOrNull { !it.audio.isNullOrEmpty() }?.audio

        // Update favorite toggle button state
        updateFavoriteToggleText(response.word)

        // Update meanings list
        meaningAdapter.updateNewData(response.meanings)
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.searchBtn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.searchBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun toggleFavorite(word: String) {
        val isFavorite = favoritesPref.getBoolean(word, false)
        favoritesPref.edit().putBoolean(word, !isFavorite).apply()
        updateFavoriteToggleText(word)
        val message = if (!isFavorite) "$word added to favorites" else "$word removed from favorites"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Set text to "Add to Favorites" if not favorited, or "Remove from Favorites" if already favorited
    private fun updateFavoriteToggleText(word: String) {
        val isFavorite = favoritesPref.getBoolean(word, false)
        binding.favoriteBtn.text = if (isFavorite) "Remove from Favorites" else "Add to Favorites"
    }

    // Play audio using MediaPlayer with a proper URI data source
    private fun playAudio(url: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@MainActivity, Uri.parse(url))
                setOnPreparedListener { start() }
                prepareAsync()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // Save searched word to recent words (max 10, most recent first)
    private fun saveRecentWord(word: String) {
        recentWordsList = loadRecentWords().toMutableList()
        recentWordsList.remove(word)
        recentWordsList.add(0, word)
        if (recentWordsList.size > 10) {
            recentWordsList = recentWordsList.subList(0, 10)
        }
        val joined = recentWordsList.joinToString(",")
        recentPref.edit().putString(RECENT_WORDS_KEY, joined).apply()
        updateRecentWordsUI()
    }

    private fun loadRecentWords(): List<String> {
        val joined = recentPref.getString(RECENT_WORDS_KEY, "") ?: ""
        return if (joined.isNotEmpty()) joined.split(",") else listOf()
    }

    private fun updateRecentWordsUI() {
        val recent = loadRecentWords()
        if (recent.isEmpty()) {
            binding.recentWordsRecyclerView.visibility = View.GONE
            binding.clearRecentBtn.visibility = View.GONE
            binding.recentLabel.visibility = View.GONE
        } else {
            binding.recentWordsRecyclerView.visibility = View.VISIBLE
            binding.clearRecentBtn.visibility = View.VISIBLE
            binding.recentLabel.visibility = View.VISIBLE
            recentWordsAdapter.updateData(recent)
        }
    }

    // Clear all recent searches from SharedPreferences and update UI
    private fun clearRecentWords() {
        recentPref.edit().remove(RECENT_WORDS_KEY).apply()
        updateRecentWordsUI()
        Toast.makeText(this, "Recent searches cleared", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
