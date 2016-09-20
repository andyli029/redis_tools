package com.letv.redis.benchmark.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

public class ReadThread extends Thread {

    protected JedisPool pool;
    protected JedisCluster jedisCluster;
    protected JedisSentinelPool sentinelPool;
    protected CyclicBarrier barrier;
    private Map<String, Long> costMapPerThread;
    private int index;

    public ReadThread(JedisPool pool, CyclicBarrier barrier, int index) {
        this.pool = pool;
        this.barrier = barrier;
        this.index = index;
    }

    public ReadThread(JedisCluster jedisCluster, CyclicBarrier barrier, int index) {
        this.jedisCluster = jedisCluster;
        this.barrier = barrier;
        this.index = index;
    }

    public ReadThread(JedisSentinelPool pool, CyclicBarrier barrier, int index) {
        this.sentinelPool = pool;
        this.barrier = barrier;
        this.index = index;
    }

    public void run() {

        try {
            barrier.await();
            if (Cli.enablestentinel) {
                this.setCostMapPerThread(getKey(sentinelPool, Cli.repeatCount, Cli.key_prefix));
            } else if (Cli.enableCluster) {
                this.setCostMapPerThread(getKey(jedisCluster, Cli.repeatCount, Cli.key_prefix));
            } else {
                this.setCostMapPerThread(getKey(pool, Cli.repeatCount, Cli.key_prefix));
            }

            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Long> getKey(JedisSentinelPool pool, int repeats, String key_prefix) {
        Jedis jedis = sentinelPool.getResource();
        Thread thread = Thread.currentThread();
        // long thread_id = thread.getId();
        long avgGetCostPerThread = 0;
        long maxGetCostPerThread = Long.MIN_VALUE;
        long minGetCostPerThread = Long.MAX_VALUE;
        long sumGetCostPerThread = 0;
        Map<String, Long> costMapPerThread = new HashMap<>();
        int begin = repeats * index + 1;
        int end = repeats * (index + 1);
        for (int i = begin; i <= end; i++) {
            String key = key_prefix + i;
            // for (int i = 1; i <= repeats; i++) {
            // String key = "redis-check-noc-" + thread_id + i;
            long startTime = System.nanoTime();
            if (Cli.passwd != null)
                jedis.auth(Cli.passwd);
            jedis.get(key);
            long estimatedTime = System.nanoTime() - startTime;

            sumGetCostPerThread = sumGetCostPerThread + estimatedTime;
            avgGetCostPerThread = sumGetCostPerThread / i;
            maxGetCostPerThread = maxGetCostPerThread > estimatedTime ? maxGetCostPerThread : estimatedTime;
            minGetCostPerThread = minGetCostPerThread < estimatedTime ? minGetCostPerThread : estimatedTime;

        }

        if (jedis != null) {
            pool.returnResource(jedis);
        }

        costMapPerThread.put("avgGetCostPerThread", avgGetCostPerThread);
        costMapPerThread.put("maxGetCostPerThread", maxGetCostPerThread);
        costMapPerThread.put("minGetCostPerThread", minGetCostPerThread);

        return costMapPerThread;
    }

    private Map<String, Long> getKey(JedisPool pool, int repeats, String key_prefix) {

        Jedis jedis = pool.getResource();
        Thread thread = Thread.currentThread();
        // long thread_id = thread.getId();

        long avgGetCostPerThread = 0;
        long maxGetCostPerThread = Long.MIN_VALUE;
        long minGetCostPerThread = Long.MAX_VALUE;
        long sumGetCostPerThread = 0;
        Map<String, Long> costMapPerThread = new HashMap<>();
        int begin = repeats * index + 1;
        int end = repeats * (index + 1);
        for (int i = begin; i <= end; i++) {
            String key = key_prefix + i;
            // for (int i = 1; i <= repeats; i++) {
            // String key = "redis-check-noc-" + thread_id + i;
            long startTime = System.nanoTime();
            if (Cli.passwd != null)
                jedis.auth(Cli.passwd);
            jedis.get(key);
            long estimatedTime = System.nanoTime() - startTime;

            sumGetCostPerThread = sumGetCostPerThread + estimatedTime;
            avgGetCostPerThread = sumGetCostPerThread / i;
            maxGetCostPerThread = maxGetCostPerThread > estimatedTime ? maxGetCostPerThread : estimatedTime;
            minGetCostPerThread = minGetCostPerThread < estimatedTime ? minGetCostPerThread : estimatedTime;

        }

        if (jedis != null) {
            pool.returnResource(jedis);
        }

        costMapPerThread.put("avgGetCostPerThread", avgGetCostPerThread);
        costMapPerThread.put("maxGetCostPerThread", maxGetCostPerThread);
        costMapPerThread.put("minGetCostPerThread", minGetCostPerThread);

        return costMapPerThread;
    }

    private Map<String, Long> getKey(JedisCluster jedisCluster, int repeats, String key_prefix) {

        long avgGetCostPerThread = 0;
        long maxGetCostPerThread = Long.MIN_VALUE;
        long minGetCostPerThread = Long.MAX_VALUE;
        long sumGetCostPerThread = 0;
        Map<String, Long> costMapPerThread = new HashMap<>();
        int begin = repeats * index + 1;
        int end = repeats * (index + 1);
        for (int i = begin; i <= end; i++) {
            String key = key_prefix + i;
            // for (int i = 1; i <= repeats; i++) {
            // String key = "redis-check-noc-" + i;
            long startTime = System.nanoTime();
            jedisCluster.get(key);
            long estimatedTime = System.nanoTime() - startTime;

            sumGetCostPerThread = sumGetCostPerThread + estimatedTime;
            avgGetCostPerThread = sumGetCostPerThread / i;
            maxGetCostPerThread = maxGetCostPerThread > estimatedTime ? maxGetCostPerThread : estimatedTime;
            minGetCostPerThread = minGetCostPerThread < estimatedTime ? minGetCostPerThread : estimatedTime;

        }

        costMapPerThread.put("avgGetCostPerThread", avgGetCostPerThread);
        costMapPerThread.put("maxGetCostPerThread", maxGetCostPerThread);
        costMapPerThread.put("minGetCostPerThread", minGetCostPerThread);

        return costMapPerThread;
    }

    public Map<String, Long> getCostMapPerThread() {
        return costMapPerThread;
    }

    public void setCostMapPerThread(Map<String, Long> costMapPerThread) {
        this.costMapPerThread = costMapPerThread;
    }

}
