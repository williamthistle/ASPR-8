package plugins.properties.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.Context;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import nucleus.testsupport.MockContext;
import util.ContractException;
import util.MutableDouble;
import util.SeedProvider;
import util.annotations.UnitTest;
import util.annotations.UnitTestConstructor;
import util.annotations.UnitTestMethod;

/**
 * Common interface to all person property managers. A person property manager
 * manages all the property values for people for a particular person property
 * identifier.
 * 
 * @author Shawn Hatch
 *
 */

@UnitTest(target = ObjectPropertyManager.class)
public class AT_ObjectPropertyManager {

	@Test
	@UnitTestMethod(name = "getPropertyValue", args = { int.class })
	public void testGetPropertyValue() {
		RandomGenerator randomGenerator = SeedProvider.getRandomGenerator(6268125375257441705L);

		MockContext mockContext = MockContext.builder().build();

		String defaultValue = "YELLOW";
		PropertyDefinition propertyDefinition = PropertyDefinition.builder().setType(String.class).setDefaultValue(defaultValue).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		ObjectPropertyManager objectPropertyManager = new ObjectPropertyManager(mockContext, propertyDefinition, 0);

		/*
		 * We will set the first 300 values multiple times at random
		 */
		Map<Integer, String> expectedValues = new LinkedHashMap<>();

		for (int i = 0; i < 1000; i++) {
			int id = randomGenerator.nextInt(300);
			String value = getRandomString(randomGenerator);
			expectedValues.put(id, value);
			objectPropertyManager.setPropertyValue(id, value);
		}

		/*
		 * if the value was set above, then it should equal the last value place
		 * in the expected values, otherwise it will have the default value.
		 */
		for (int i = 0; i < 300; i++) {
			if (expectedValues.containsKey(i)) {
				assertEquals(expectedValues.get(i), objectPropertyManager.getPropertyValue(i));

			} else {
				assertEquals(defaultValue, (String) objectPropertyManager.getPropertyValue(i));

			}
		}

		// precondition tests
		ContractException contractException = assertThrows(ContractException.class, () -> objectPropertyManager.getPropertyValue(-1));
		assertEquals(PropertyError.NEGATIVE_INDEX, contractException.getErrorType());

	}

	@Test
	@UnitTestMethod(name = "getPropertyTime", args = { int.class })
	public void testGetPropertyTime() {
		RandomGenerator randomGenerator = SeedProvider.getRandomGenerator(3180659211825142278L);

		MutableDouble time = new MutableDouble(0);
		MockContext mockContext = MockContext.builder().setTimeSupplier(() -> time.getValue()).build();

		PropertyDefinition propertyDefinition = PropertyDefinition.builder().setType(String.class).setDefaultValue("RED").build();

		ObjectPropertyManager objectPropertyManager = new ObjectPropertyManager(mockContext, propertyDefinition, 0);
		assertThrows(RuntimeException.class, () -> objectPropertyManager.getPropertyTime(0));

		propertyDefinition = PropertyDefinition.builder().setType(String.class).setDefaultValue("YELLOW").setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		ObjectPropertyManager objectPropertyManager2 = new ObjectPropertyManager(mockContext, propertyDefinition, 0);
		for (int i = 0; i < 1000; i++) {
			int id = randomGenerator.nextInt(300);
			time.setValue(randomGenerator.nextDouble() * 1000);
			String value = getRandomString(randomGenerator);
			objectPropertyManager2.setPropertyValue(id, value);
			assertEquals(time.getValue(), objectPropertyManager2.getPropertyTime(id), 0);
		}

		// precondition tests:
		propertyDefinition = PropertyDefinition.builder().setType(Boolean.class).setDefaultValue(false).build();
		ObjectPropertyManager opm = new ObjectPropertyManager(mockContext, propertyDefinition, 0);
		ContractException contractException = assertThrows(ContractException.class, () -> opm.getPropertyTime(0));
		assertEquals(PropertyError.TIME_TRACKING_OFF, contractException.getErrorType());

		contractException = assertThrows(ContractException.class, () -> opm.getPropertyTime(-1));
		assertEquals(PropertyError.NEGATIVE_INDEX, contractException.getErrorType());

	}

	private static String getRandomString(RandomGenerator randomGenerator) {
		switch (randomGenerator.nextInt(3)) {
		case 0:
			return "RED";
		case 1:
			return "YELLOW";
		default:
			return "BLUE";
		}
	}

	@Test
	@UnitTestMethod(name = "setPropertyValue", args = { int.class, Object.class })
	public void testSetPropertyValue() {

		RandomGenerator randomGenerator = SeedProvider.getRandomGenerator(6268125375257441705L);

		MockContext mockContext = MockContext.builder().build();

		String defaultValue = "YELLOW";
		PropertyDefinition propertyDefinition = PropertyDefinition.builder().setType(String.class).setDefaultValue(defaultValue).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		ObjectPropertyManager objectPropertyManager = new ObjectPropertyManager(mockContext, propertyDefinition, 0);

		/*
		 * We will set the first 300 values multiple times at random
		 */
		Map<Integer, String> expectedValues = new LinkedHashMap<>();

		for (int i = 0; i < 1000; i++) {
			int id = randomGenerator.nextInt(300);
			String value = getRandomString(randomGenerator);
			expectedValues.put(id, value);
			objectPropertyManager.setPropertyValue(id, value);
		}

		/*
		 * if the value was set above, then it should equal the last value place
		 * in the expected values, otherwise it will have the default value.
		 */
		for (int i = 0; i < 300; i++) {
			if (expectedValues.containsKey(i)) {
				assertEquals(expectedValues.get(i), objectPropertyManager.getPropertyValue(i));

			} else {
				assertEquals(defaultValue, (String) objectPropertyManager.getPropertyValue(i));

			}
		}

		// precondition tests
		ContractException contractException = assertThrows(ContractException.class, () -> objectPropertyManager.setPropertyValue(-1, "value"));
		assertEquals(PropertyError.NEGATIVE_INDEX, contractException.getErrorType());

	}

	@Test
	@UnitTestMethod(name = "removeId", args = { int.class })
	public void testRemoveId() {
		/*
		 * Should have no effect on the value that is stored for the sake of
		 * efficiency.
		 */

		MockContext mockContext = MockContext.builder().build();

		// we will first test the manager with an initial value of false
		String defaultValue = "RED";
		PropertyDefinition propertyDefinition = PropertyDefinition.builder().setType(String.class).setDefaultValue(defaultValue).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		ObjectPropertyManager objectPropertyManager = new ObjectPropertyManager(mockContext, propertyDefinition, 0);

		// initially, the value should be the default value for the manager
		assertEquals(defaultValue, (String) objectPropertyManager.getPropertyValue(5));

		// after setting the value we should be able to retrieve a new value
		String newValue = "BLUE";
		objectPropertyManager.setPropertyValue(5, newValue);
		assertEquals(newValue, (String) objectPropertyManager.getPropertyValue(5));

		// removing the id from the manager should return the value to the
		// deafault
		objectPropertyManager.removeId(5);

		assertEquals(defaultValue, (String) objectPropertyManager.getPropertyValue(5));

		// we will next test the manager with an initial value of true
		propertyDefinition = PropertyDefinition.builder().setType(String.class).setDefaultValue(defaultValue).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		objectPropertyManager = new ObjectPropertyManager(mockContext, propertyDefinition, 0);

		// initially, the value should be the default value for the manager
		assertEquals(defaultValue, (String) objectPropertyManager.getPropertyValue(5));

		// after setting the value we should be able to retrieve the new value
		objectPropertyManager.setPropertyValue(5, newValue);
		assertEquals(newValue, (String) objectPropertyManager.getPropertyValue(5));

		// removing the id from the manager should return the value to the
		// default
		objectPropertyManager.removeId(5);

		assertEquals(defaultValue, (String) objectPropertyManager.getPropertyValue(5));

		// precondition tests
		PropertyDefinition def = PropertyDefinition.builder().setType(Boolean.class).setDefaultValue(true).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();
		ObjectPropertyManager opm = new ObjectPropertyManager(mockContext, def, 0);

		ContractException contractException = assertThrows(ContractException.class, () -> opm.removeId(-1));
		assertEquals(PropertyError.NEGATIVE_INDEX, contractException.getErrorType());
	}

	@Test
	@UnitTestConstructor(args = { Context.class, PropertyDefinition.class, int.class })
	public void testConstructor() {
		MockContext mockContext = MockContext.builder().build();

		PropertyDefinition goodPropertyDefinition = PropertyDefinition.builder().setType(Object.class).setDefaultValue("BLUE").build();

		PropertyDefinition badDoublePropertyDefinition = PropertyDefinition.builder().setType(Object.class).build();

		// if the property definition is null
		ContractException contractException = assertThrows(ContractException.class, () -> new ObjectPropertyManager(mockContext, null, 0));
		assertEquals(PropertyError.NULL_PROPERTY_DEFINITION, contractException.getErrorType());

		// if the property definition does not contain a default value
		contractException = assertThrows(ContractException.class, () -> new ObjectPropertyManager(mockContext, badDoublePropertyDefinition, 0));
		assertEquals(PropertyError.PROPERTY_DEFINITION_MISSING_DEFAULT, contractException.getErrorType());

		// if the initial size is negative
		contractException = assertThrows(ContractException.class, () -> new ObjectPropertyManager(mockContext, goodPropertyDefinition, -1));
		assertEquals(PropertyError.NEGATIVE_INITIAL_SIZE, contractException.getErrorType());

		ObjectPropertyManager objectPropertyManager = new ObjectPropertyManager(mockContext, goodPropertyDefinition, 0);
		assertNotNull(objectPropertyManager);

	}
	
	@Test
	@UnitTestMethod(name = "incrementCapacity", args = { int.class })
	public void testIncrementCapacity() {
		MutableDouble time = new MutableDouble(0);
		MockContext mockContext = MockContext.builder().setTimeSupplier(() -> time.getValue()).build();

		PropertyDefinition propertyDefinition = PropertyDefinition.builder().setType(Integer.class).setDefaultValue(234).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		ObjectPropertyManager objectPropertyManager = new ObjectPropertyManager(mockContext, propertyDefinition, 0);

		// precondition tests
		ContractException contractException = assertThrows(ContractException.class, () -> objectPropertyManager.incrementCapacity(-1));
		assertEquals(PropertyError.NEGATIVE_CAPACITY_INCREMENT, contractException.getErrorType());
	}

}
