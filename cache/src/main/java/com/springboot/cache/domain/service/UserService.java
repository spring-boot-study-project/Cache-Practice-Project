package com.springboot.cache.domain.service;

import java.time.Duration;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.springboot.cache.domain.entity.RedisHashUser;
import com.springboot.cache.domain.entity.User;
import com.springboot.cache.domain.repository.RedisHashUserRepository;
import com.springboot.cache.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import static com.springboot.cache.config.CacheConfig.CACHE1;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RedisHashUserRepository redisHashUserRepository;

    private final RedisTemplate<String, User> userRedisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    // 캐시 적용 방법 redisTemplate를 사용한 캐시 -> 캐시 적용 방법을 직접 제어할 수 있다. 간단한게 아니라면 이를 사용하는게 개발자한텐 더 좋다고 한다.
    public User getUser(final Long id) {
        var key = "users:%d".formatted(id);
        var cachedUser = objectRedisTemplate.opsForValue().get(key);
        if (cachedUser != null) {
            return (User) cachedUser;
        }
        User user = userRepository.findById(id).orElseThrow();
        objectRedisTemplate.opsForValue().set(key, user, Duration.ofSeconds(30));
        return user;
    }

    // Redis Hash를 사용해서 캐시 적용하는 방법 -> crudrepository를 사용해서 간단하게 캐시 적용 가능하다.
    public RedisHashUser getUser2(final Long id) {
        var cachedUser = redisHashUserRepository.findById(id).orElseGet(() -> {
            User user = userRepository.findById(id).orElseThrow();
            return redisHashUserRepository.save(RedisHashUser.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build());
        });
        return cachedUser;
    }

    // 추상화된 캐시 적용 -> 캐시를 내부적으로 어떻게 동작하는지 알고 있다면 추상화된 어노테이션을 사용하는 것이 좋다.
    @Cacheable(cacheNames = CACHE1, key = "'user:' + #id")
    public User getUser3(final Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
