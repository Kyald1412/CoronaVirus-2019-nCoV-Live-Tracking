package co.kyald.coronavirustracking.ui.feature.mainscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.model.CoronaEntity
import kotlinx.android.synthetic.main.item_country.view.*

class MainRecyclerViewAdapter(
    private val context: Context) :
    RecyclerView.Adapter<MainRecyclerViewAdapter.CardViewViewHolder>() {

    private var mData: MutableList<CoronaEntity.Entry> = mutableListOf()

    fun setEntity(entryList: List<CoronaEntity.Entry>) {
        mData.clear()
        mData.addAll(entryList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CardViewViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_country, viewGroup, false)
        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(entry: CoronaEntity.Entry) {
            itemView.tvTitle.text = entry.gsxcountry.t
            itemView.tvCases.text = entry.gsxconfirmedcases.t
            itemView.tvDeathCase.text = if(entry.gsxreporteddeaths.t == "") "-" else entry.gsxreporteddeaths.t
        }
    }
}
