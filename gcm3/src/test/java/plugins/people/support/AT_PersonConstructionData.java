package plugins.people.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import tools.annotations.UnitTest;
import tools.annotations.UnitTestMethod;

@UnitTest(target = PersonConstructionData.class)
public final class AT_PersonConstructionData {

	@Test
	@UnitTestMethod(name = "builder", args = {})
	public void testBuilder() {
		assertNotNull(PersonConstructionData.builder());
	}

	@Test
	@UnitTestMethod(target = PersonConstructionData.Builder.class, name = "build", args = {})
	public void testBuild() {
		assertNotNull(PersonConstructionData.builder().build());
	}

	@Test
	@UnitTestMethod(target = PersonConstructionData.Builder.class, name = "add", args = { Object.class })
	public void testAdd() {
		Map<Class<?>, List<Object>> expectedValues = new LinkedHashMap<>();

		List<Object> values = new ArrayList<>();
		values.add("aux1");
		values.add(15);
		values.add(3);
		values.add("aux2");
		values.add("aux1");
		values.add(3);
		values.add(false);

		/*
		 * Fill the builder and add to the expected values map
		 */
		PersonConstructionData.Builder builder = PersonConstructionData.builder();
		for (Object value : values) {
			builder.add(value);
			List<Object> list = expectedValues.get(value.getClass());
			if (list == null) {
				list = new ArrayList<>();
				expectedValues.put(value.getClass(), list);
			}
			list.add(value);
		}

		/*
		 * Show that the person construction data returns the auxiliary data by type
		 * and in the correct order
		 */
	   PersonConstructionData personConstructionData = builder.build();
		for (Class<?> c : expectedValues.keySet()) {
			List<Object> expectedList = expectedValues.get(c);
			List<?> actualList = personConstructionData.getValues(c);
			assertEquals(expectedList, actualList);
		}
		
		//show that types that have no values return an empty list
		assertTrue(personConstructionData.getValues(Double.class).isEmpty());

	}

	@Test
	@UnitTestMethod(name = "getValue", args = {Class.class})
	public void testGetValue() {
		Map<Class<?>, List<Object>> expectedValues = new LinkedHashMap<>();

		List<Object> values = new ArrayList<>();
		values.add("aux1");
		values.add(15);
		values.add(3);
		values.add("aux2");
		values.add("aux1");
		values.add(3);
		values.add(false);

		/*
		 * Fill the builder and add to the expected values map
		 */
		PersonConstructionData.Builder builder = PersonConstructionData.builder();
		for (Object value : values) {
			builder.add(value);
			List<Object> list = expectedValues.get(value.getClass());
			if (list == null) {
				list = new ArrayList<>();
				expectedValues.put(value.getClass(), list);
			}
			list.add(value);
		}

		/*
		 * Show that the bulk construction data returns the first auxiliary data by type
		 * 
		 */
		PersonConstructionData personConstructionData = builder.build();
		for (Class<?> c : expectedValues.keySet()) {
			Object expectedValue = expectedValues.get(c).get(0);
			Optional<?> optional = personConstructionData.getValue(c);
			assertTrue(optional.isPresent());
			Object actualValue = optional.get(); 
			assertEquals(expectedValue, actualValue);
		}
		
		//show that types not contained return 
		
		Optional<Double> optional = personConstructionData.getValue(Double.class);
		assertFalse(optional.isPresent());
	}

	
	@Test
	@UnitTestMethod(name = "getValues", args = {Class.class})
	public void testGetValues() {
		Map<Class<?>, List<Object>> expectedValues = new LinkedHashMap<>();

		List<Object> values = new ArrayList<>();
		values.add("aux1");
		values.add(15);
		values.add(3);
		values.add("aux2");
		values.add("aux1");
		values.add(3);
		values.add(false);

		/*
		 * Fill the builder and add to the expected values map
		 */
		PersonConstructionData.Builder builder = PersonConstructionData.builder();
		for (Object value : values) {
			builder.add(value);
			List<Object> list = expectedValues.get(value.getClass());
			if (list == null) {
				list = new ArrayList<>();
				expectedValues.put(value.getClass(), list);
			}
			list.add(value);
		}

		/*
		 * Show that the construction data returns the auxiliary data by type
		 * and in the correct order
		 */
		PersonConstructionData personConstructionData = builder.build();
		for (Class<?> c : expectedValues.keySet()) {
			List<Object> expectedList = expectedValues.get(c);
			List<?> actualList = personConstructionData.getValues(c);
			assertEquals(expectedList, actualList);
		}
		
		//show that types that have no values return an empty list
		assertTrue(personConstructionData.getValues(Double.class).isEmpty());
	}

}
