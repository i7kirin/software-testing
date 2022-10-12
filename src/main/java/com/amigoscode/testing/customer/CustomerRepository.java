package com.amigoscode.testing.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    @Query(
            nativeQuery = true,
            value = "select id, name, phone_number " +
                    "from customer where phone_number = :phoneNumber"
    )
    Optional<Customer> getByPhoneNumber(String phoneNumber);

}
