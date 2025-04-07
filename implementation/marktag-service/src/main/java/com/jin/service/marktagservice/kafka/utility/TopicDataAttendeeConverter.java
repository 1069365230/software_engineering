package com.jin.service.marktagservice.kafka.utility;
import org.json.JSONException;
import org.json.JSONObject;

public class TopicDataAttendeeConverter {
    private JSONObject json;
    private int globalAttendeeID;
    private String role;
    public TopicDataAttendeeConverter(String receivedData) throws JSONException {
        this.json = new JSONObject(receivedData);
        this.globalAttendeeID = json.getInt("id");
        this.role = json.getString("role");
    }

    public int getGlobalAttendeeID() {
        return globalAttendeeID;
    }

    public String getRole() {
        return role;
    }
}
