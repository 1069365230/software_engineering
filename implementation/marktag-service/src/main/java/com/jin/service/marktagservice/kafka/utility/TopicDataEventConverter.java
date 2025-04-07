package com.jin.service.marktagservice.kafka.utility;

import com.jin.service.marktagservice.exception.TopicIncorrectDataException;
import org.json.JSONException;
import org.json.JSONObject;

public class TopicDataEventConverter {
    private JSONObject json;
    private Integer globalId;
    private String name;

    public TopicDataEventConverter(String receivedData) throws JSONException {
        this.json = new JSONObject(receivedData);
        this.globalId = json.getInt("id");
        this.name = json.getString("name");
    }

    public Integer getGlobalId() {
        return globalId;
    }

    public String getName() {
        return name;
    }
}

