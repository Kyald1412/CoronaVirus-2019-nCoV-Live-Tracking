package co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s3adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntity
import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntityResponse
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaEntity
import kotlinx.android.synthetic.main.item_country_second.view.*
import timber.log.Timber
import java.util.*

class S3RecyclerViewAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<S3RecyclerViewAdapter.CardViewViewHolder>(), Filterable {

    private var coronaS3Data: MutableList<S3CoronaEntity> = mutableListOf()

    var coronaS3DataFilterList: MutableList<S3CoronaEntity> = mutableListOf()

    init {
        coronaS3DataFilterList = coronaS3Data
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {
                    coronaS3DataFilterList = coronaS3Data
                } else {
                    val resultList: MutableList<S3CoronaEntity> = mutableListOf()
                    for (row in coronaS3Data) {
                        if (row.attributes.countryRegion?.toLowerCase(Locale.ROOT)!!.contains(
                                charSearch.toLowerCase(
                                    Locale.ROOT
                                )
                            )) {
                            resultList.add(row)
                        }
                    }
                    Timber.e("NEWSTESXT ADAPTER ${charSearch.toLowerCase(
                        Locale.ROOT
                    )}")

                    Timber.e("FILTERED LIST ${resultList}")
                    coronaS3DataFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = coronaS3DataFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                coronaS3DataFilterList = results?.values as MutableList<S3CoronaEntity>
                notifyDataSetChanged()
            }

        }
    }

    fun setEntity(entryList: List<S3CoronaEntity>) {
        coronaS3Data.clear()
        coronaS3Data.addAll(entryList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CardViewViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_country_second, viewGroup, false)
        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(coronaS3DataFilterList[position])
    }

    override fun getItemCount(): Int = coronaS3DataFilterList.size

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(entry: S3CoronaEntity) {
            try {
                itemView.tvTitle.text = entry.attributes.combinedKey
                itemView.tvCases.text = entry.attributes.confirmed.toString()
                itemView.tvDeathCase.text = entry.attributes.deaths.toString()
                itemView.tvRecoverCase.text = entry.attributes.recovered.toString()
            } catch (e: Exception) {

            }
        }
    }
}
