package edu.ase.loginservice.stream;

import edu.ase.loginservice.models.EMSUser;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class StreamProducer {
    private StreamBridge streamBridge;
    private final String TOPIC = "user.management";

    public StreamProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishUser(EMSUser emsUser) {
        streamBridge.send(TOPIC, emsUser);
    }
}
