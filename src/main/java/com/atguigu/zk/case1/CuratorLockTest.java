package com.atguigu.zk.case1;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

import java.security.PrivateKey;

public class CuratorLockTest {

    private String rootNode = "/locks";
    private String connectString = "192.168.81.128:2181,192.168.81.129:2181,192.168.81.130:2181";
    // connection 超时时间
    private int connectTimeout = 10000;
    // session 超时时间
    private int sessionTimeout = 10000;

    // 分布式锁初始化
    public CuratorFramework getCuratorFrameWork(){
        // 重试策略，初始时间 3秒，重试 3次
        ExponentialBackoffRetry policy = new ExponentialBackoffRetry(3000,3);
        // 通过 工厂 创建 Curator
        CuratorFramework cient = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .connectionTimeoutMs(connectTimeout)
                .sessionTimeoutMs(connectTimeout)
                .retryPolicy(policy).build();
        // 开启连接
        cient.start();
        System.out.println("zookeeper 初始化完成....");
        return cient;
    }

    // 测试 多线程 加锁 解锁
    public void test(){
        //创建分布式锁1
        final InterProcessMutex lock1 = new InterProcessMutex(getCuratorFrameWork(), rootNode);
        //创建分布式锁2
        final InterProcessMutex lock2 = new InterProcessMutex(getCuratorFrameWork(), rootNode);
        // 创建线程
        new Thread(new Runnable() {
            public void run() {
                // 获取 锁 对象
                try {
                    lock1.acquire();
                    System.out.println("线程1 获取锁...");
                    // 测试 锁 重入
                    lock1.acquire();
                    System.out.println("线程1 再次获取锁...");
                    Thread.sleep(5*1000);
                    // 释放锁
                    lock1.release();
                    System.out.println("线程1 释放锁...");
                    lock1.release();
                    System.out.println("线程1 再次释放锁...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                try {
                    lock2.acquire();
                    System.out.println("线程2 获取锁...");
                    lock2.acquire();
                    System.out.println("线程2 再次获取锁...");
                    Thread.sleep(5*1000);
                    lock2.release();
                    System.out.println("线程2 释放锁...");
                    lock2.release();
                    System.out.println("线程2 再次释放锁...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        new CuratorLockTest().test();
    }
}
