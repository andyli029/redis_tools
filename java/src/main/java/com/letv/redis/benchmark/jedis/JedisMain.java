package com.letv.redis.benchmark.jedis;
import com.letv.redis.benchmark.common.Constants;
import com.letv.redis.benchmark.common.StringGenerator;
import redis.clients.jedis.*;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class JedisMain {
    public static void main(String[] args) throws Exception {

        new Cli(args).parse();

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Cli.connCount);

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        
        if(Cli.enablestentinel){
        	HashSet<String> sentinels = new HashSet<String>();
        	if(Cli.sentinels.contains(",")){
        		String[] hostandport = Cli.sentinels.split(",");
            	
            	for(int i = 0; i < hostandport.length; i++)
            	{
            		sentinels.add(hostandport[i]);
            	}
            	
        	}
        	else{
        		sentinels.add(Cli.sentinels);
        	}
        	JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster", sentinels, config, Cli.opTimeout);
        	
            if (Cli.operation.equals("set")) {
            	System.out.println("JedisMain setkey startup");

                CyclicBarrier barrier = new CyclicBarrier(Cli.threadCount + 1);
                ArrayList<Thread> threadList = new ArrayList<>();

                for (int i = 0; i < Cli.threadCount; i++) {
                    threadList.add(new WriteThread(sentinelPool, barrier, i));
                }

                for (int j = 0; j < threadList.size(); j++) {
                    threadList.get(j).start();
                }

                barrier.await();
                long startTime = System.nanoTime();
                barrier.await();
                long estimatedTime = System.nanoTime() - startTime;

                float totalRepeat = Cli.repeatCount * Cli.threadCount;

                System.out.println("JedisMain setkey finish, cost time = "
                        + estimatedTime + "ns, " + "set count = " + totalRepeat
                        + ", ops = " + (totalRepeat / estimatedTime)
                        * Constants.seed);

                long avgSetCost = 0;
                long maxSetCost = Long.MIN_VALUE;
                long minSetCost = Long.MAX_VALUE;
                long sumSetCost = 0;
                Map<String, Long> costMap;

                for (int m = 0; m < threadList.size(); m++) {
                    costMap = ((WriteThread) threadList.get(m))
                            .getCostMapPerThread();
                    sumSetCost = sumSetCost + costMap.get("avgSetCostPerThread");
                    avgSetCost = sumSetCost / (m + 1);
                    maxSetCost = maxSetCost > costMap.get("maxSetCostPerThread") ? maxSetCost
                            : costMap.get("maxSetCostPerThread");
                    minSetCost = minSetCost < costMap.get("minSetCostPerThread") ? minSetCost
                            : costMap.get("minSetCostPerThread");
                }

                System.out.println("avg get cost time = " + avgSetCost + "ns");
                System.out.println("max get cost time = " + maxSetCost + "ns");
                System.out.println("min get cost time = " + minSetCost + "ns");

            } else if (Cli.operation.equals("get")) {
                System.out.println("JedisMain getkey startup");

                CyclicBarrier barrier = new CyclicBarrier(Cli.threadCount + 1);
                ArrayList<Thread> threadList = new ArrayList<>();

                for (int i = 0; i < Cli.threadCount; i++) {
                    threadList.add(new ReadThread(sentinelPool, barrier, i));
                }

                for (int j = 0; j < threadList.size(); j++) {
                    threadList.get(j).start();
                }

                barrier.await();
                long startTime = System.nanoTime();
                barrier.await();
                long estimatedTime = System.nanoTime() - startTime;

                float totalRepeat = Cli.repeatCount * Cli.threadCount;

                System.out.println("JedisMain getkey finish, cost time = "
                        + estimatedTime + "ns, " + "get count = " + totalRepeat
                        + ", ops = " + (totalRepeat / estimatedTime)
                        * Constants.seed);

                long avgGetCost = 0;
                long maxGetCost = Long.MIN_VALUE;
                long minGetCost = Long.MAX_VALUE;
                long sumGetCost = 0;
                Map<String, Long> costMap;

                for (int m = 0; m < threadList.size(); m++) {
                    costMap = ((ReadThread) threadList.get(m))
                            .getCostMapPerThread();
                    sumGetCost = sumGetCost + costMap.get("avgGetCostPerThread");
                    avgGetCost = sumGetCost / (m + 1);
                    maxGetCost = maxGetCost > costMap.get("maxGetCostPerThread") ? maxGetCost
                            : costMap.get("maxGetCostPerThread");
                    minGetCost = minGetCost < costMap.get("minGetCostPerThread") ? minGetCost
                            : costMap.get("minGetCostPerThread");
                }

                System.out.println("avg get cost time = " + avgGetCost + "ns");
                System.out.println("max get cost time = " + maxGetCost + "ns");
                System.out.println("min get cost time = " + minGetCost + "ns");

            }

            sentinelPool.close();
        }
        
        else if (Cli.enableCluster) {
            Set<HostAndPort> jedisClusterNodes = new HashSet<>();
            jedisClusterNodes.add(new HostAndPort(Cli.host, Integer.valueOf(Cli.port)));
            JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes, Cli.opTimeout, config);

            if (Cli.operation.equals("set")) {
            	 System.out.println("JedisMain setkey startup");

                 CyclicBarrier barrier = new CyclicBarrier(Cli.threadCount + 1);
                 ArrayList<Thread> threadList = new ArrayList<>();

                 for (int i = 0; i < Cli.threadCount; i++) {
                     threadList.add(new WriteThread(jedisCluster, barrier, i));
                 }

                 for (int j = 0; j < threadList.size(); j++) {
                     threadList.get(j).start();
                 }

                 barrier.await();
                 long startTime = System.nanoTime();
                 barrier.await();
                 long estimatedTime = System.nanoTime() - startTime;

                 float totalRepeat = Cli.repeatCount * Cli.threadCount;

                 System.out.println("JedisMain setkey finish, cost time = "
                         + estimatedTime + "ns, " + "set count = " + totalRepeat
                         + ", ops = " + (totalRepeat / estimatedTime)
                         * Constants.seed);

                 long avgSetCost = 0;
                 long maxSetCost = Long.MIN_VALUE;
                 long minSetCost = Long.MAX_VALUE;
                 long sumSetCost = 0;
                 Map<String, Long> costMap;

                 for (int m = 0; m < threadList.size(); m++) {
                     costMap = ((WriteThread) threadList.get(m))
                             .getCostMapPerThread();
                     sumSetCost = sumSetCost + costMap.get("avgSetCostPerThread");
                     avgSetCost = sumSetCost / (m + 1);
                     maxSetCost = maxSetCost > costMap.get("maxSetCostPerThread") ? maxSetCost
                             : costMap.get("maxSetCostPerThread");
                     minSetCost = minSetCost < costMap.get("minSetCostPerThread") ? minSetCost
                             : costMap.get("minSetCostPerThread");
                 }

                 System.out.println("avg set cost time = " + avgSetCost + "ns");
                 System.out.println("max set cost time = " + maxSetCost + "ns");
                 System.out.println("min set cost time = " + minSetCost + "ns");

            } else if (Cli.operation.equals("get")) {
                System.out.println("JedisMain getkey startup");

                CyclicBarrier barrier = new CyclicBarrier(Cli.threadCount + 1);
                ArrayList<Thread> threadList = new ArrayList<>();

                for (int i = 0; i < Cli.threadCount; i++) {
                    threadList.add(new ReadThread(jedisCluster, barrier, i));
                }

                for (int j = 0; j < threadList.size(); j++) {
                    threadList.get(j).start();
                }

                barrier.await();
                long startTime = System.nanoTime();
                barrier.await();
                long estimatedTime = System.nanoTime() - startTime;

                float totalRepeat = Cli.repeatCount * Cli.threadCount;

                System.out.println("JedisMain getkey finish, cost time = "
                        + estimatedTime + "ns, " + "get count = " + totalRepeat
                        + ", ops = " + (totalRepeat / estimatedTime)
                        * Constants.seed);

                long avgGetCost = 0;
                long maxGetCost = Long.MIN_VALUE;
                long minGetCost = Long.MAX_VALUE;
                long sumGetCost = 0;
                Map<String, Long> costMap;

                for (int m = 0; m < threadList.size(); m++) {
                    costMap = ((ReadThread) threadList.get(m))
                            .getCostMapPerThread();
                    sumGetCost = sumGetCost + costMap.get("avgGetCostPerThread");
                    avgGetCost = sumGetCost / (m + 1);
                    maxGetCost = maxGetCost > costMap.get("maxGetCostPerThread") ? maxGetCost
                            : costMap.get("maxGetCostPerThread");
                    minGetCost = minGetCost < costMap.get("minGetCostPerThread") ? minGetCost
                            : costMap.get("minGetCostPerThread");
                }

                System.out.println("avg get cost time = " + avgGetCost + "ns");
                System.out.println("max get cost time = " + maxGetCost + "ns");
                System.out.println("min get cost time = " + minGetCost + "ns");

            }

            jedisCluster.close();

        } else {
            JedisPool pool = new JedisPool(config, Cli.host, Integer.valueOf(Cli.port), Cli.opTimeout);
            if (Cli.operation.equals("set")) {
            	System.out.println("JedisMain setkey startup");

                CyclicBarrier barrier = new CyclicBarrier(Cli.threadCount + 1);
                ArrayList<Thread> threadList = new ArrayList<>();

                for (int i = 0; i < Cli.threadCount; i++) {
                    threadList.add(new WriteThread(pool, barrier, i));
                }

                for (int j = 0; j < threadList.size(); j++) {
                    threadList.get(j).start();
                }

                barrier.await();
                long startTime = System.nanoTime();
                barrier.await();
                long estimatedTime = System.nanoTime() - startTime;

                float totalRepeat = Cli.repeatCount * Cli.threadCount;

                System.out.println("JedisMain setkey finish, cost time = "
                        + estimatedTime + "ns, " + "set count = " + totalRepeat
                        + ", ops = " + (totalRepeat / estimatedTime)
                        * Constants.seed);

                long avgSetCost = 0;
                long maxSetCost = Long.MIN_VALUE;
                long minSetCost = Long.MAX_VALUE;
                long sumSetCost = 0;
                Map<String, Long> costMap;

                for (int m = 0; m < threadList.size(); m++) {
                    costMap = ((WriteThread) threadList.get(m))
                            .getCostMapPerThread();
                    sumSetCost = sumSetCost + costMap.get("avgSetCostPerThread");
                    avgSetCost = sumSetCost / (m + 1);
                    maxSetCost = maxSetCost > costMap.get("maxSetCostPerThread") ? maxSetCost
                            : costMap.get("maxSetCostPerThread");
                    minSetCost = minSetCost < costMap.get("minSetCostPerThread") ? minSetCost
                            : costMap.get("minSetCostPerThread");
                }

                System.out.println("avg get cost time = " + avgSetCost + "ns");
                System.out.println("max get cost time = " + maxSetCost + "ns");
                System.out.println("min get cost time = " + minSetCost + "ns");

            } else if (Cli.operation.equals("get")) {
                System.out.println("JedisMain getkey startup");

                CyclicBarrier barrier = new CyclicBarrier(Cli.threadCount + 1);
                ArrayList<Thread> threadList = new ArrayList<>();

                for (int i = 0; i < Cli.threadCount; i++) {
                    threadList.add(new ReadThread(pool, barrier, i));
                }

                for (int j = 0; j < threadList.size(); j++) {
                    threadList.get(j).start();
                }

                barrier.await();
                long startTime = System.nanoTime();
                barrier.await();
                long estimatedTime = System.nanoTime() - startTime;

                float totalRepeat = Cli.repeatCount * Cli.threadCount;

                System.out.println("JedisMain getkey finish, cost time = "
                        + estimatedTime + "ns, " + "get count = " + totalRepeat
                        + ", ops = " + (totalRepeat / estimatedTime)
                        * Constants.seed);

                long avgGetCost = 0;
                long maxGetCost = Long.MIN_VALUE;
                long minGetCost = Long.MAX_VALUE;
                long sumGetCost = 0;
                Map<String, Long> costMap;

                for (int m = 0; m < threadList.size(); m++) {
                    costMap = ((ReadThread) threadList.get(m))
                            .getCostMapPerThread();
                    sumGetCost = sumGetCost + costMap.get("avgGetCostPerThread");
                    avgGetCost = sumGetCost / (m + 1);
                    maxGetCost = maxGetCost > costMap.get("maxGetCostPerThread") ? maxGetCost
                            : costMap.get("maxGetCostPerThread");
                    minGetCost = minGetCost < costMap.get("minGetCostPerThread") ? minGetCost
                            : costMap.get("minGetCostPerThread");
                }

                System.out.println("avg get cost time = " + avgGetCost + "ns");
                System.out.println("max get cost time = " + maxGetCost + "ns");
                System.out.println("min get cost time = " + minGetCost + "ns");

            }

            pool.close();

        }
    }
}
