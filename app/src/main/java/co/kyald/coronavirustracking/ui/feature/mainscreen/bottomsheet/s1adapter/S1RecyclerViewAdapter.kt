package co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s1adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import kotlinx.android.synthetic.main.item_country_first.view.*

class S1RecyclerViewAdapter(
    private val context: Context) :
    RecyclerView.Adapter<S1RecyclerViewAdapter.CardViewViewHolder>() {

    private var mData: MutableList<S1CoronaEntity.Entry> = mutableListOf()

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
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(entry: S1CoronaEntity.Entry) {
            itemView.tvTitle.text = entry.gsxcountry.t
            itemView.tvCases.text = entry.gsxconfirmedcases.t
            itemView.tvDeathCase.text = if(entry.gsxreporteddeaths.t == "" || entry.gsxreporteddeaths.t == "0") "-" else entry.gsxreporteddeaths.t
        }
    }
}
