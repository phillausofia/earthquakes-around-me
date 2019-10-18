package com.earthquakesaroundme.overview


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.earthquakesaroundme.databinding.ListItemViewBinding
import com.earthquakesaroundme.network.Model.Earthquake


private val ITEM_VIEW_TYPE_EARTHQUAKE_ITEM = 1
private val ITEM_VIEW_TYPE_PROGRESS_ITEM = 0

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
            return oldItem.properties.time == newItem.properties.time
        }

    }

    class OnClickListener(val clickListener: (earthquake: Earthquake) -> Unit) {
        fun onClick(earthquake: Earthquake) = clickListener(earthquake)
    }

}

sealed class DataItem {

    data class EarthquakeItem(val earthquake: Earthquake) : DataItem() {
        override val id = earthquake.properties.time
    }

    object ProgressItem : DataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}