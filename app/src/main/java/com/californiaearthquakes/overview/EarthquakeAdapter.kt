package com.californiaearthquakes.overview


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.californiaearthquakes.databinding.ListItemViewBinding
import com.californiaearthquakes.network.Model
import com.californiaearthquakes.network.Model.Earthquake
import kotlinx.android.synthetic.main.fragment_overview.view.*

class EarthquakeAdapter(val onClickListener: OnClickListener) : ListAdapter<Earthquake, EarthquakeAdapter.EarthquakeViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeViewHolder {

        return EarthquakeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: EarthquakeViewHolder, position: Int) {
        val earthquake = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(earthquake)
        }
        holder.bin(earthquake)
    }

    class EarthquakeViewHolder(private val binding: ListItemViewBinding): RecyclerView.ViewHolder(binding.root) {

        fun bin(earthquake: Earthquake) {
            binding.earthquake = earthquake
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : EarthquakeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemViewBinding.inflate(layoutInflater, parent, false)

                return EarthquakeViewHolder(binding)
            }
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<Earthquake>() {
        override fun areItemsTheSame(oldItem: Earthquake, newItem: Earthquake): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Earthquake, newItem: Earthquake): Boolean {
            return oldItem.time == newItem.time
        }

    }

    class OnClickListener(val clickListener: (earthquake: Earthquake) -> Unit) {
        fun onClick(earthquake: Earthquake) = clickListener(earthquake)
    }


}