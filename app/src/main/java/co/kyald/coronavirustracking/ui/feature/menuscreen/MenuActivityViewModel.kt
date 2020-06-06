package co.kyald.coronavirustracking.ui.feature.menuscreen

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.database.model.CoronaEntity
import co.kyald.coronavirustracking.data.repository.CoronaS2Repository
import co.kyald.coronavirustracking.data.repository.CoronaS3Repository
import co.kyald.coronavirustracking.data.repository.CoronaS4Repository
import co.kyald.coronavirustracking.data.repository.CoronaS5Repository
import co.kyald.coronavirustracking.utils.ChartUtils
import co.kyald.coronavirustracking.utils.Constants
import co.kyald.coronavirustracking.utils.extensions.color
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.coroutines.Dispatchers


class MenuActivityViewModel(
    private val context: Context,
    private val coronaS2Repository: CoronaS2Repository
) : ViewModel() {

    var coronaCountryLiveDataSource: MutableLiveData<CoronaEntity> =
        coronaS2Repository.coronaCountryLiveDataS2

    var coronaLineChartLiveDataSource: MutableLiveData<LineData> = MutableLiveData()
    var coronaBarChartLiveDataSource: MutableLiveData<BarData> = MutableLiveData()

    init {
        coronaS2Repository.fetchCoronaCountryDataS2(Dispatchers.IO, "Indonesia")

        setupLineChartData()
        setupBarChartData()
    }


    fun setupLineChartData() {

//        val totalConfirmed = LineDataSet(
//            daily.mapIndexed { index, dailyItem ->
//                Entry(
//                    index.toFloat(),
//                    dailyItem.totalConfirmed.toFloat(),
//                    NumberUtils.formatTime(dailyItem.date)
//                )
//            }, getString(R.string.confirmed)
//        ).apply {
//            setLineChartStyle(this, R.color.color_confirmed)
//        }

        val total = LineDataSet(
            mutableListOf(
                Entry(10f, 100f, 200000)
            ), "I"
        ).apply {
            ChartUtils().setLineChartStyle(context, this, R.color.colorConfirm)
        }

        val total2 = LineDataSet(
            mutableListOf(
                Entry(20f, 200f, 200000)
            ), "Id"
        ).apply {
            ChartUtils().setLineChartStyle(context, this, R.color.colorDeaths)
        }

        val total3 = LineDataSet(
            mutableListOf(
                Entry(30f, 300f, 200000)
            ), "Indo"
        ).apply {
            ChartUtils().setLineChartStyle(context, this, R.color.colorRecovered)
        }

        val lineData = LineData(total, total2, total3)

        coronaLineChartLiveDataSource.postValue(lineData)
    }


    fun setupBarChartData() {
//        val values = data.mapIndexed { index, perCountryProvinceItem ->
//            BarEntry(
//                index.toFloat(), floatArrayOf(
//                    perCountryProvinceItem.totalConfirmed.toFloat(),
//                    perCountryProvinceItem.totalDeath.toFloat(),
//                    perCountryProvinceItem.totalRecovered.toFloat()
//                ), perCountryProvinceItem.name
//            )
//        }

        val values = mutableListOf(
            BarEntry(
                0f, floatArrayOf(
                    100f,
                    200f,
                    300f
                ), "2010"
            ),
            BarEntry(
                1f, floatArrayOf(
                    100f,
                    200f,
                    300f
                ), "2011"
            ),
            BarEntry(
                2f, floatArrayOf(
                    100f,
                    200f,
                    300f
                ), "2012"
            )
        )


        val barDataSet = BarDataSet(values, "Global")
        barDataSet.stackLabels = arrayOf(
            "Confirm",
            "Deaths",
            "Recover"
        )
        barDataSet.setColors(
            context.color(R.color.colorConfirm),
            context.color(R.color.colorDeaths),
            context.color(R.color.colorRecovered)
        )
        barDataSet.setDrawValues(false)
        val dataSet = arrayListOf<IBarDataSet>()
        dataSet.add(barDataSet)
        dataSet.add(barDataSet)

        coronaBarChartLiveDataSource.postValue(BarData(dataSet))
    }
}