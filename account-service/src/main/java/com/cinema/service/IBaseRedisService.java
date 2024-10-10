package com.cinema.service;

public interface IBaseRedisService<K, F, V> {
    void set(K key, V value);

    void setTimeToLive(K key, long timeoutInHours);

    void hashSet(K key, F field, V value);

    //    boolean hashExists(String key, String field);

    Object get(K key);

    //    public Map<String,Object> getField(String key);
    //    Object hashGet(String key,String field);
    //    List<Object> hashGetByFieldPrefix(String key, String filedPrefix);
    //    Set<String> getFieldPrefixes(String key);
    void delete(K key);
    //    void delete(String key,String field);
    //    void delete(String key,List<String> fields);
}
