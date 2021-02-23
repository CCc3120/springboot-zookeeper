package com.bingo.guava;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FinalData {

	public static void main(String[] args) throws Exception {
		System.out.println("----------Test allocate--------");
		System.out.println("before alocate:"
				+ Runtime.getRuntime().freeMemory());

		// 如果分配的内存过小，调用Runtime.getRuntime().freeMemory()大小不会变化？
		// 要超过多少内存大小JVM才能感觉到？
		ByteBuffer buffer = ByteBuffer.allocate(102400);
		System.out.println("buffer = " + buffer);

		System.out.println("after alocate:"
				+ Runtime.getRuntime().freeMemory());

		// 这部分直接用的系统内存，所以对JVM的内存没有影响
		ByteBuffer directBuffer = ByteBuffer.allocateDirect(102400);
		System.out.println("directBuffer = " + directBuffer);
		System.out.println("after direct alocate:"
				+ Runtime.getRuntime().freeMemory());

		System.out.println("----------Test wrap--------");
		byte[] bytes = new byte[32];
		buffer = ByteBuffer.wrap(bytes);
		System.out.println(buffer);

		buffer = ByteBuffer.wrap(bytes, 10, 10);
		System.out.println(buffer);
	}

	private static void Selector() throws Exception {
		// 打开一个ServerSocketChannel
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
		// 绑定地址
		serverSocketChannel.bind(address);
		// 设置为非阻塞
		serverSocketChannel.configureBlocking(false);
		// 打开一个选择器
		Selector selector = Selector.open();
		// serverSocketChannel注册到选择器中,监听连接事件
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		// 循环等待客户端的连接
		while (true) {
			// 等待3秒，（返回0相当于没有事件）如果没有事件，则跳过
			if (selector.select(3000) == 0) {
				System.out.println("服务器等待3秒，没有连接");
				continue;
			}
			// 如果有事件selector.select(3000)>0的情况,获取事件
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			// 获取迭代器遍历
			Iterator<SelectionKey> it = selectionKeys.iterator();
			while (it.hasNext()) {
				// 获取到事件
				SelectionKey selectionKey = it.next();
				// 判断如果是连接事件
				if (selectionKey.isAcceptable()) {
					// 服务器与客户端建立连接，获取socketChannel
					SocketChannel socketChannel = serverSocketChannel.accept();
					// 设置成非阻塞
					socketChannel.configureBlocking(false);
					// 把socketChannel注册到selector中，监听读事件，并绑定一个缓冲区
					socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
				}
				// 如果是读事件
				if (selectionKey.isReadable()) {
					// 获取通道
					SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
					// 获取关联的ByteBuffer
					ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
					// 打印从客户端获取到的数据
					socketChannel.read(buffer);
					System.out.println("from 客户端：" + new String(buffer.array()));
					buffer.clear();
				}
				// 从事件集合中删除已处理的事件，防止重复处理
				it.remove();
			}
		}

	}


	// 直接缓冲区or非直接缓冲区
	private static void allocateAndallocateDirect() throws Exception {
		long starTime = System.currentTimeMillis();
		// 获取文件输入流
		File file = new File("G:\\Development\\Script.pvf");// 文件大小209MB

		FileInputStream inputStream = new FileInputStream(file);
		// 从文件输入流获取通道
		FileChannel inputStreamChannel = inputStream.getChannel();
		// 获取文件输出流
		FileOutputStream outputStream = new FileOutputStream(new File("G:\\Development\\Script1.pvf"));
		// 从文件输出流获取通道
		FileChannel outputStreamChannel = outputStream.getChannel();
		// 创建一个直接缓冲区 速度略快
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(5 * 1024 * 1024);
		// 创建一个非直接缓冲区
		// ByteBuffer byteBuffer = ByteBuffer.allocate(5 * 1024 * 1024);
		// 写入到缓冲区
		while (inputStreamChannel.read(byteBuffer) != -1) {
			// 切换读模式
			byteBuffer.flip();
			outputStreamChannel.write(byteBuffer);
			byteBuffer.clear();
		}
		// 关闭通道
		outputStream.close();
		inputStream.close();
		outputStreamChannel.close();
		inputStreamChannel.close();
		long endTime = System.currentTimeMillis();
		System.out.println("消耗时间：" + (endTime - starTime) + "毫秒");

	}

	// 分散读取和聚合写入的操作
	private static void dispersionPolymerization() throws Exception {
		// 获取文件输入流
		File file = new File("G:\\Development\\eclipse\\workspace\\springboot-zk-1\\src\\main\\resources\\1.txt");
		FileInputStream inputStream = new FileInputStream(file);
		// 从文件输入流获取通道
		FileChannel inputStreamChannel = inputStream.getChannel();
		// 获取文件输出流
		FileOutputStream outputStream = new FileOutputStream(new File("G:\\Development\\eclipse\\workspace\\springboot-zk-1\\src\\main\\resources\\2.txt"));
		// 从文件输出流获取通道
		FileChannel outputStreamChannel = outputStream.getChannel();
		// 创建三个缓冲区，分别都是5
		ByteBuffer byteBuffer1 = ByteBuffer.allocate(5);
		ByteBuffer byteBuffer2 = ByteBuffer.allocate(5);
		ByteBuffer byteBuffer3 = ByteBuffer.allocate(5);
		// 创建一个缓冲区数组
		ByteBuffer[] buffers = new ByteBuffer[] { byteBuffer1, byteBuffer2, byteBuffer3 };
		// 循环写入到buffers缓冲区数组中，分散读取
		long read;
		long sumLength = 0;
		while ((read = inputStreamChannel.read(buffers)) != -1) {
			sumLength += read;
			Arrays.stream(buffers)
					.map(buffer -> "posstion=" + buffer.position() + ",limit=" + buffer.limit())
					.forEach(System.out::println);
			// 切换模式
			Arrays.stream(buffers).forEach(Buffer::flip);
			// 聚合写入到文件输出通道
			outputStreamChannel.write(buffers);
			// 清空缓冲区
			Arrays.stream(buffers).forEach(Buffer::clear);
		}
		System.out.println("总长度:" + sumLength);
		// 关闭通道
		outputStream.close();
		inputStream.close();
		outputStreamChannel.close();
		inputStreamChannel.close();
	}

	private static void socketTelnet() throws Exception {
		// 获取ServerSocketChannel
		ServerSocketChannel channel = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
		// 绑定地址，端口号
		channel.bind(address);
		// 创建一个缓冲区
		// ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

		// 网络io分散读取，聚合写入
		// 创建三个缓冲区，分别都是5
		ByteBuffer byteBuffer1 = ByteBuffer.allocate(5);
		ByteBuffer byteBuffer2 = ByteBuffer.allocate(5);
		ByteBuffer byteBuffer3 = ByteBuffer.allocate(5);
		// 创建一个缓冲区数组
		ByteBuffer[] buffers = new ByteBuffer[] { byteBuffer1, byteBuffer2, byteBuffer3 };

		while (true) {
			// 获取SocketChannel
			SocketChannel socketChannel = channel.accept();
			while (socketChannel.read(buffers) != -1) {
				// 网络io分散读取，聚合写入
				Arrays.stream(buffers)
						.map(buffer -> "posstion=" + buffer.position() + ",limit=" + buffer.limit())
						.forEach(System.out::println);

				Arrays.stream(buffers).forEach(buf -> System.out.println(new String(buf.array())));

				Arrays.stream(buffers).forEach(Buffer::clear);

//				// 打印结果
//				System.out.println(new String(byteBuffer.array()));
//				// 清空缓冲区
//				byteBuffer.clear();
			}
		}

	}

	private static void FileChannel() throws Exception {
		// 获取文件输入流
		File file = new File("G:\\Development\\eclipse\\workspace\\springboot-zk-1\\src\\main\\resources\\1.txt");
		FileInputStream inputStream = new FileInputStream(file);
		// 从文件输入流获取通道
		FileChannel inputStreamChannel = inputStream.getChannel();
		// 获取文件输出流
		FileOutputStream outputStream = new FileOutputStream(
				new File("G:\\Development\\eclipse\\workspace\\springboot-zk-1\\src\\main\\resources\\2.txt"));
		// 从文件输出流获取通道
		FileChannel outputStreamChannel = outputStream.getChannel();
		// 创建一个byteBuffer，小文件所以就直接一次读取，不分多次循环了
		ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

		// 1、
		// 把输入流通道的数据读取到缓冲区
		inputStreamChannel.read(byteBuffer);
		// 切换成读模式
		byteBuffer.flip();
		// 把数据从缓冲区写入到输出流通道
		outputStreamChannel.write(byteBuffer);
		// 关闭通道

		// or
		// 2、
		// 把输入流通道的数据读取到输出流的通道
		inputStreamChannel.transferTo(0, byteBuffer.limit(), outputStreamChannel);

		// or
		// 3、
		// 把输入流通道的数据读取到输出流的通道
		outputStreamChannel.transferFrom(inputStreamChannel, 0, byteBuffer.limit());

		outputStream.close();
		inputStream.close();
		outputStreamChannel.close();
		inputStreamChannel.close();

	}

	private static void byteBuffer() {
		// 创建堆内内存块(非直接缓冲区)的方法是：
		// 创建堆内内存块HeapByteBuffer
		/*
		 * ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024); String msg =
		 * "java技术爱好者"; // 包装一个byte[]数组获得一个Buffer，实际类型是HeapByteBuffer ByteBuffer
		 * byteBuffer2 = ByteBuffer.wrap(msg.getBytes());
		 * 
		 * // 创建堆外内存块DirectByteBuffer ByteBuffer byteBuffer3 =
		 * ByteBuffer.allocateDirect(1024);
		 */
		// 创建堆内内存块HeapByteBuffer
		ByteBuffer.allocate(1024);
		// 创建堆外内存块DirectByteBuffer
		ByteBuffer.allocateDirect(1024);

		String msg = "java技术爱好者，起飞！";
		// 创建一个固定大小的buffer(返回的是HeapByteBuffer)
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		byte[] bytes = msg.getBytes();
		// 写入数据到Buffer中
		byteBuffer.put(bytes);
		// 切换成读模式，关键一步
		byteBuffer.flip();
		// 创建一个临时数组，用于存储获取到的数据
		byte[] tempByte = new byte[bytes.length];
		int i = 0;
		// 如果还有数据，就循环。循环判断条件
		while (byteBuffer.hasRemaining()) {
			// 获取byteBuffer中的数据
			byte b = byteBuffer.get();
			// 放到临时数组中
			tempByte[i] = b;
			i++;
		}
		// 打印结果
		System.out.println(new String(tempByte));// java技术爱好者，起飞！

	}

	private static void loop() {
		int[] a = new int[2000];
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++)
			for (int j = 0; j < 2000; j++)
				a[j] = a[j] + 1;

		System.out.println(System.currentTimeMillis() - start);

		int[] b = new int[2000];
		long crs = System.currentTimeMillis();
		for (int j = 0; j < 2000; j++)
			for (int i = 0; i < 1000000; i++)
				b[j] = b[j] + 1;

		System.out.println(System.currentTimeMillis() - crs);
	}

	private static void StackOverflowError() {
		// 运行如下代码探究运行时常量池的位置
		// 用list保持着引用 防止full gc回收常量池
		// List<String> list = new ArrayList<String>();
		// int i = 0;
		// while (true) {
		// list.add(String.valueOf(i++).intern());
		// }

		try {
			List<Object> list = new ArrayList<Object>();
			for (;;) {
				list.add(new Object()); // 创建对象速度可能高于jvm回收速度
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		try {
			hi();// 递归造成StackOverflowError 这边因为每运行一个方法将创建一个栈帧，栈帧创建太多无法继续申请到内存扩展
		} catch (StackOverflowError e) {
			e.printStackTrace();
		}
	}


	public static void hi() {
		hi();
	}
}
