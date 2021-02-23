package com.bingo.zk.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bingo.zk.model.Fruit;
import com.bingo.zk.service.IFruitService;

@RestController
@RequestMapping(value = "/zk")
public class FruitController {

	@Autowired
	private IFruitService fruitService;

	@Autowired
	private CuratorFramework curatorFramework;

	@Autowired
	private Environment environment;

	@RequestMapping("/getProduct")
	public Map getProduct(String fdId) {
		Map map = new HashMap();
		map.put("id", fdId);
		map.put("name", "你好");
		return map;
	}

	@RequestMapping(value = "/testlock")
	public String demo(String fdId) {
		String lockPath = environment.getProperty("zk.lockPath") + fdId;
		InterProcessMutex mutex = new InterProcessMutex(curatorFramework, lockPath);
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					pay(mutex, fdId);
				}
			}).start();
		}
		return "ok";
	}

	/**
	 * InterProcessMutex：分布式可重入排它锁 </br>
	 * InterProcessSemaphoreMutex：分布式排它锁 </br>
	 * InterProcessReadWriteLock：分布式读写锁</br>
	 * InterProcessMultiLock：将多个锁作为单个实体管理的容器</br>
	 */
	private void pay(InterProcessMutex mutex, String fdId) {
		try {
			// 获取锁资源
			if (mutex.acquire(20, TimeUnit.SECONDS)) {
				pay(fdId);
			}
		} catch (Exception e) {
			System.out.println("异常============");
			e.printStackTrace();
		} finally {
			try {
				mutex.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void pay(String fdId) {
		Fruit fruit = fruitService.findByPrimarykey(fdId);
		if (fruit.getFdNum() > 0) {
			fruit.setFdNum(fruit.getFdNum() - 1);
			fruitService.update(fruit);
			System.out.println(
					Thread.currentThread().getName() + "抢购成功，剩余" + fruit.getFdNum() + "件>>" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ">>>1");
		} else {
			System.out.println(Thread.currentThread().getName() + "抢购失败！！！" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ">>>1");
		}
	}

	private synchronized void paySynchronized(String fdId) {
		pay(fdId);
	}


	@RequestMapping(value = "/test")
	public String add() {
		for (int i = 0; i < 10; i++) {
			Fruit fruit = new Fruit();
			fruit.setFdName("苹果" + i);
			fruit.setFdNum(30 + i);
			fruit.setFdPrice(3.2 + i);
			fruitService.save(fruit);
		}
		return "ok";
	}

	@RequestMapping(value = "/testfind")
	public String test() {
		System.out.println("findByFdName==>>" + fruitService.findByFdName("苹果1").toString());
		System.out.println("findFirstByOrderByFdNum==>>" + fruitService.findFirstByOrderByFdNum());
		System.out.println("findTopByOrderByFdNumDesc==>>" + fruitService.findTopByOrderByFdNumDesc());
		System.out.println("findByFdNameOrFdNum==>>" + fruitService.findByFdNameOrFdNum("苹果", 2));
		System.out.println("findByFdNameAndFdNum==>>" + fruitService.findByFdNameAndFdNum("苹果", 30));
		System.out.println("findByFdNameLike==>>");
		fruitService.findByFdNameLike("苹果").forEach(f -> System.out.println(f.toString()));
		System.out.println("findByFdName%Like%==>>");
		fruitService.findByFdNameContaining("苹果").forEach(f -> System.out.println(f.toString()));
		System.out.println("countByFdName==>>" + fruitService.countByFdName("苹果1"));
		System.out.println("deleteByFdId==>>" + fruitService.deleteByFdId("4028820f73b2ec4c0173b2ed20720000"));
		return "ok";
	}

	public static void main(String[] args) {
		// 实例化Zookeeper
		// 创建重试连接的策略-最多重试连接10次
		ExponentialBackoffRetry backoffRetry = new ExponentialBackoffRetry(1000, 10);
		// 创建一个Zookeeper客户端
		CuratorFramework client = CuratorFrameworkFactory
				.builder()
				.connectString("127.0.0.1:2181")
				.namespace("kill-1")
				.retryPolicy(backoffRetry)
				.build();
		// 启动客户端
		client.start();
		String fdId = "";
		String lockPath = "/lock/" + fdId;

		InterProcessMutex mutex = new InterProcessMutex(client, lockPath);
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// 获取锁资源
						if (mutex.acquire(100, TimeUnit.SECONDS)) {
//							pay(fdId);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							mutex.release();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}
}
