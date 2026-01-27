package com.example.weather_app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.weather_app.ui.theme.Weather_AppTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.network.NoaaApi
import com.example.weather_app.network.NoaaService
import com.example.weather_app.ui.ForecastAdapter
import com.example.weather_app.ui.WeatherViewModel


class MainActivity : ComponentActivity() {
    private val vm: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progress = findViewById<ProgressBar>(R.id.progress)
        val errorText = findViewById<TextView>(R.id.errorText)
        val retryButton = findViewById<Button>(R.id.retryButton)
        val recycler = findViewById<RecyclerView>(R.id.forecastRecycler)

        val adapter = ForecastAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        fun load() {
            vm.load(36.6002, -121.8947) // temp coords
        }

        retryButton.setOnClickListener { load() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.ui.collect { state ->
                    progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    val hasError = state.error != null
                    errorText.visibility = if (hasError) View.VISIBLE else View.GONE
                    retryButton.visibility = if (hasError) View.VISIBLE else View.GONE
                    errorText.text = state.error ?: ""

                    adapter.submitList(state.periods)
                }
            }
        }

        load()
    }
}
