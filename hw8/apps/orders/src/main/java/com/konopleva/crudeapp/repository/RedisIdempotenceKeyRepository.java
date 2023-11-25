package com.konopleva.crudeapp.repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisIdempotenceKeyRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private ListOperations<String, Object> listOperations;

    @PostConstruct
    private void init(){
        listOperations = redisTemplate.opsForList();
    }

    public void add(String iKey) {
        listOperations.leftPush(iKey, iKey);
    }
    public boolean delete(String iKey) {
        var result = listOperations.leftPop(iKey);
        return result != null;
    }

}
