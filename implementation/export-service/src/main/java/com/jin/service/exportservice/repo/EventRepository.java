package com.jin.service.exportservice.repo;

import com.jin.service.exportservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("SELECT e FROM Event e WHERE e.globalId = :uid")
    Event findAllByGlobalEventId(int uid);

}
