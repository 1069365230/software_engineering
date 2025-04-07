package com.jin.service.exportservice.repo;

import com.jin.service.exportservice.model.AttendingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendingRepository extends JpaRepository<AttendingEvent, Integer> {
    @Query("SELECT e FROM AttendingEvent e WHERE e.globalAttendeeID = :uid")
    List<AttendingEvent> findAllByGlobalAttendeeId(int uid);

    @Query("SELECT e FROM AttendingEvent e WHERE e.globalAttendeeID = :uid AND e.globalEventID = :eid")
    Optional<AttendingEvent> findAllByGlobalAttendeeIdAndEventId(int uid, int eid);
}
