package com.ase.eventinventoryservice.repository;

import com.ase.eventinventoryservice.model.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {
}
