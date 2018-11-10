package com.test.main;

import org.redisson.api.RLocalCachedMap;


public class TestThread implements Runnable{

    private boolean running;

    public TestThread(){
        running = true;
    }
    private byte state = 0;
    private final static byte normal = 0;
    private final static byte timeout = 1;


    public void run() {
        while(running){
            try {
                switch (state) {
                    case normal:
                        int count = 100;
                        //创建0-49个RLocalCachedMap对象
                        for (int i = 0; i < count; i++) {
                            try {
                                String region = i + "";
                                String key = "key-" + i;
                                System.out.println("begin read region:" + region + ",key:" + key);
                                RLocalCachedMap<String, Long> rLocalCachedMap = Main.getLocalCachedMap(region);
                                Long v = rLocalCachedMap.get(key);
                                if (v != null) {
                                    System.out.println("end read region:" + region + ",key:" + key + ",V:" + v);
                                } else {
                                    System.out.println("write region:" + region + ",key:" + key + ",V:" + i);
                                    rLocalCachedMap.put(key, (long) i);
                                }
                            } catch (Exception e) {
                                state = timeout;
                                System.out.println("access :key-" + i);
                                //释放掉老的region为0的RLocalCachedMap,并等10秒
                                Main.releaseLocalCachedMap("0");
                                e.printStackTrace();
                                Thread.sleep(10000);
                                break;
                            }
                        }
                        break;
                    case timeout:
                        String region = "50";
                        try {
                            //访问一个新的RLocalCachedMap
                            RLocalCachedMap<String, Long> rLocalCachedMap = Main.getLocalCachedMap(region);
                        }catch (Exception e){
                            System.out.println("state:timeout,access :key-" + region);
                            e.printStackTrace();
                        }
                        break;
                }
                Thread.sleep(100);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void stop()
    {
        running = false;
    }
}
