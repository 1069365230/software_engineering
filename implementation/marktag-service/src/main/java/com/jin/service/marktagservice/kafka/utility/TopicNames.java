package com.jin.service.marktagservice.kafka.utility;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TopicNames {
    //topic names handles all the key for kafka topics
    //keep it at one place makes it easier to modify
    public static final String ATTENDEE_TOPIC = "user.management";
    public static final String ATTENDANCE_TOPIC = "attendee.event-booking";
    public static final String ATTENDANCE_TOPIC_EXTRA = "attendee.event-tag.attendance";
    public static final String INVENTORY_TOPIC = "event.inventory";
    public static final String TAG_TOPIC = "attendee.event-tag";
    public static final String BOOKMARK_TOPIC = "attendee.bookmark";
}
