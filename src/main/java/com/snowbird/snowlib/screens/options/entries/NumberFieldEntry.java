package com.snowbird.snowlib.screens.options.entries;

import java.util.function.Consumer;

/**
 * A text field entry that only accepts digits (converts to a Long).
 */
public class NumberFieldEntry extends AbstractTextFieldEntry<Long> {

	public NumberFieldEntry(
		String name,
		String description,
		long initialValue,
		Consumer<Long> onChange
	) {
		super(name, description, String.valueOf(initialValue), onChange);
		this.textField.setFilter(input -> input.matches("\\d*")); // digits only
	}

	@Override
	protected void onTextChanged(String value) {
		if (!value.isEmpty()) {
			onChange.accept(Long.parseLong(value));
		}
	}
}
