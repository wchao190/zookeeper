package com.atguigu.zk.case1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ZK 集群客户端代码
 */
public class DistributeClient {
    private static String connectString = "192.168.81.128:2181,192.168.81.129:2181,192.168.81.130:2181";
    private static int sessionTimeout = 2000000;
    private ZooKeeper zk =null;
    private String parentNode = "/servers";
    //创建到 ZK 的客户端连接
    public void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                try {
                    getServerList();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //获取 ZK 服务器列表信息
    public void getServerList() throws KeeperException, InterruptedException {
        // 1.获取 /servers 节点下的信息，并监听该节点
        List<String> children = zk.getChildren(parentNode, true);
        // 2.存储服务器信息列表
        ArrayList<String> servers = new ArrayList<String>();
        // 3.遍历所有节点，获取节点中的主机名称信息
        for (String child : children){
            //获取父节点下的子节点信息，不监听
            byte[] data = zk.getData(parentNode + "/" + child, false, null);
            servers.add(new String(data));
        }
        System.out.println(servers);
    }
    //业务功能
    public void bussiness() throws InterruptedException {
        System.out.println("client is working...");
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws Exception {
        // 1.获取 zk 连接
        DistributeClient client = new DistributeClient();
        client.getConnect();
        // 2.获取 servers 的子节点信息,从中获取服务器信息列表
        client.getServerList();
        // 3.业务进程启动
        client.bussiness();
    }
}
