package nl.sogeti.android.gpstracker.ng.features.graphs

import android.support.annotation.WorkerThread
import nl.sogeti.android.gpstracker.ng.features.graphs.widgets.GraphPoint
import nl.sogeti.android.gpstracker.ng.features.graphs.widgets.GraphValueDescriptor
import nl.sogeti.android.gpstracker.ng.features.summary.Summary

interface GraphDataProvider {

    val valueDescriptor: GraphValueDescriptor
    val xLabel: Int
    val yLabel: Int
    val inverseSpeed : Boolean

    @WorkerThread
    fun calculateGraphPoints(summary: Summary, inverseRunnersSpeed: Boolean): List<GraphPoint>
}
