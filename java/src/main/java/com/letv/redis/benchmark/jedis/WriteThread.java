package com.letv.redis.benchmark.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import com.letv.redis.benchmark.common.StringGenerator;

public class WriteThread extends Thread {

    protected JedisPool pool;
    protected JedisCluster jedisCluster;
    protected JedisSentinelPool jedispool;
    protected CyclicBarrier barrier;
    private Map<String, Long> costMapPerThread;
    private int index;

    public WriteThread(JedisPool pool, CyclicBarrier barrier, int index) {
        this.pool = pool;
        this.barrier = barrier;
        this.index = index;
    }

    public WriteThread(JedisCluster jedisCluster, CyclicBarrier barrier, int index) {
        this.jedisCluster = jedisCluster;
        this.barrier = barrier;
        this.index = index;
    }

    public WriteThread(JedisSentinelPool pool, CyclicBarrier barrier, int index) {
        this.jedispool = pool;
        this.barrier = barrier;
        this.index = index;
    }

    public void run() {

        try {
            barrier.await();

            if(Cli.enablestentinel){
                this.setCostMapPerThread(setKey(jedispool, Cli.repeatCount, Cli.value_bytes, Cli.key_prefix));
            }
            else if (Cli.enableCluster) {
                this.setCostMapPerThread(setKey(jedisCluster, Cli.repeatCount, Cli.value_bytes, Cli.key_prefix));
            } else {
                this.setCostMapPerThread(setKey(pool, Cli.repeatCount, Cli.value_bytes, Cli.key_prefix));
            }

            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Map<String, Long> setKey(JedisSentinelPool pool,
            int repeats, int value_bytes, String key_prefix) {

        Jedis jedis = pool.getResource();

        long avgSetCostPerThread = 0;
        long maxSetCostPerThread = Long.MIN_VALUE;
        long minSetCostPerThread = Long.MAX_VALUE;
        long sumSetCostPerThread = 0;
        Map<String, Long> costMapPerThread = new HashMap<>();
        //int end = (repeats / threadcount) * (index + 1);
        //int begin = (repeats / threadcount) * index;
        int begin = repeats * index + 1;
        int end = repeats * (index + 1);
        for (int i = begin; i <= end; i++) {
            String key = key_prefix + i;
            // String key = StringGenerator.RandomString(key_bytes);
            long startTime = System.nanoTime();
            String value =StringGenerator.RandomString(value_bytes);
//            if(Cli.passwd != null)
//                jedis.auth(Cli.passwd);
            jedis.set(key,value);
            long estimatedTime = System.nanoTime() - startTime;

            sumSetCostPerThread = sumSetCostPerThread + estimatedTime;
            avgSetCostPerThread = sumSetCostPerThread / i;
            maxSetCostPerThread = maxSetCostPerThread > estimatedTime ? maxSetCostPerThread
                    : estimatedTime;
            minSetCostPerThread = minSetCostPerThread < estimatedTime ? minSetCostPerThread
                    : estimatedTime;

        }

        if (jedis != null) {
            pool.returnResource(jedis);
        }

        costMapPerThread.put("avgSetCostPerThread", avgSetCostPerThread);
        costMapPerThread.put("maxSetCostPerThread", maxSetCostPerThread);
        costMapPerThread.put("minSetCostPerThread", minSetCostPerThread);

        return costMapPerThread;
    }

    private Map<String, Long> setKey(JedisPool pool,
            int repeats, int value_bytes, String key_prefix) {

        Jedis jedis = pool.getResource();

        long avgSetCostPerThread = 0;
        long maxSetCostPerThread = Long.MIN_VALUE;
        long minSetCostPerThread = Long.MAX_VALUE;
        long sumSetCostPerThread = 0;
        Map<String, Long> costMapPerThread = new HashMap<>();
        //        int end = (repeats / threadcount) * (index + 1);
        //        int begin = (repeats / threadcount) * index;
        int begin = repeats * index + 1;
        int end = repeats * (index + 1);
        for (int i = begin; i <= end; i++) {
            String key = key_prefix + i;
            // String key = StringGenerator.RandomString(key_bytes);
            long startTime = System.nanoTime();
            String value =StringGenerator.RandomString(value_bytes);

//          if(Cli.passwd != null)
//              jedis.auth(Cli.passwd);
            jedis.set(key,value);
            long estimatedTime = System.nanoTime() - startTime;

            sumSetCostPerThread = sumSetCostPerThread + estimatedTime;
            avgSetCostPerThread = sumSetCostPerThread / i;
            maxSetCostPerThread = maxSetCostPerThread > estimatedTime ? maxSetCostPerThread
                    : estimatedTime;
            minSetCostPerThread = minSetCostPerThread < estimatedTime ? minSetCostPerThread
                    : estimatedTime;

        }

        if (jedis != null) {
            pool.returnResource(jedis);
        }

        costMapPerThread.put("avgSetCostPerThread", avgSetCostPerThread);
        costMapPerThread.put("maxSetCostPerThread", maxSetCostPerThread);
        costMapPerThread.put("minSetCostPerThread", minSetCostPerThread);

        return costMapPerThread;
    }

    private Map<String, Long> setKey(JedisCluster jedisCluster,
            int repeats, int value_bytes, String key_prefix) {

        long avgSetCostPerThread = 0;
        long maxSetCostPerThread = Long.MIN_VALUE;
        long minSetCostPerThread = Long.MAX_VALUE;
        long sumSetCostPerThread = 0;
        Map<String, Long> costMapPerThread = new HashMap<>();
        int begin = repeats * index + 1;
        int end = repeats * (index + 1);
        for (int i = begin ; i <= end; i++) {
            // for (int i = 1; i <= repeats; i++) {
            String key = key_prefix + i;
            //String key = StringGenerator.RandomString(key_bytes);
            long startTime = System.nanoTime();
            String value =StringGenerator.RandomString(value_bytes);

            jedisCluster.set(key,value);
            long estimatedTime = System.nanoTime() - startTime;

            sumSetCostPerThread = sumSetCostPerThread + estimatedTime;
            avgSetCostPerThread = sumSetCostPerThread / i;
            maxSetCostPerThread = maxSetCostPerThread > estimatedTime ? maxSetCostPerThread
                    : estimatedTime;
            minSetCostPerThread = minSetCostPerThread < estimatedTime ? minSetCostPerThread
                    : estimatedTime;

        }

        costMapPerThread.put("avgSetCostPerThread", avgSetCostPerThread);
        costMapPerThread.put("maxSetCostPerThread", maxSetCostPerThread);
        costMapPerThread.put("minSetCostPerThread", minSetCostPerThread);

        return costMapPerThread;
    }

    public Map<String, Long> getCostMapPerThread() {
        return costMapPerThread;
    }

    public void setCostMapPerThread(Map<String, Long> costMapPerThread) {
        this.costMapPerThread = costMapPerThread;
    }

}
