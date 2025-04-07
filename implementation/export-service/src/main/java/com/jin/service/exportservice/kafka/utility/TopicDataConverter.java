package com.jin.service.exportservice.kafka.utility;

import com.jin.service.exportservice.model.Event;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TopicDataConverter {

    private String location;
    private String name;
    private String date;
    private int globalEventID;
    private int globalAttendeeID;

    boolean action;

    private List<String> tags = new ArrayList<>();
    private String tag;
    private JSONObject json;


    public TopicDataConverter(String receivedData, TopicType topicType) throws JSONException {
        this.json = new JSONObject(receivedData);



        switch (topicType) {
            case ATTENDANCE, BOOKMARK:
                this.globalAttendeeID = json.getInt("attendeeId");
                this.globalEventID = json.getInt("eventId");
                this.action = json.getBoolean("action");
                //converting tags
                JSONArray tagsArray = json.getJSONArray("tags");
                for (int i = 0; i < tagsArray.length(); i++) {
                    this.tags.add(tagsArray.getString(i));
                }

                break;

            case EVENT:
                this.globalEventID = json.getInt("id");
                this.name = json.getString("name");
                this.location = json.getString("city");
                this.date = json.getString("startDate");
                break;

            case TAG:
                this.globalEventID = json.getInt("eventId");
                this.globalAttendeeID = json.getInt("attendeeId");
                this.tag = json.getString("tag");
                this.action = json.getBoolean("action");
                break;
        }
    }
    public boolean getAction() {
        return action;
    }
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getGlobalEventID() {
        return globalEventID;
    }

    public int getGlobalAttendeeID() {
        return globalAttendeeID;
    }

    public String getTag() {
        return tag;
    }

    public Event convertToEvent() {
        Event event = new Event();
        event.setGlobalId(this.globalEventID);
        event.setName(this.name);
        event.setLocation(this.location);
        event.setDate(this.date);

        return event;
    }
}
