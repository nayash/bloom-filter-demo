package com.outliers.bloomfilter.repositories;

import com.outliers.bloomfilter.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u.username FROM User u")
    List<String> findAllUsernames();
}
