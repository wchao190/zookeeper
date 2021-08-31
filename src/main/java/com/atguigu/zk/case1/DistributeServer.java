package com.atguigu.zk.case1;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * ZK 集群 服务端代码
 */
public class DistributeServer {
    //连接的服务器 ip:port
    private static String connectString = "192.168.81.128:2181,192.168.81.129:2181,192.168.81.130:2181";
    //最大连接时长
    private static int sessionTimeout = 100000;
    //集群对象
    private ZooKeeper zk = null;
    //定义 服务器在 ZK 集群中的 父节点
    private String parentNode="/servers/";

    //创建到 ZK 服务器连接
    public void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

    //注册服务器，就是创建服务器节点
    public void reisterSerer(String hostName) throws KeeperException, InterruptedException {

        String create = zk.create(parentNode + "server", hostName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostName + "is online" +create);
    }

    //业务功能
    public void business(String hostName) throws InterruptedException {
        System.out.println(hostName + "is working...");
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws Exception {
        // 1.获取 ZK 连接
        DistributeServer server = new DistributeServer();
        server.getConnect();
        // 2.利用 ZK 连接注册服务器信息
        server.reisterSerer(args[0]);
        // 3.启动业务功能
        server.business(args[0]);
    }
}
