package com.snowbird.snowlib.screens.categories;

/**
 * A named category for grouping configuration options.
 *
 * Each Category has a Runnable that populates the screen with its relevant options.
 */
public class Category {
	private final String name;
	private final Runnable showOptions;

	public Category(String name, Runnable showOptions) {
		this.name = name;
		this.showOptions = showOptions;
	}

	public String getName() {
		return name;
	}

	public Runnable getShowOptions() {
		return showOptions;
	}
}
