//package com.example.mobiledictionary
//
//import android.content.Context
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.mobiledictionary.databinding.ActivityFavoritesBinding
//
//class FavoritesActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityFavoritesBinding
//    private lateinit var favoritesAdapter: RecentWordsAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityFavoritesBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val favoritesPref = getSharedPreferences("favorites", Context.MODE_PRIVATE)
//        // Filter keys whose value is true
//        val favorites = favoritesPref.all.filter { it.value == true }.keys.toList()
//
//        favoritesAdapter = RecentWordsAdapter(favorites) {
//            // Optional: implement click behavior for a favorite word if needed
//        }
//
//        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.favoritesRecyclerView.adapter = favoritesAdapter
//    }
//}

package com.example.mobiledictionary

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledictionary.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var favoritesAdapter: RecentWordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val favoritesPref = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        // Filter keys whose value is a Boolean and equals true
        val favorites = favoritesPref.all.filter {
            it.value is Boolean && it.value == true
        }.keys.toList()

        favoritesAdapter = RecentWordsAdapter(favorites) {
            // Optionally, implement behavior when a favorite word is clicked.
        }

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favoritesRecyclerView.adapter = favoritesAdapter
    }
}
