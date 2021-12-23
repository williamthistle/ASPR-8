package util.arraycontainers;

import java.util.Arrays;

/**
 * A container that maps non-negative int index values to doubles by storing
 * each double in an array. Returns a default double value for every
 * non-negative int index value until the value is explicitly set by an
 * invocation to the set() method.
 * 
 * @author Shawn Hatch
 *
 */
public final class DoubleValueContainer {

	/*
	 * The array for storing the values
	 */
	private double[] values;

	/*
	 * Holds the logical size of the values array based on the highest index
	 * used in invocations of setValue().
	 */
	private int size;

	/*
	 * The value returned for any non-negative index that has not been set via
	 * an invocation of setValue().
	 */
	private double defaultValue;

	/*
	 * Grows the length of the values array to be the greater of the given
	 * capacity and 125% of its current length, filling empty elements in the
	 * array with the default value.
	 */
	private void grow(int capacity) {
		int oldCapacity = values.length;
		int newCapacity = Math.max(capacity, values.length + (values.length >> 2));
		values = Arrays.copyOf(values, newCapacity);
		if (defaultValue != 0) {
			for (int i = oldCapacity; i < newCapacity; i++) {
				values[i] = defaultValue;
			}
		}
	}

	/**
	 * Returns the value at index
	 * 
	 * @param index
	 * @return
	 * @throws IndexOutOfBoundsException
	 *             <li>if index is negative
	 * 
	 */
	public double getValue(int index) {
		double result;

		if (index < values.length) {
			result = values[index];
		} else {
			result = defaultValue;
		}

		return result;
	}

	public int size() {
		return size;
	}

	/**
	 * Sets the capacity to the given capacity if the current capacity is less
	 * than the one given.
	 */
	public void setCapacity(int capacity) {
		if (capacity > values.length) {
			grow(capacity);
		}
	}

	/**
	 * Returns the capacity of this container. Capacity is guaranteed to be
	 * greater than or equal to size.
	 */
	public int getCapacity() {
		return values.length;
	}

	/**
	 * Returns the default value
	 * 
	 */
	public double getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Constructs the DoubleValueContainer with the given default value.
	 * 
	 * @param defaultValue
	 */
	public DoubleValueContainer(double defaultValue) {
		this(defaultValue, 16);
	}

	/**
	 * Constructs the DoubleValueContainer with the given default value and
	 * initial capacity
	 * 
	 * @param defaultValue
	 * @param capacity
	 * 
	 * @throws NegativeArraySizeException
	 *             if the capacity is negative
	 */
	public DoubleValueContainer(double defaultValue, int capacity) {
		values = new double[capacity];
		if (defaultValue != 0) {
			for (int i = 0; i < capacity; i++) {
				values[i] = defaultValue;
			}
		}
		this.defaultValue = defaultValue;

	}

	/**
	 * Sets the value at the index to the given value
	 * 
	 * @throws RuntimeException
	 *             <li>if index < 0
	 */
	public void setValue(int index, double value) {
		if (index < 0) {
			throw new RuntimeException("index out of bounds " + index);
		}
		if (index >= values.length) {
			grow(index + 1);
		}
		if (index >= size) {
			size = index + 1;
		}
		values[index] = value;
	}

}
