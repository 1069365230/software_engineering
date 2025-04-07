package edu.ems.notificationservice.repositories;

import edu.ems.notificationservice.models.EMSUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EMSUserRepository extends CrudRepository<EMSUser, Long> {
    EMSUser findEMSUserByEmail(String email);
}
