package at.univie.davidreichert.analyticsandreport.dto.incoming;

public class EventBookmarkDTO {

    private int attendeeId;

    private int eventId;

    private boolean action;

    public EventBookmarkDTO(int attendeeId, int eventId) {
        this.attendeeId = attendeeId;
        this.eventId = eventId;
    }

    public EventBookmarkDTO() {
    }


    public int getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(int attendeeId) {
        this.attendeeId = attendeeId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public boolean getAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }
}
