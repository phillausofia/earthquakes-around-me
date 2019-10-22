package com.earthquakesaroundme.overview


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.earthquakesaroundme.R
import com.earthquakesaroundme.Utils
import com.earthquakesaroundme.databinding.ListItemViewBinding
import com.earthquakesaroundme.network.Model.Earthquake
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.ad_item.view.*
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
                else -> {
                    val earthquakes: MutableList<DataItem> = list.map {DataItem.EarthquakeItem(it)}.toMutableList()
                        var adPositionInList = if (earthquakes.size >= 3) 3 else earthquakes.size
                        while (adPositionInList <= earthquakes.size) {
                            earthquakes.add(adPositionInList, DataItem.AdItem(adPositionInList))
                            adPositionInList += 11
                        }
                    earthquakes
                }
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
            ITEM_VIEW_TYPE_AD_ITEM -> AdViewHolder.from(parent)
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
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.EarthquakeItem -> ITEM_VIEW_TYPE_EARTHQUAKE_ITEM
            is DataItem.ProgressItem -> ITEM_VIEW_TYPE_PROGRESS_ITEM
            is DataItem.AdItem -> ITEM_VIEW_TYPE_AD_ITEM
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

    class AdViewHolder(view: View): RecyclerView.ViewHolder(view) {

        companion object {

            fun from(parent: ViewGroup): AdViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.ad_item, parent, false)

                val adView = AdView(parent.context)
                view.linear_layout_ad_item.addView(adView)
                adView.adUnitId = Utils.ADAPTIVE_BANNER_AD_UNIT_ID
                adView.adSize = Utils.adSize
                val adRequest = AdRequest
                    .Builder()
                    .addTestDevice("5D768934B1FE279BA20FDDAAE2951F1F").build()
                adView.loadAd(adRequest)

                return AdViewHolder(view)
            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
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

    data class AdItem(val positionInList: Int) : DataItem() {
        override val id = positionInList.toLong()
    }

    object ProgressItem : DataItem() {
        override val id = Long.MIN_VALUE
    }


    abstract val id: Long
}