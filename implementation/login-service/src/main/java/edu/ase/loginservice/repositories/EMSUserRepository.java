package edu.ase.loginservice.repositories;

import edu.ase.loginservice.models.EMSUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EMSUserRepository extends CrudRepository<EMSUser, Long> {
    EMSUser findEMSUserByUsername(String username);

    EMSUser findEMSUserByUsernameAndPassword(String username, String password);
}
