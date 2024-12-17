package com.gaitmonitoring.screens.home

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun GraphUI(
    modifier: Modifier = Modifier,
    data: LineData,

    ) {
    val graphBackgroundColor = android.graphics.Color.parseColor("#5BC0EB") // Graph background color
    val boxBackgroundColor = Color(0xFF05648B)

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(24.dp, RoundedCornerShape(10.dp))
            .background(boxBackgroundColor, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        AndroidView(
            modifier = modifier
                .matchParentSize(),
            factory = { ctx ->

                LineChart(ctx).apply {

                    animateX(1200, Easing.EaseInSine)
                    description.isEnabled = false
                    setBackgroundColor(graphBackgroundColor)
                    xAxis.apply {
                        setDrawGridLines(false)
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1F
                        setDrawLabels(false)
                        disableAxisLineDashedLine()
                        textColor = android.graphics.Color.WHITE
                    }

                    axisLeft.apply {
                        setDrawLabels(false)
                        setDrawGridLines(false)  // Disable horizontal grid lines
                        setDrawAxisLine(false)
                        textColor = android.graphics.Color.WHITE
                    }

                    axisRight.apply {
                        isEnabled = false
                        textColor = android.graphics.Color.WHITE
                    }

                    legend.apply {
                        orientation = Legend.LegendOrientation.VERTICAL
                        verticalAlignment = Legend.LegendVerticalAlignment.TOP
                        horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                        textSize = 10F
                        form = Legend.LegendForm.LINE
                        textColor = android.graphics.Color.WHITE
                    }



                    data.dataSets.forEach { dataSet ->
                        (dataSet as LineDataSet).apply {
                            setDrawValues(false)
                            valueTextColor =
                                android.graphics.Color.WHITE // Disable drawing values on data points
                        }
                    }
                }

            },
            update = {
                it.invalidate()
                it.data = data
            }

        )
    }

}

/** setup how data labels will be o x axis */
private class MyAxisFormatter : IndexAxisValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val index = value.toInt()
        return "Label $index"
    }
}