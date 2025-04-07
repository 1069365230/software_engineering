package com.jin.service.exportservice.repo;

import com.jin.service.exportservice.model.BookmarkedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkedRepository extends JpaRepository<BookmarkedEvent, Integer> {
    @Query("SELECT e FROM BookmarkedEvent e WHERE e.globalAttendeeID = :uid")
    List<BookmarkedEvent> findAllByGlobalAttendeeId(int uid);

    @Query("SELECT e FROM BookmarkedEvent e WHERE e.globalAttendeeID = :uid AND e.globalEventID = :eid")
    Optional<BookmarkedEvent> findAllByGlobalAttendeeIdAndEventId(int uid, int eid);

}
