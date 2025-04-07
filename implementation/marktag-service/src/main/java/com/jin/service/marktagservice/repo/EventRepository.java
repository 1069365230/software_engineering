package com.jin.service.marktagservice.repo;

import com.jin.service.marktagservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository <Event, Integer>{
    @Query("SELECT e FROM Event e WHERE e.globalId = :gid")
    Optional<Event> findByGlobalId(int gid);

    @Query("SELECT e FROM Event e WHERE e.posted = true")
    Optional<List<Event>> findAllPostedEvent();

    @Query("SELECT e FROM Event e WHERE e.globalId = :gid AND e.posted = true")
    Optional<Event> findPostedEvent(int gid);
}
