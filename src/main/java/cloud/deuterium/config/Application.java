package cloud.deuterium.config;

import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Milan Stojkovic 28-Sep-2022
 */

@Startup
@ApplicationScoped
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final String DEBEZIUM_PREFIX = "debezium.";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private DebeziumEngine<?> engine;

    @Inject
    ChangeEventConsumer consumer;

    @PostConstruct
    public void start() {
        Config config = ConfigProvider.getConfig();
        Properties properties = getProperties(config);

        this.engine = DebeziumEngine.create(Json.class, Json.class)
                .using(properties)
                .notifying(consumer)
                .build();

        executor.execute(engine::run);
    }

    private Properties getProperties(Config config) {

        final Properties properties = new Properties();

        for (String name : config.getPropertyNames()) {
            if (name.startsWith(DEBEZIUM_PREFIX)) {
                String key = name.substring(DEBEZIUM_PREFIX.length());
                String value = config.getConfigValue(name).getValue();
                properties.setProperty(key, value);
            }
        }
        return properties;
    }

    public void stop(@Observes ShutdownEvent event) {
        try {
            LOGGER.info("Attempting to stop the embedded Debezium engine");
            engine.close();
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);

        } catch (Exception e) {
            LOGGER.error("Exception while shutting down Debezium: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
