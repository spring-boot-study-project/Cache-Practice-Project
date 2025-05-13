package com.springboot.cache.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.cache.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
