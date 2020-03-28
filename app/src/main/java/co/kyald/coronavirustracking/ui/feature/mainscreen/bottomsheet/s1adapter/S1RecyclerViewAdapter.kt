package co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s1adapter

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
import kotlinx.android.synthetic.main.item_country_first.view.*
import java.util.*

class S1RecyclerViewAdapter(
    private val context: Context) :
    RecyclerView.Adapter<S1RecyclerViewAdapter.CardViewViewHolder>(), Filterable {

    private var mData: MutableList<S1CoronaEntity.Entry> = mutableListOf()


    var coronaS1DataFilterList: MutableList<S1CoronaEntity.Entry> = mutableListOf()

    init {
        coronaS1DataFilterList = mData
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {
                    coronaS1DataFilterList = mData
                } else {
                    val resultList: MutableList<S1CoronaEntity.Entry> = mutableListOf()
                    for (row in mData) {
                        if (row.gsxcountry.t?.toLowerCase(Locale.ROOT)!!.contains(
                                charSearch.toLowerCase(
                                    Locale.ROOT
                                )
                            )
                        ) {
                            resultList.add(row)
                        }
                    }
                    coronaS1DataFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = coronaS1DataFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                coronaS1DataFilterList = results?.values as MutableList<S1CoronaEntity.Entry>
                notifyDataSetChanged()
            }

        }
    }


    fun setEntity(entryList: List<S1CoronaEntity.Entry>) {
        mData.clear()
        mData.addAll(entryList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CardViewViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_country_first, viewGroup, false)
        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(coronaS1DataFilterList[position])
    }

    override fun getItemCount(): Int = coronaS1DataFilterList.size

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(entry: S1CoronaEntity.Entry) {
            itemView.tvTitle.text = entry.gsxcountry.t
            itemView.tvCases.text = entry.gsxconfirmedcases.t
            itemView.tvDeathCase.text = if(entry.gsxreporteddeaths.t == "" || entry.gsxreporteddeaths.t == "0") "-" else entry.gsxreporteddeaths.t
        }
    }
}
