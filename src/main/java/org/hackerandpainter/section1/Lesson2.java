package org.hackerandpainter.section1;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

import static java.util.Arrays.asList;

public class Lesson2 {
	public static void main(String[] args) {
		AppleTree[] appleTrees = AppleTree.newTreeGarden(6);

		Callable<Void> applePicker1 = createApplePicker(appleTrees, 0, 2, "Alex");
		Callable<Void> applePicker2 = createApplePicker(appleTrees, 2, 4, "Bob");
		Callable<Void> applePicker3 = createApplePicker(appleTrees, 4, 6, "Carol");

		ForkJoinPool.commonPool().invokeAll(asList(applePicker1, applePicker2, applePicker3));

		System.out.println();
		System.out.println("All fruits collected!");
	}

	/**
	 * 创建Apple Picker
	 * @param appleTrees 苹果树
	 * @param fromIndexInclusive
	 * @param toIndexExclusive
	 * @param workerName
	 * @return
	 */
	public static Callable<Void> createApplePicker(AppleTree[] appleTrees, int fromIndexInclusive, int toIndexExclusive,
			String workerName) {
		return () -> {
			for (int i = fromIndexInclusive; i < toIndexExclusive; i++) {
				appleTrees[i].pickApples(workerName);
			}
			return null;
		};
	}
}
