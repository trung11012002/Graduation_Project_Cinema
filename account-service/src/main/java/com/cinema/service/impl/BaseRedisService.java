package com.cinema.service.impl;

import com.cinema.service.IBaseRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BaseRedisService<K, F, V> implements IBaseRedisService<K, F, V> {

    private final RedisTemplate<K, V> redisTemplate;
    private final HashOperations<K, F, V> hashOperations;

    @Override
    public void set(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setTimeToLive(K key, long timeoutInHours) {
        redisTemplate.expire(key, timeoutInHours, TimeUnit.MINUTES);
    }

    @Override
    public void hashSet(K key, F field, V value) {
        hashOperations.put(key, field, value);
    }
    //
    //    @Override
    //    public boolean hashExists(String key, String field) {
    //        return hashOperations.hasKey(key,field);
    //    }
    //
    @Override
    public Object get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(K key) {
        redisTemplate.delete(key);
    }
    //
    //    @Override
    //    public Map<String, Object> getField(String key) {
    //        return hashOperations.entries(key);
    //    }
    //
    //    @Override
    //    public Object hashGet(String key, String field) {
    //        return hashOperations.get(key,field);
    //    }
    //
    //    @Override
    //    public List<Object> hashGetByFieldPrefix(String key, String filedPrefix) {
    //        List<Object> objects = new ArrayList<>();
    //        Map<String,Object> hashEntries = hashOperations.entries(key);
    //
    //        for(Map.Entry<String,Object> entry: hashEntries.entrySet()){
    //            if(entry.getKey().startsWith(filedPrefix)){
    //                objects.add(entry.getValue());
    //            }
    //        }
    //        return objects;
    //    }
    //
    //    @Override
    //    public Set<String> getFieldPrefixes(String key) {
    //        return hashOperations.entries(key).keySet();
    //    }
    //

    //
    //    @Override
    //    public void delete(String key, String field) {
    //        hashOperations.delete(key,field);
    //    }
    //
    //    @Override
    //    public void delete(String key, List<String> fields) {
    //        for(String field: fields){
    //            hashOperations.delete(key,field);
    //        }
    //    }
}
