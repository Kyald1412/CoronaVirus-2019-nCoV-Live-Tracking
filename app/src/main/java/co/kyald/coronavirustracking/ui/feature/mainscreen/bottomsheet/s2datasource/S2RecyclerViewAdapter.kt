package co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s2datasource

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.kyald.coronavirustracking.R
import kotlinx.android.synthetic.main.item_country_second.view.*
import timber.log.Timber

class S2RecyclerViewAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<S2RecyclerViewAdapter.CardViewViewHolder>() {

    private var confirmCase: MutableList<List<String>> = mutableListOf()
    private var confirmDeath: MutableList<List<String>> = mutableListOf()
    private var confirmRecover: MutableList<List<String>> = mutableListOf()

    fun setConfirmCase(entryList: List<List<String>>) {

        confirmCase.clear()
        confirmCase.addAll(entryList)

        notifyDataSetChanged()
    }

    fun setDeathCase(entryList: List<List<String>>) {
        confirmDeath.clear()
        confirmDeath.addAll(entryList)
        notifyDataSetChanged()
    }

    fun setRecoverCase(entryList: List<List<String>>) {
        confirmRecover.clear()
        confirmRecover.addAll(entryList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CardViewViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_country_second, viewGroup, false)
        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(confirmCase[position])
    }

    override fun getItemCount(): Int = confirmCase.size-1

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(entry: List<String>) {
            try {
                itemView.tvTitle.text = if(confirmCase[adapterPosition+1][0] == "") confirmCase[adapterPosition+1][1] else confirmCase[adapterPosition+1][0]
                itemView.tvCases.text = confirmCase[adapterPosition+1][confirmCase[adapterPosition+1].size-1]
                itemView.tvDeathCase.text = confirmDeath[adapterPosition+1][confirmDeath[adapterPosition+1].size-1]
                itemView.tvRecoverCase.text = confirmRecover[adapterPosition+1][confirmRecover[adapterPosition+1].size-1]
            } catch (e :Exception){

            }
        }
    }
}
