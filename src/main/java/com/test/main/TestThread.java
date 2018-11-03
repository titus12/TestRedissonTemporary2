package com.test.main;

import org.redisson.api.RLocalCachedMap;


public class TestThread implements Runnable{

    private boolean running;

    public TestThread(){
        running = true;
    }

    public void run() {
        while(running){
            try {

                int count = 100;
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
                    }catch (Exception e){
                        System.out.println("access :key-" + i);
                        e.printStackTrace();
                    }
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
