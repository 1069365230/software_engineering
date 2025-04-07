package com.ase.recommenderservice.model.dto.incoming;

public class BookmarkDTO extends EventInteraction {

    public BookmarkDTO(Long attendeeId, Long eventId, boolean active) {
        super(attendeeId, eventId, active);
    }
}
