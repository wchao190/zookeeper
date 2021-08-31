package com.atguigu.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class zkClient {
    private  static String connecString = "192.168.81.128:2181,192.168.81.129:2181,192.168.81.130:2181";
    private static int sessionTimeout = 200000;
    private ZooKeeper zkClient = null;

    @Before
    @Test
    public void  init() throws IOException {
        zkClient = new ZooKeeper(connecString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                try {
                    List<String> children = zkClient.getChildren("/",true);
                    for (String child : children){
                        System.out.println(child);
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void create() throws KeeperException, InterruptedException {
        /* 参数 1：要创建的节点的路径； 参数 2：节点数据 ； 参数 3：节点权限 ；参数 4：节点的类型 */
        String nodeCreated = zkClient.create("/atguigu2","idea2".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
    }

    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        System.out.println("开始监听根节点变化！");
        // 监听 节点，每调用一次 zkClient，就注册一次监听
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children){
            System.out.println(child);
        }
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void exist() throws KeeperException, InterruptedException {
        Stat exists = zkClient.exists("/atguigu", false);
        System.out.println(exists ==null ? "atguigu is not exist!":"atguigu exist!");
    }
}
