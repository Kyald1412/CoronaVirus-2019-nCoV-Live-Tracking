package co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s4adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.database.model.worldometers.S4CoronaEntity
import kotlinx.android.synthetic.main.item_country_second.view.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class S4RecyclerViewAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<S4RecyclerViewAdapter.CardViewViewHolder>(), Filterable {

    private var coronaS4Data: MutableList<S4CoronaEntity> = mutableListOf()

    var coronaS4DataFilterList: MutableList<S4CoronaEntity> = mutableListOf()

    init {
        coronaS4DataFilterList = coronaS4Data
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {
                    coronaS4DataFilterList = coronaS4Data
                } else {
                    val resultList: MutableList<S4CoronaEntity> = mutableListOf()
                    for (row in coronaS4Data) {
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
                    coronaS4DataFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = coronaS4DataFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                coronaS4DataFilterList = results?.values as MutableList<S4CoronaEntity>
                notifyDataSetChanged()
            }

        }
    }

    fun setEntity(entryList: List<S4CoronaEntity>) {
        coronaS4Data.clear()
        coronaS4Data.addAll(entryList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CardViewViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_country_second, viewGroup, false)
        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(coronaS4DataFilterList[position])
    }

    override fun getItemCount(): Int = coronaS4DataFilterList.size

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(entry: S4CoronaEntity) {
            try {
                itemView.tvTitle.text = "${entry.country} ${
                
                if(!entry.country.isNullOrEmpty()) {
                    "("+entry.country+")"
                } else {
                    ""
                }
                }"
                itemView.tvCases.text = entry.cases.toString()
                itemView.tvDeathCase.text = entry.deaths.toString()
                itemView.tvRecoverCase.text = entry.recovered.toString()
            } catch (e: Exception) {

            }
        }
    }
}
