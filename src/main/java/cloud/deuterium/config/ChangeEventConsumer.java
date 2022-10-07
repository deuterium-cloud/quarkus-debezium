package cloud.deuterium.config;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by Milan Stojkovic 29-Sep-2022
 */

@ApplicationScoped
public class ChangeEventConsumer implements DebeziumEngine.ChangeConsumer<ChangeEvent<String, String>> {

    @Inject
    Event<ChangeEvent<String, String>> event;

    @Override
    public void handleBatch(List<ChangeEvent<String, String>> records,
                            DebeziumEngine.RecordCommitter<ChangeEvent<String, String>> recordCommitter) throws InterruptedException {

        for (ChangeEvent<String, String> changeEvent : records) {
            event.fire(changeEvent);
            recordCommitter.markProcessed(changeEvent);
        }

        recordCommitter.markBatchFinished();
    }
}
