package com.algolia.instantsearch.insights

import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.EventObjects
import com.algolia.instantsearch.insights.event.EventUploader
import com.algolia.instantsearch.insights.internal.InsightsLogger
import com.algolia.instantsearch.insights.internal.converter.ConverterEventToEventInternal
import com.algolia.instantsearch.insights.internal.currentTimeMillis
import com.algolia.instantsearch.insights.internal.database.Database
import com.algolia.instantsearch.insights.internal.event.EventInternal
import com.algolia.instantsearch.insights.internal.webservice.WebService
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Main class used for interacting with the InstantSearch Insights library.
 * In order to send insights, you first need to register an APP ID and API key for a given Index.
 * Once registered, you can simply call `Insights.shared(index: String)` to send your events.
 */
public class Insights internal constructor(
    private val indexName: String,
    private val eventUploader: EventUploader,
    internal val database: Database,
    internal val webService: WebService
) {

    /**
     * Change this variable to `true` or `false` to enable or disable logging.
     * Use a filter on tag `Algolia Insights` to see all logs generated by the Insights library.
     */
    @Suppress("unused") // setter does side-effect
    public var loggingEnabled: Boolean = false
        set(value) {
            field = value
            InsightsLogger.enabled[indexName] = value
        }

    /**
     * Change this variable to `true` or `false` to disable Insights, opting-out the current session from tracking.
     */
    public var enabled: Boolean = true

    /**
     * Change this variable to change the default debouncing interval. Values lower than 15 minutes will be ignored.
     */
    public var debouncingIntervalInMinutes: Long? = null
        set(value) {
            value?.let { eventUploader.setInterval(value) }
        }

    /**
     * Set a user identifier that will override any event's.
     *
     * Depending if the user is logged-in or not, several strategies can be used from a sessionId to a technical identifier.
     * You should always send pseudonymous or anonymous userTokens.
     */
    public var userToken: String? = null

    private fun userTokenOrThrow(): String = userToken ?: throw InsightsException.NoUserToken()

    /**
     * Change this variable to change the default amount of event sent at once.
     */
    public var minBatchSize: Int = 10

    init {
        eventUploader.startPeriodicUpload()
    }

    // region Event tracking methods
    /**
     * Tracks a View event, unrelated to a specific search query.
     *
     * @param eventName the event's name, **must not be empty**.
     * @param objectIDs the viewed object(s)' `objectID`.
     * @param timestamp the time at which the view happened. Defaults to current time.
     */
    @JvmOverloads
    public fun viewed(
        eventName: String,
        objectIDs: EventObjects.IDs,
        timestamp: Long = currentTimeMillis
    ) = viewed(
        Event.View(
            eventName = eventName,
            userToken = userTokenOrThrow(),
            timestamp = timestamp,
            eventObjects = objectIDs
        )
    )

    /**
     * Tracks a View event, unrelated to a specific search query.
     *
     * @param eventName the event's name, **must not be empty**.
     * @param filters the clicked filter(s).
     * @param timestamp the time at which the view happened. Defaults to current time.
     */
    @JvmOverloads
    public fun viewed(
        eventName: String,
        filters: EventObjects.Filters,
        timestamp: Long = currentTimeMillis
    ) = viewed(
        Event.View(
            eventName = eventName,
            userToken = userTokenOrThrow(),
            timestamp = timestamp,
            eventObjects = filters
        )
    )

    /**
     * Tracks a click event, unrelated to a specific search query.
     *
     * @param eventName the event's name, **must not be empty**.
     * @param objectIDs the clicked object(s)' `objectID`.
     * @param timestamp the time at which the click happened. Defaults to current time.
     */
    @JvmOverloads
    public fun clicked(
        eventName: String,
        objectIDs: EventObjects.IDs,
        timestamp: Long = currentTimeMillis
    ) = clicked(
        Event.Click(
            eventName = eventName,
            userToken = userTokenOrThrow(),
            timestamp = timestamp,
            eventObjects = objectIDs
        )
    )


    /**
     * Tracks a click event, unrelated to a specific search query.
     *
     * @param eventName the event's name, **must not be empty**.
     * @param filters the clicked filter(s).
     * @param timestamp the time at which the click happened. Defaults to current time.
     */
    @JvmOverloads
    public fun clicked(
        eventName: String,
        filters: EventObjects.Filters,
        timestamp: Long = currentTimeMillis
    ) = clicked(
        Event.Click(
            eventName = eventName,
            userToken = userTokenOrThrow(),
            timestamp = timestamp,
            eventObjects = filters
        )
    )

    /**
     * Tracks a Click event after a search has been done.
     *
     * @param eventName the event's name, **must not be empty**.
     * @param queryId the related [query's identifier][https://www.algolia.com/doc/guides/insights-and-analytics/click-analytics/?language=php#identifying-the-query-result-position].
     * @param objectIDs the object(s)' `objectID`.
     * @param positions the clicked object(s)' position(s).
     * @param timestamp the time at which the click happened. Defaults to current time.
     */
    @JvmOverloads
    public fun clickedAfterSearch(
        eventName: String,
        queryId: String,
        objectIDs: EventObjects.IDs,
        positions: List<Int>,
        timestamp: Long = currentTimeMillis
    ) = clicked(
        Event.Click(
            eventName = eventName,
            userToken = userTokenOrThrow(),
            timestamp = timestamp,
            eventObjects = objectIDs,
            queryId = queryId,
            positions = positions
        )
    )

    /**
     * Tracks a Conversion event, unrelated to a specific search query.
     *
     * @param eventName the event's name, **must not be empty**.
     * @param timestamp the time at which the conversion happened. Defaults to current time.
     * @param filters the converted filter(s).
     */
    @JvmOverloads
    public fun converted(
        eventName: String,
        filters: EventObjects.Filters,
        timestamp: Long = currentTimeMillis
    ) = converted(
        Event.Conversion(
            eventName = eventName,
            userToken = userTokenOrThrow(),
            timestamp = timestamp,
            eventObjects = filters
        )
    )

    /**
     * Tracks a Conversion event, unrelated to a specific search query.
     *
     * @param eventName the event's name, **must not be empty**.
     * @param objectIDs the object(s)' `objectID`.
     * @param timestamp the time at which the conversion happened. Defaults to current time.
     */
    @JvmOverloads
    public fun converted(
        eventName: String,
        objectIDs: EventObjects.IDs,
        timestamp: Long = currentTimeMillis
    ) = converted(
        Event.Conversion(
            eventName = eventName,
            userToken = userTokenOrThrow(),
            timestamp = timestamp,
            eventObjects = objectIDs
        )
    )

    /**
     * Tracks a Conversion event after a search has been done.
     *
     * @param eventName the event's name, **must not be empty**.
     * @param queryId the related [query's identifier][https://www.algolia.com/doc/guides/insights-and-analytics/click-analytics/?language=php#identifying-the-query-result-position].
     * @param objectIDs the object(s)' `objectID`.
     * @param timestamp the time at which the conversion happened. Defaults to current time.
     */
    @JvmOverloads
    public fun convertedAfterSearch(
        eventName: String,
        queryId: String,
        objectIDs: EventObjects.IDs,
        timestamp: Long = currentTimeMillis
    ) = converted(
        Event.Conversion(
            eventName = eventName,
            userToken = userTokenOrThrow(),
            timestamp = timestamp,
            eventObjects = objectIDs,
            queryId = queryId
        )
    )

    /**
     * Tracks a View event constructed manually.
     */
    public fun viewed(event: Event.View) = track(event)

    /**
     * Tracks a Click event constructed manually.
     */
    public fun clicked(event: Event.Click) = track(event)

    /**
     * Tracks a Conversion event, constructed manually.
     */
    public fun converted(event: Event.Conversion) = track(event)

    /**
     * Method for tracking an event.
     * For a complete description of events see our [documentation][https://www.algolia.com/doc/rest-api/insights/?language=android#push-events].
     * @param [event] An [Event] that you want to track.
     */
    public fun track(event: Event) {
        track(ConverterEventToEventInternal.convert(event to indexName))
    }

    private fun track(event: EventInternal) {
        if (enabled) {
            database.append(event)
            if (database.count() >= minBatchSize) {
                eventUploader.startOneTimeUpload()
            }
        }
    }

    // endregion

    override fun toString(): String {
        return "Insights(indexName='$indexName', webService=$webService)"
    }

    companion object {

        internal val insightsMap = mutableMapOf<String, Insights>()


        /**
         * Register your index with a given appId and apiKey.
         * @param context A [Context].
         * @param appId The given app id for which you want to track the events.
         * @param apiKey The API Key for your `appId`.
         * @param indexName The index that is being tracked.
         * @param configuration A [Configuration] class.
         * @return An [Insights] instance.
         */
        internal fun register(
            eventUploader: EventUploader,
            database: Database,
            webService: WebService,
            indexName: String,
            configuration: Configuration = Configuration(5000, 5000)
        ): Insights {

            val insights = Insights(indexName, eventUploader, database, webService)
            insights.userToken = configuration.defaultUserToken

            val previousInsights = insightsMap.put(indexName, insights)
            previousInsights?.let {
                InsightsLogger.log("Registering new Insights for indexName $indexName. Previous instance: $insights")
            }
            shared = insights
            return insights
        }

        /**
         * Access an already registered `Insights` without having to pass the `apiKey` and `appId`.
         *
         * If the index was not register before, it will throw an [InsightsException.IndexNotRegistered] exception.
         * @param indexName The index that is being tracked.
         * @return An [Insights] instance.
         * @throws InsightsException.IndexNotRegistered if no index was registered as [indexName] before.
         */
        @JvmStatic
        public fun shared(indexName: String): Insights {
            return insightsMap[indexName]
                ?: throw InsightsException.IndexNotRegistered()
        }

        /**
         * Access the latest registered `Insights` instance, if any.
         */
        @JvmStatic
        public var shared: Insights? = null
            @JvmName("shared")
            get() = if (field != null) field else throw InsightsException.IndexNotRegistered()
    }

    /**
     * Insights configuration.
     * @param connectTimeoutInMilliseconds Maximum amount of time in milliseconds before a connect timeout.
     * @param readTimeoutInMilliseconds Maximum amount of time in milliseconds before a read timeout.
     */
    public class Configuration @JvmOverloads constructor(
        val connectTimeoutInMilliseconds: Int,
        val readTimeoutInMilliseconds: Int,
        val defaultUserToken: String? = null
    )
}
