package util.wrappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import tools.annotations.UnitTest;
import tools.annotations.UnitTestConstructor;
import tools.annotations.UnitTestMethod;
import util.random.RandomGeneratorProvider;

@UnitTest(target = MutableInteger.class)
public class AT_MutableInteger {
	@Test
	@UnitTestConstructor(args = {})
	public void testConstructor() {
		MutableInteger mutableInteger = new MutableInteger();
		assertEquals(0.0, mutableInteger.getValue());
	}

	@Test
	@UnitTestConstructor(args = { int.class })
	public void testConstructor_Double() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(3824074981405252870L);
		for (int i = 0; i < 30; i++) {
			int value = randomGenerator.nextInt(1000);
			MutableInteger mutableInteger = new MutableInteger(value);
			assertEquals(value, mutableInteger.getValue());
		}
	}

	@Test
	@UnitTestMethod(name = "decrement", args = {})
	public void testDecrement() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(1556136528392901493L);
		for (int i = 0; i < 30; i++) {
			int value = randomGenerator.nextInt(1000);
			MutableInteger mutableInteger = new MutableInteger(value);
			mutableInteger.decrement();
			assertEquals(value - 1, mutableInteger.getValue());
		}
	}

	@Test
	@UnitTestMethod(name = "decrement", args = { int.class })
	public void testDecrement_Double() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(6417530085366013967L);
		for (int i = 0; i < 30; i++) {
			int value = randomGenerator.nextInt(1000);
			MutableInteger mutableInteger = new MutableInteger(value);
			int value2 = randomGenerator.nextInt(1000);
			mutableInteger.decrement(value2);
			assertEquals(value - value2, mutableInteger.getValue());
		}
	}

	@Test
	@UnitTestMethod(name = "increment", args = {})
	public void testIncrement() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(5989612764991268700L);
		for (int i = 0; i < 30; i++) {
			int value = randomGenerator.nextInt(1000);
			MutableInteger mutableInteger = new MutableInteger(value);
			mutableInteger.increment();
			assertEquals(value + 1, mutableInteger.getValue());
		}
	}

	@Test
	@UnitTestMethod(name = "increment", args = { int.class })
	public void testIncrement_Double() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(7024132741444760192L);
		for (int i = 0; i < 30; i++) {
			int value = randomGenerator.nextInt(1000);
			MutableInteger mutableInteger = new MutableInteger(value);
			int value2 = randomGenerator.nextInt(1000);
			mutableInteger.increment(value2);
			assertEquals(value + value2, mutableInteger.getValue());
		}
	}

	@Test
	@UnitTestMethod(name = "getValue", args = {})
	public void testGetValue() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(6346882071250399307L);
		for (int i = 0; i < 30; i++) {
			int value = randomGenerator.nextInt(1000);
			MutableInteger mutableInteger = new MutableInteger(value);
			assertEquals(value, mutableInteger.getValue());
		}
	}

	@Test
	@UnitTestMethod(name = "setValue", args = { int.class })
	public void testSetValue() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(6410956163545832731L);
		for (int i = 0; i < 30; i++) {
			int value = randomGenerator.nextInt(1000);
			MutableInteger mutableInteger = new MutableInteger(value);
			int value2 = randomGenerator.nextInt(1000);
			mutableInteger.setValue(value2);
			assertEquals(value2, mutableInteger.getValue());
		}
	}

	@Test
	@UnitTestMethod(name = "equals", args = { Object.class })
	public void testEquals() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(5255250189313941474L);
		for (int i = 0; i < 30; i++) {
			int value = randomGenerator.nextInt(1000);
			MutableInteger mutableInteger1 = new MutableInteger(value);
			MutableInteger mutableInteger2 = new MutableInteger(value);
			assertEquals(mutableInteger1.getValue(), mutableInteger2.getValue());
		}
	}

	@Test
	@UnitTestMethod(name = "hashCode", args = {})
	public void testHashCode() {

		// show equal objects have equal hash codes
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(5255250189313941474L);
		for (int i = 0; i < 30; i++) {
			int value = randomGenerator.nextInt(1000);
			MutableInteger mutableInteger1 = new MutableInteger(value);
			MutableInteger mutableInteger2 = new MutableInteger(value);
			assertEquals(mutableInteger1.hashCode(), mutableInteger2.hashCode());
		}

	}

	@Test
	@UnitTestMethod(name = "toString", args = {})
	public void testToString() {
		MutableInteger mutableInteger = new MutableInteger(2);
		String expectedValue = "MutableInteger [value=2]";
		String actualValue = mutableInteger.toString();
		assertEquals(actualValue, expectedValue);
		
		mutableInteger = new MutableInteger(62);
		expectedValue = "MutableInteger [value=62]";
		actualValue = mutableInteger.toString();
		assertEquals(actualValue, expectedValue);

	}

}
