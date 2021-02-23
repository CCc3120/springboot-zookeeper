package com.bingo.guava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

class Guava {

	static List<String> list = new ArrayList<>();


	static {
		// 初始化集合
		for (int i = 1; i <= 10; i++) {
			list.add(String.valueOf(i));
		}
	}

	public static void main(String[] args) {
		List<String> unmodifiableLists = ImmutableList.copyOf(list);
		list.remove("1");
		System.out.println(unmodifiableLists);// [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

		System.out.println(list instanceof Object);
		List<String> unmodifiableList = Collections.unmodifiableList(list);
		System.out.println(unmodifiableList);// [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
		// 删除原集合元素
		list.remove("1");
		System.out.println(unmodifiableList);// [2, 3, 4, 5, 6, 7, 8, 9, 10]
	}

	public static void list() {
		List<String> subList = list.subList(0, 5);
		list.forEach(System.out::println);
		subList.forEach(System.out::println);
		subList.add("12");
		System.out.println("-----------------------");
		list.forEach(System.out::println);
		subList.forEach(System.out::println);
		// ===================
		List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6);
		nums.add(7);
		// ==============
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String str = it.next();
			if ("1".equals(str)) {
				it.remove();
			}
		}
		
		list.forEach(str -> System.out.println(str));
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			if ("2".equals(s)) {
				list.remove(s);
			}
		}
		list.forEach(System.out::println);
		list.removeIf(str -> "3".equals(str));
		list.forEach(System.out::println);
		// =================
		List<String> lists = Lists.newArrayList();
		org.apache.curator.shaded.com.google.common.collect.Lists.newArrayList();
		/**
		 * 创建不可变集合 先理解什么是immutable(不可变)对象
		 * 
		 * 在多线程操作下，是线程安全的 </br>
		 * 所有不可变集合会比可变集合更有效的利用资源 </br>
		 * 中途不可改变</br>
		 */
		ImmutableList<String> immutableList = ImmutableList.of("1", "2");
		Joiner.on("-").join(immutableList);
	}

}

class PrintUtil {
	/**
	 * 对要遍历的元素添加add操作
	 */
	public void addString(String x) {
		System.out.println(x + "add");
	}
}
