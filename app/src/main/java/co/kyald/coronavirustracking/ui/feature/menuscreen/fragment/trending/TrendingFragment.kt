package co.kyald.coronavirustracking.ui.feature.menuscreen.fragment.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.databinding.FragmentGraphBinding
import co.kyald.coronavirustracking.utils.NumberUtils
import co.kyald.coronavirustracking.utils.extensions.color
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GraphFragment : Fragment() {

    private val viewModel by sharedViewModel<GraphViewModel>()

    lateinit var binding: FragmentGraphBinding

    companion object {
        private const val TEXT_ANIMATION_DURATION = 1000L
        private const val PIE_ANIMATION_DURATION = 1500
        private const val PIE_RADIUS = 75f
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pieDataSet = PieDataSet(
            listOf(
                PieEntry(204f, context?.getString(R.string.info_active)),
                PieEntry(204f, context?.getString(R.string.info_confirm)),
                PieEntry(204f, context?.getString(R.string.info_death)),
                PieEntry(204f, context?.getString(R.string.info_recovered))
            ), context?.getString(R.string.app_name)
        )

        val colors = arrayListOf(
            context?.color(R.color.colorAccent),
            context?.color(R.color.colorAccent),
            context?.color(R.color.colorAccent)
        )
        pieDataSet.colors = colors

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)

        with(binding.pieChart) {
            if (data == pieData) return
            data = pieData
            legend.isEnabled = false
            description = null
            holeRadius = PIE_RADIUS
            setHoleColor(context.color(R.color.colorAccent))
            setDrawEntryLabels(false)
            animateY(PIE_ANIMATION_DURATION, Easing.EaseInOutQuart)
            invalidate()
        }

        setupChart()
        setupData()

        setupChartBar()
        setupDataBar()
    }

    private fun setupChart() {
        with(binding.lineChart) {
            animateX(1500)
            legend.textColor = context.color(R.color.white)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = context.color(R.color.colorAccent)

            axisLeft.textColor = context.color(R.color.colorAccent)
            axisRight.textColor = context.color(R.color.colorAccent)
            description.isEnabled = false

            axisRight.enableGridDashedLine(10f, 10f, 2f)
            axisLeft.enableGridDashedLine(10f, 10f, 2f)
            xAxis.enableGridDashedLine(10f, 10f, 2f)

//            val dates = data.map { NumberUtils.formatTime(it.date) }
//            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
//                override fun getFormattedValue(value: Float): String {
//                    return dates[value.toInt()]
//                }
//            }

        }

    }


    fun setupData() {
        val totalDeath = LineDataSet(
            listOf(
                Entry(
                    0f,
                    0f,
                    NumberUtils.formatTime(1586269716878)
                )
            )
            , getString(R.string.app_name)
        ).apply {
            setLineChartStyle(this, R.color.colorAccent)
        }
        val lineData = LineData(totalDeath)
        binding.lineChart.data = lineData
    }


//    private fun setupData(daily: List<PerCountryDailyItem>) {
//        val totalConfirmed = LineDataSet(
//            daily.mapIndexed { index, dailyItem ->
//                Entry(
//                    index.toFloat(),
//                    dailyItem.totalConfirmed.toFloat(),
//                    NumberUtils.formatTime(dailyItem.date)
//                )
//            }, getString(R.string.app_name)
//        ).apply {
//            setLineChartStyle(this, R.color.colorAccent)
//        }
//        val totalRecovered = LineDataSet(
//            daily.mapIndexed { index, dailyItem ->
//                Entry(
//                    index.toFloat(),
//                    dailyItem.totalRecovered.toFloat(),
//                    NumberUtils.formatTime(dailyItem.date)
//                )
//            }, getString(R.string.recovered)
//        ).apply {
//            setLineChartStyle(this, R.color.colorAccent)
//        }
//        val totalDeath = LineDataSet(
//            daily.mapIndexed { index, dailyItem ->
//                Entry(
//                    index.toFloat(),
//                    dailyItem.totalDeath.toFloat(),
//                    NumberUtils.formatTime(dailyItem.date)
//                )
//            }, getString(R.string.app_name)
//        ).apply {
//            setLineChartStyle(this, R.color.colorAccent)
//        }
//        val lineData = LineData(totalConfirmed, totalDeath, totalRecovered)
//        binding.lineChart.data = lineData
//    }

    private fun setLineChartStyle(lineDataSet: LineDataSet, @ColorRes colorResId: Int) {
        with(lineDataSet) {
            color = context!!.color(colorResId)
            lineWidth = 2f
            circleRadius = 1f
            setDrawCircleHole(false)
            setCircleColor(context!!.color(colorResId))
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextColor = context!!.color(R.color.white)

            setDrawFilled(true)
            fillColor = context!!.color(colorResId)
            fillAlpha = 100
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGraphBinding.inflate(inflater, container, false)
        return binding.root
    }



    private fun setupChartBar() {
        with(binding.barChart) {
            animateX(1500)
            legend.textColor = context.color(R.color.white)
            setMaxVisibleValueCount(40)

            xAxis.isEnabled = false
            axisLeft.textColor = context.color(R.color.colorAccent)
            axisRight.textColor = context.color(R.color.colorAccent)
            description.isEnabled = false

            axisRight.enableGridDashedLine(10f, 10f, 2f)
            axisLeft.enableGridDashedLine(10f, 10f, 2f)
            xAxis.enableGridDashedLine(10f, 10f, 2f)

//            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
//                override fun onNothingSelected() {
//                }
//
//                override fun onValueSelected(e: Entry?, h: Highlight?) {
//                    val barEnrty = e as? BarEntry
//                    kotlin.with(binding) {
//                        barEnrty?.let { dataBarEnty ->
//                            layoutData.visible()
//                            txtProvince.text = dataBarEnty.data.toString()
//                            txtConfirmed.text = getString(
//                                R.string.confirmed_case_count,
//                                dataBarEnty.yVals[0].toInt().toString()
//                            )
//                            txtDeath.text = getString(
//                                R.string.death_case_count,
//                                dataBarEnty.yVals[1].toInt().toString()
//                            )
//                            txtRecovered.text = getString(
//                                R.string.recovered_case_count,
//                                dataBarEnty.yVals[2].toInt().toString()
//                            )
//
//                        }
//
//                    }
//                }
//            })

        }

    }

    private fun setupDataBar() {
//        val values = data.mapIndexed { index, perCountryProvinceItem ->
//            BarEntry(
//                index.toFloat(), floatArrayOf(
//                    perCountryProvinceItem.totalConfirmed.toFloat(),
//                    perCountryProvinceItem.totalDeath.toFloat(),
//                    perCountryProvinceItem.totalRecovered.toFloat()
//                ), perCountryProvinceItem.name
//            )
//        }

        val values =  listOf(
            BarEntry(
                0f, floatArrayOf(
                    0f,
                    0f,
                    0f
                ), "asd"
            )
        )
        val barDataSet = BarDataSet(values, getString(R.string.data_source))
        barDataSet.stackLabels = arrayOf(
            getString(R.string.select_data_source),
            getString(R.string.data_source),
            getString(R.string.recovered)
        )
        barDataSet.setColors(
            context!!.color(R.color.colorAccent),
            context!!.color(R.color.colorAccent),
            context!!.color(R.color.colorAccent)
        )
        barDataSet.setDrawValues(false)
        val dataSet = arrayListOf<IBarDataSet>()
        dataSet.add(barDataSet)
        binding.barChart.data = BarData(dataSet)
        binding.barChart.invalidate()
    }


}
