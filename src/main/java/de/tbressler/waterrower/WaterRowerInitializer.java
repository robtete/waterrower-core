package de.tbressler.waterrower;

import de.tbressler.waterrower.io.RxtxChannelInitializer;
import de.tbressler.waterrower.io.RxtxCommunicationService;
import de.tbressler.waterrower.io.WaterRowerConnector;
import de.tbressler.waterrower.subscriptions.SubscriptionPollingService;
import de.tbressler.waterrower.watchdog.DeviceVerificationWatchdog;
import de.tbressler.waterrower.watchdog.PingWatchdog;
import io.netty.bootstrap.Bootstrap;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.Objects.requireNonNull;

/**
 * Initializes the dependencies of the WaterRower class based on the given parameters.
 *
 * @author Tobias Bressler
 * @version 1.0
 */
public class WaterRowerInitializer {

    /* Handles the connection to the WaterRower. */
    private final WaterRowerConnector connector;

    /* Polls and handles subscriptions. */
    private final SubscriptionPollingService subscriptionPollingService;

    /* Watchdog that checks if a ping is received periodically. */
    private final PingWatchdog pingWatchdog;

    /* Watchdog that checks if the device sends it's model information in order to verify
     * compatibility with the library. */
    private final DeviceVerificationWatchdog deviceVerificationWatchdog;


    /**
     * Initializes the dependencies of the WaterRower class based on the given parameters.
     *
     * @param pollingInterval The polling interval for the subscriptions, must not be null.
     *                        Recommended = 1 second.
     * @param timeoutInterval The timeout interval for messages, if a message was not received from the WaterRower
     *                        during this interval a timeout error will get fired, must not be null.
     *                        Recommended = 1 second.
     * @param threadPoolSize The number of threads to keep in the pool, which should be used by the WaterRower
     *                       service even if they are idle.
     *                       Recommended = 5.
     */
    public WaterRowerInitializer(Duration pollingInterval, Duration timeoutInterval, int threadPoolSize) {
        requireNonNull(pollingInterval);
        requireNonNull(timeoutInterval);
        if (threadPoolSize < 1)
            throw new IllegalArgumentException("The number of thread must be at least 1!");

        Bootstrap bootstrap = new Bootstrap();

        RxtxCommunicationService communicationService = new RxtxCommunicationService(bootstrap, new RxtxChannelInitializer());

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(threadPoolSize);

        connector = new WaterRowerConnector(communicationService);

        subscriptionPollingService = new SubscriptionPollingService(pollingInterval, connector, executorService);

        pingWatchdog = new PingWatchdog(timeoutInterval, executorService);

        deviceVerificationWatchdog = new DeviceVerificationWatchdog(timeoutInterval, executorService);
    }


    /**
     * Returns the connector, which handles the connection to the WaterRower.
     *
     * @return The connector, never null.
     */
    WaterRowerConnector getWaterRowerConnector() {
        return connector;
    }

    /**
     * Returns the watchdog that checks if a ping was received periodically.
     *
     * @return The watchdog, never null.
     */
    PingWatchdog getPingWatchdog() {
        return pingWatchdog;
    }

    /**
     * Returns the watchdog that checks if the device sends it's model information in order to verify
     * compatibility with the library.
     *
     * @return The watchdog, never null.
     */
    DeviceVerificationWatchdog getDeviceVerificationWatchdog() {
        return deviceVerificationWatchdog;
    }

    /**
     * Returns the subscription polling service, which polls and handles the subscriptions.
     *
     * @return The subscription polling service, never null.
     */
    SubscriptionPollingService getSubscriptionPollingService() {
        return subscriptionPollingService;
    }

}
