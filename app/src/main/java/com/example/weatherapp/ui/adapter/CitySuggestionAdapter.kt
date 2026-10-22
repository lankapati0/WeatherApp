package com.example.weatherapp.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.data.GeocodingResult
import com.example.weatherapp.databinding.ItemCitySuggestionBinding

class CitySuggestionAdapter(
    private val onCityClick:(GeocodingResult) -> Unit
): RecyclerView.Adapter<CitySuggestionAdapter.ViewHolder>() {

    private val cities = mutableListOf<GeocodingResult>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CitySuggestionAdapter.ViewHolder {
        val binding = ItemCitySuggestionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding, onCityClick)
    }

    override fun onBindViewHolder(holder: CitySuggestionAdapter.ViewHolder, position: Int) {

        holder.bind(cities[position])
    }

    override fun getItemCount(): Int = cities.size

    fun submitCities(newCities: List<GeocodingResult>){

        cities.clear()
        cities.addAll(newCities)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemCitySuggestionBinding, private val onCityClick: (GeocodingResult) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(city: GeocodingResult){
            binding.tvCitySuggestion.text = city.name
            binding.tvCityCountry.text = city.country
            binding.root.setOnClickListener {
                onCityClick(city)
            }
        }
    }
}