package com.rs2.core.base;

public interface IPanel {
	// Static Method
	default void run(String file) {
		System.out.println("Called from Interface IPanel");
	}

	default void open() {
		System.out.println("Called from Interface IPanel");
	}

	default void open(String folder) {
		System.out.println("Called from Interface IPanel");
	}
}
