package com.smartpantry.repository;

import com.smartpantry.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    //Phone number se user find krne ke liye
    Optional<User> findByPhoneNumber(String phoneNumber);

    //Check krne ke liye ki phone already registered hai ya nhi
    boolean existsByPhoneNumber(String phoneNumber);


    // Find user by email
    Optional<User> findByEmail(String email);

    // Check whether email is already registered
    boolean existsByEmail(String email);


}
