package co.kyald.coronavirustracking.utils

import android.content.Context
import androidx.annotation.ColorRes
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.utils.extensions.color
import com.github.mikephil.charting.data.LineDataSet
import co.kyald.coronavirustracking.utils.extensions.color


class ChartUtils {
    fun setLineChartStyle(
        context: Context,
        lineDataSet: LineDataSet, @ColorRes colorResId: Int
    ) {
        with(lineDataSet) {
            color = context.color(colorResId)
            lineWidth = 2f
            circleRadius = 1f
            setDrawCircleHole(false)
            setCircleColor(context.color(colorResId))
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextColor = context.color(R.color.white)

            setDrawFilled(true)
            fillColor = context.color(colorResId)
            fillAlpha = 100
        }
    }

}
