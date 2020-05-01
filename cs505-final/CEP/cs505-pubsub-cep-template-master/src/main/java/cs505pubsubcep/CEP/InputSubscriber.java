package cs505pubsubcep.CEP;

import io.siddhi.core.util.transport.InMemoryBroker;
import cs505pubsubcep.Launcher;
public class InputSubscriber implements InMemoryBroker.Subscriber {

    private String topic;

    public InputSubscriber(String topic, String streamName) {
        this.topic = topic;
    }

    @Override
    public void onMessage(Object msg) {}

    @Override
    public String getTopic() {
        return topic;
    }

}
