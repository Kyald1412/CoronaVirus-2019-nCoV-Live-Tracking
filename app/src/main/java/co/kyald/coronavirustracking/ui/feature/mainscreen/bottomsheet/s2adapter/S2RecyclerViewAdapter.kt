package co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s2adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaEntity
import kotlinx.android.synthetic.main.item_country_second.view.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class S2RecyclerViewAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<S2RecyclerViewAdapter.CardViewViewHolder>(), Filterable {

    private var coronaS2Data: MutableList<S2CoronaEntity> = mutableListOf()

    var coronaS2DataFilterList: MutableList<S2CoronaEntity> = mutableListOf()

    init {
        coronaS2DataFilterList = coronaS2Data
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {
                    coronaS2DataFilterList = coronaS2Data
                } else {
                    val resultList: MutableList<S2CoronaEntity> = mutableListOf()
                    for (row in coronaS2Data) {
                        if (row.country?.toLowerCase(Locale.ROOT)!!.contains(
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
                    coronaS2DataFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = coronaS2DataFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                coronaS2DataFilterList = results?.values as MutableList<S2CoronaEntity>
                notifyDataSetChanged()
            }

        }
    }

    fun setEntity(entryList: List<S2CoronaEntity>) {
        coronaS2Data.clear()
        coronaS2Data.addAll(entryList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CardViewViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_country_second, viewGroup, false)
        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(coronaS2DataFilterList[position])
    }

    override fun getItemCount(): Int = coronaS2DataFilterList.size

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(entry: S2CoronaEntity) {
            try {
                itemView.tvTitle.text = "${entry.country} ${
                
                if(!entry.province.isNullOrEmpty()) {
                    "("+entry.province+")"
                } else {
                    ""
                }
                }"
                itemView.tvCases.text = entry.stats.confirmed.toString()
                itemView.tvDeathCase.text = entry.stats.deaths.toString()
                itemView.tvRecoverCase.text = entry.stats.recovered.toString()
            } catch (e: Exception) {

            }
        }
    }
}
