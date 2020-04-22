package co.kyald.coronavirustracking.ui.feature.menuscreen.fragment.trending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.kyald.coronavirustracking.databinding.FragmentGraphBinding
import co.kyald.coronavirustracking.databinding.FragmentTrendingBinding
import co.kyald.coronavirustracking.ui.adapter.CoronaRecyclerViewAdapter
import co.kyald.coronavirustracking.ui.adapter.TrendingRecyclerViewAdapter
import co.kyald.coronavirustracking.ui.feature.mainscreen.MainActivity
import co.kyald.coronavirustracking.utils.DataDummy
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TrendingFragment : Fragment() {

    private val viewModel by sharedViewModel<TrendingViewModel>()

    lateinit var binding: FragmentTrendingBinding


    companion object{
        private lateinit var trendingAdapter: TrendingRecyclerViewAdapter
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTrendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvData()
    }


    private fun setupRvData() {

        trendingAdapter =
            TrendingRecyclerViewAdapter(
                context!!
            )

        binding.rvData.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = trendingAdapter
        }

        trendingAdapter.setEntity(DataDummy.generateCoronaEntity())
    }

}
