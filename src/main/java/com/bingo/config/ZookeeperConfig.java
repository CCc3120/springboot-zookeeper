package com.bingo.config;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ZookeeperConfig {

	@Autowired
	private Environment environment;

	@Bean
	public CuratorFramework curatorFramework() {
//		CuratorFramework curatorFramework = CuratorFrameworkFactory
//				.newClient(environment.getProperty("zk.host"), 5000, 3000, new ExponentialBackoffRetry(2000, 10));
		CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
				.connectString(environment.getProperty("zk.host"))
				.namespace(environment.getProperty("zk.namespace"))
//				.sessionTimeoutMs(5000)
//				.connectionTimeoutMs(5000)
				// 重试策略
				.retryPolicy(new ExponentialBackoffRetry(2000, 10))
//				.retryPolicy(new RetryNTimes(5, 1000))
				.build();
		curatorFramework.start();
		return curatorFramework;
	}

//	@Bean
//	public ZooKeeper zooKeeper() {
//		ZooKeeper keeper = null;
//		try {
//			keeper = new ZooKeeper(environment.getProperty("zk.host"), 5000, (watchedEvent) -> {
//			});
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return keeper;
//	}

//	@Bean
//	public InterProcessMutex interProcessMutex() {
//		InterProcessMutex mutex = new InterProcessMutex(curatorFramework(), environment.getProperty("zk.lockPath"));
//		return mutex;
//	}
}
