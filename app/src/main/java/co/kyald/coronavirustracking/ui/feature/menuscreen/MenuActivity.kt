package co.kyald.coronavirustracking.ui.feature.menuscreen

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.database.model.CoronaEntity
import co.kyald.coronavirustracking.ui.feature.preferencescreen.PreferenceActivity
import co.kyald.coronavirustracking.utils.extensions.color
import co.kyald.coronavirustracking.utils.extensions.observe
import co.kyald.coronavirustracking.utils.extensions.startActivity
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.activity_menu.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MenuActivity : AppCompatActivity() {

    private val viewModel: MenuActivityViewModel by viewModel()
    private val preferences: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initObserver()
        initListener()

    }

    private fun initListener() {
        imgSetting.setOnClickListener {
            startActivity<PreferenceActivity>()
        }
    }

    private fun initObserver() {
        observe(viewModel.coronaCountryLiveDataSource, ::setupCountryData)
        observe(viewModel.coronaLineChartLiveDataSource, ::setupLineChartData)
        observe(viewModel.coronaBarChartLiveDataSource, ::setupBarChartData)
    }
    private fun setupCountryData(coronaEntity: CoronaEntity) {
        tvFlag.text = coronaEntity.info.country.toString()
        tvRecovered.text = coronaEntity.info.case_recovered.toString()
        tvActive.text = coronaEntity.info.case_actives.toString()
        tvInfected.text = coronaEntity.info.case_confirms.toString()
        tvDeaths.text = coronaEntity.info.case_deaths.toString()
        Glide.with(this).load(coronaEntity.info.flags).into(imgFlag)
    }

    private fun setupBarChartData(barData: BarData){
        with(bar_chart) {
//            animateX(1500)
            legend.textColor = color(R.color.white)
//            setMaxVisibleValueCount(40)

            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.setDrawInside(true)
            legend.yOffset = 0f
            legend.xOffset = 10f
            legend.yEntrySpace = 0f
            legend.textSize = 8f

            xAxis.granularity = 1f
            xAxis.setCenterAxisLabels(true)
            xAxis.valueFormatter = object :
                ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toString()
                }
            }

            axisLeft.valueFormatter = LargeValueFormatter()
            axisLeft.setDrawGridLines(false)
            axisLeft.spaceTop = 35f
            axisLeft.axisMinimum = 0f // this replaces setStartAtZero(true)


            axisRight.isEnabled = false



//            xAxis.isEnabled = false
//            axisLeft.textColor = color(R.color.colorRecovered)
//            axisRight.textColor = color(R.color.colorRecovered)
//            description.isEnabled = false
//
//            axisRight.enableGridDashedLine(10f, 10f, 2f)
//            axisLeft.enableGridDashedLine(10f, 10f, 2f)
//            xAxis.enableGridDashedLine(10f, 10f, 2f)

//            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
//                override fun onNothingSelected() {
//                }
//
//                override fun onValueSelected(e: Entry?, h: Highlight?) {
//                    val barEnrty = e as? BarEntry
//                    with(binding) {
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

        val groupSpace = 0.08f
        val barSpace = 0.03f // x4 DataSet

        bar_chart.data = barData

        bar_chart.barData.barWidth = 0.2f;
        bar_chart.groupBars(0f, groupSpace, barSpace);


        bar_chart.invalidate()
    }

    private fun setupLineChartData(lineData: LineData) {
        with(line_chart) {
            animateX(1500)
            legend.textColor = color(R.color.white)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = color(R.color.gray1)

            axisLeft.textColor = color(R.color.gray1)
            axisRight.textColor = color(R.color.gray1)
            description.isEnabled = false

            axisRight.enableGridDashedLine(10f, 10f, 2f)
            axisLeft.enableGridDashedLine(10f, 10f, 2f)
            xAxis.enableGridDashedLine(10f, 10f, 2f)

//            val dates = data.map { NumberUtils.formatTime(it.date) }
            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "300"
                }
            }

        }

        line_chart.data = lineData

    }


}
