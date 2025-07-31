package com.example.demo.Login.Repository;

import com.example.demo.Login.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>  {

}
