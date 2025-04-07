package com.jin.service.marktagservice.repo;

import com.jin.service.marktagservice.model.Attendee;
import com.jin.service.marktagservice.model.Event;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendeeRepository extends JpaRepository <Attendee, Integer>{
    @Query("SELECT a FROM Attendee a WHERE a.globalId = :gid")
    Optional<Attendee> findByGlobalId(int gid);
}
