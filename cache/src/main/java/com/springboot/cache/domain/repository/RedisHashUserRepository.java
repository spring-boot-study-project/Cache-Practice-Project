package com.springboot.cache.domain.repository;

import org.springframework.data.repository.CrudRepository;

import com.springboot.cache.domain.entity.RedisHashUser;

public interface RedisHashUserRepository extends CrudRepository<RedisHashUser, Long> {

}
