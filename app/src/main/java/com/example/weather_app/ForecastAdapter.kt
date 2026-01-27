package com.example.weather_app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.R
import com.example.weather_app.network.ForecastPeriod

class ForecastAdapter :
    ListAdapter<ForecastPeriod, ForecastAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<ForecastPeriod>() {
        override fun areItemsTheSame(oldItem: ForecastPeriod, newItem: ForecastPeriod): Boolean {
            // NOAA periods have startTime; good stable key for a simple app
            return oldItem.startTime == newItem.startTime && oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ForecastPeriod, newItem: ForecastPeriod): Boolean {
            return oldItem == newItem
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle)
        val details: TextView = itemView.findViewById(R.id.details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_forecast, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)
        holder.title.text = p.name
        holder.subtitle.text = "${p.temperature}${p.temperatureUnit} — ${p.shortForecast}"
        holder.details.text = p.detailedForecast.orEmpty()
    }
}
