package nl.sogeti.android.gpstracker.ng.features.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import nl.sogeti.android.gpstracker.ng.features.gpxexport.GpxShareProvider
import nl.sogeti.android.gpstracker.ng.features.gpxexport.ShareIntentFactory
import nl.sogeti.android.gpstracker.ng.features.gpximport.GpxImportController
import nl.sogeti.android.gpstracker.ng.features.gpximport.GpxParser
import nl.sogeti.android.gpstracker.ng.features.map.LocationFactory
import nl.sogeti.android.gpstracker.ng.features.map.LocationFactoryImpl
import nl.sogeti.android.gpstracker.ng.features.map.TrackReaderFactory
import nl.sogeti.android.gpstracker.ng.features.map.rendering.TrackTileProviderFactory
import nl.sogeti.android.gpstracker.ng.features.trackedit.TrackTypeDescriptions
import nl.sogeti.android.gpstracker.ng.features.tracklist.ImportNotification
import nl.sogeti.android.gpstracker.ng.features.tracklist.summary.SummaryCalculator
import nl.sogeti.android.gpstracker.ng.features.tracklist.summary.SummaryManager
import nl.sogeti.android.gpstracker.ng.features.wear.StatisticsCollector
import nl.sogeti.android.gpstracker.service.util.PermissionRequester
import nl.sogeti.android.gpstracker.utils.VersionHelper
import nl.sogeti.android.gpstracker.utils.concurrent.ExecutorFactory
import nl.sogeti.android.gpstracker.v2.sharedwear.messaging.MessageSenderFactory
import nl.sogeti.android.gpstracker.v2.sharedwear.util.StatisticsFormatting
import nl.sogeti.android.gpstracker.v2.sharedwear.util.TimeSpanCalculator
import java.util.*
import javax.inject.Named

@FeatureScope
@Module
class FeatureModule(val context: Context) {

    @FeatureScope
    @Provides
    fun summaryManager() = SummaryManager()

    @FeatureScope
    @Provides
    fun importNotification() = ImportNotification(context)

    @Provides
    fun summaryCalculator() = SummaryCalculator()

    @Provides
    fun trackReaderFactory() = TrackReaderFactory()

    @Provides
    fun trackTileProviderFactory() = TrackTileProviderFactory()

    @Provides
    fun trackTypeDescriptions() = TrackTypeDescriptions()

    @Provides
    fun shareIntentFactory() = ShareIntentFactory()

    @Provides
    @Named("shareProviderAuthority")
    fun shareProviderAuthority(): String {
        return GpxShareProvider.AUTHORITY
    }

    @Provides
    fun gpxParser() = GpxParser(context)

    @Provides
    fun gpxImportController() = GpxImportController(context)

    @Provides
    fun executorFactory() = ExecutorFactory()

    @Provides
    fun statisticsCollector() = StatisticsCollector()

    @Provides
    fun permissionRequester(): PermissionRequester = PermissionRequester()

    @Provides
    fun statisticsFormatting(locale: Locale, timeSpanUtil: TimeSpanCalculator) = StatisticsFormatting(locale, timeSpanUtil)

    @Provides
    fun messageSenderFactory() = MessageSenderFactory()

    @Provides
    fun timeSpanCalculator() = TimeSpanCalculator()

    @Provides
    fun locationFactory(): LocationFactory = LocationFactoryImpl()

    @Provides
    fun versionHelper() = VersionHelper()
}
