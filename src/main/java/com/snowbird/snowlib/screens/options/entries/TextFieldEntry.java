package com.snowbird.snowlib.screens.options.entries;

import java.util.function.Consumer;

/**
 * A text field entry that accepts string input.
 * Extends the generic AbstractTextFieldEntry with String as the type parameter.
 */
public class TextFieldEntry extends AbstractTextFieldEntry<String> {

	/**
	 * Constructs a new TextFieldEntry.
	 *
	 * @param name        The display name of the entry.
	 * @param description A brief description or tooltip for the entry.
	 * @param initialValue The initial text value of the text field.
	 * @param onChange     A callback function that gets invoked whenever the text changes.
	 */
	public TextFieldEntry(String name, String description, String initialValue, Consumer<String> onChange) {
		super(name, description, initialValue, onChange);
	}

	/**
	 * Called whenever the text in the text field changes.
	 * Passes the new value to the provided onChange consumer.
	 *
	 * @param value The new text value.
	 */
	@Override
	protected void onTextChanged(String value) {
		onChange.accept(value);
	}
}
