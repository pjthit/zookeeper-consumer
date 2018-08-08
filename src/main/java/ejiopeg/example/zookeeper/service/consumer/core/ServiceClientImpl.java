package ejiopeg.example.zookeeper.service.consumer.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceClientImpl {
	
	public volatile List<String> servers = null;
	private ZooKeeper zk;
	private static final int TIMEOUT = 5000;
	private static final String SERVICE_PATH = "/registry/HelloService";
	
	@Value("${registry.servers}")
	private String zkServers;
	
	public ServiceClientImpl() {
		
	}
	
	@PostConstruct
	public void init() {
		try {
			zk = new ZooKeeper(zkServers, TIMEOUT, new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					// TODO Auto-generated method stub
					if (event.getType() == EventType.None) {
						return;
					}
					updateServers();
				}
				
			});
			System.out.println("connected to zookeeper");
			updateServers();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateServers() {
		try {
			List<String> children = zk.getChildren(SERVICE_PATH, true);
			ArrayList<String> serverList = new ArrayList<String>();
			System.out.println("service list update");
			for (String child: children) {
				byte[] data = zk.getData(SERVICE_PATH + "/" + child, false, null);
				serverList.add(new String(data));
				System.out.println(child);
			}
			servers = serverList;
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getServer() {
		int size = servers.size();
		int num = new Random().nextInt(size);	
		return servers.get(num);
	}

}
