package com.example.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.ForecastCardItem
import okhttp3.internal.notifyAll

class ForecastCardAdapter(private val layoutId:Int =
    R.layout.item_hourly_forecast): RecyclerView.Adapter<ForecastCardAdapter.ForecastViewHolder>(){

    private val items = mutableListOf<ForecastCardItem>()

    fun submitItems(newItems: List<ForecastCardItem>){
       items.clear()
       items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForecastCardAdapter.ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId,parent,false)

        return ForecastViewHolder(view, layoutId)
    }

    override fun onBindViewHolder(holder: ForecastCardAdapter.ForecastViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ForecastViewHolder(
        itemView: View,
        private val layoutId: Int
    ) : RecyclerView.ViewHolder(itemView) {

        private val label: TextView = itemView.findViewById(R.id.tvForecastTime)

        private val icon: TextView = itemView.findViewById(R.id.tvForecastIcon)

        private val detail: TextView = itemView.findViewById(R.id.tvRainChance)

        private val temperature: TextView = itemView.findViewById(R.id.tvForecastTemperature)

        private val tvForecastTime =
            itemView.findViewById<TextView>(R.id.tvForecastTime)

        private val tvForecastIcon =
            itemView.findViewById<TextView>(R.id.tvForecastIcon)

        private val tvForecastTemperature =
            itemView.findViewById<TextView>(R.id.tvForecastTemperature)

        private val tvForecastDate =
            if (layoutId == R.layout.item_daily_forecast)
                itemView.findViewById<TextView>(R.id.tvForecastDate)
            else null

        private val tvForecastDescription =
            if (layoutId == R.layout.item_daily_forecast)
                itemView.findViewById<TextView>(R.id.tvForecastDescription)
            else null

        private val tvRainChance =
            if (layoutId == R.layout.item_daily_forecast)
                itemView.findViewById<TextView>(R.id.tvRainChance)
            else null

        private val tvForecastArrow =
            if (layoutId == R.layout.item_daily_forecast)
                itemView.findViewById<TextView>(R.id.tvForecastArrow)
            else null

        fun bind(item: ForecastCardItem){
            label.text = item.label
            icon.text = item.icon
            detail?.text = item.detail
            temperature.text = item.temperature
            tvForecastDate?.text = item.detail

        }
    }
}