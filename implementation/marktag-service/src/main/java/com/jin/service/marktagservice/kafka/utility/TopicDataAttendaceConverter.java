package com.jin.service.marktagservice.kafka.utility;

import org.json.JSONException;
import org.json.JSONObject;

public class TopicDataAttendaceConverter {
    private int globalEventID;
    private int globalAttendeeID;
    private JSONObject json;
    boolean action;

    public TopicDataAttendaceConverter(String receivedData) throws JSONException {
        this.json = new JSONObject(receivedData);
        this.globalAttendeeID = json.getInt("attendeeId");
        this.globalEventID = json.getInt("eventId");
        this.action = json.getBoolean("active");
    }

    public boolean getAction() {
        return action;
    }

    public int getGlobalEventID() {
        return globalEventID;
    }

    public int getGlobalAttendeeID() {
        return globalAttendeeID;
    }
}
