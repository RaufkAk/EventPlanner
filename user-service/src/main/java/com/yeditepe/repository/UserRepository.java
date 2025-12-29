package com.yeditepe.repository;


import com.yeditepe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // Kayıt sırasında e-posta kontrolü yapmak için
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

}
