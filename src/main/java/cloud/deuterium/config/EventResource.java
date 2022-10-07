package cloud.deuterium.config;

import io.debezium.engine.ChangeEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/**
 * Created by Milan Stojkovic 29-Sep-2022
 */

@ApplicationScoped
public class EventResource {

    public void notify(@Observes ChangeEvent<String, String> event){
        System.out.println("============================");
        System.out.println(event.value());
    }
}
