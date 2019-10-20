package com.earthquakesaroundme.overview


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.earthquakesaroundme.R
import com.earthquakesaroundme.databinding.ListItemViewBinding
import com.earthquakesaroundme.network.Model.Earthquake
import kotlinx.android.synthetic.main.progress_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private val ITEM_VIEW_TYPE_AD_ITEM = 2
private val ITEM_VIEW_TYPE_EARTHQUAKE_ITEM = 1
private val ITEM_VIEW_TYPE_PROGRESS_ITEM = 0

class EarthquakeAdapter(val onClickListener: OnClickListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private var items: MutableList<DataItem>? = null

    fun addDataAndSubmitList(list: List<Earthquake>?) {
        adapterScope.launch {
            items = when (list) {
                null -> null
                else -> list.map {DataItem.EarthquakeItem(it)}.toMutableList()
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }

    }

    fun insertProgressView() {
        items!!.add(DataItem.ProgressItem)
        notifyItemInserted(items!!.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {
            ITEM_VIEW_TYPE_EARTHQUAKE_ITEM -> EarthquakeViewHolder.from(parent)
            ITEM_VIEW_TYPE_PROGRESS_ITEM -> ProgressViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EarthquakeViewHolder -> {
                val earthquakeItem = getItem(position) as DataItem.EarthquakeItem
                holder.itemView.setOnClickListener {
                    onClickListener.onClick(earthquakeItem.earthquake)
                }
                holder.bin(earthquakeItem.earthquake)
            }
        }
//        val earthquake = getItem(position)
//        holder.itemView.setOnClickListener {
//            onClickListener.onClick(earthquake)
//        }
//        holder.bin(earthquake)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.EarthquakeItem -> ITEM_VIEW_TYPE_EARTHQUAKE_ITEM
            is DataItem.ProgressItem -> ITEM_VIEW_TYPE_PROGRESS_ITEM
        }
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

    class ProgressViewHolder(view: View): RecyclerView.ViewHolder(view) {

        companion object {

            fun from(parent: ViewGroup) : ProgressViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.progress_item, parent, false)
                return ProgressViewHolder(view)
            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
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