package com.ase.recommenderservice.model.dto.incoming;

public class EventBookingDTO extends EventInteraction {

    public EventBookingDTO(Long attendeeId, Long eventId, boolean active) {
        super(attendeeId, eventId, active);
    }


}
