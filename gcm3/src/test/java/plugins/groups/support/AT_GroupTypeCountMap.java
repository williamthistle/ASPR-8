package plugins.groups.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import plugins.groups.testsupport.XTestGroupTypeId;
import util.SeedProvider;
import util.annotations.UnitTest;
import util.annotations.UnitTestMethod;

/**
 * Test class for {@link GroupTypeCountMap}
 * 
 * @author Shawn Hatch
 *
 */
@UnitTest(target = GroupTypeCountMap.class)
public class AT_GroupTypeCountMap {
	private static SeedProvider SEED_PROVIDER;

	@BeforeAll
	public static void beforeClass() {
		SEED_PROVIDER = new SeedProvider(8556976443421256038L);
	}

	/**
	 * Internal test(not part of public tests) to show that there are no large
	 * gaps in the seed cases generated by the SeedProvider.
	 */
	@AfterAll
	public static void afterClass() {
		// System.out.println(AT_GroupTypeCountMap.class.getSimpleName() + " " +
		// SEED_PROVIDER.generateUnusedSeedReport());
	}

	/**
	 * Tests {@linkplain GroupTypeCountMap#equals(Object)
	 */
	@Test
	@UnitTestMethod(name = "equals", args = { Object.class })
	public void testEquals() {
		/*
		 * Show various cases demonstrating that build order and implied zero
		 * values do not influence the equals contract
		 */

		// order should not matter
		GroupTypeCountMap.Builder builder = GroupTypeCountMap.builder();
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_1, 5);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_2, 7);
		GroupTypeCountMap groupTypeCountMap1 = builder.build();

		builder.setCount(XTestGroupTypeId.GROUP_TYPE_2, 7);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_1, 5);
		GroupTypeCountMap groupTypeCountMap2 = builder.build();

		assertEquals(groupTypeCountMap1, groupTypeCountMap2);

		// implied zero values should not matter
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_3, 0);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_2, 7);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_1, 5);
		groupTypeCountMap1 = builder.build();

		builder.setCount(XTestGroupTypeId.GROUP_TYPE_2, 7);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_1, 5);
		groupTypeCountMap2 = builder.build();

		assertEquals(groupTypeCountMap1, groupTypeCountMap2);

		// differences in positive counts matter

		builder.setCount(XTestGroupTypeId.GROUP_TYPE_1, 5);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_2, 7);
		groupTypeCountMap1 = builder.build();

		builder.setCount(XTestGroupTypeId.GROUP_TYPE_1, 5);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_2, 3);
		groupTypeCountMap2 = builder.build();

		assertNotEquals(groupTypeCountMap1, groupTypeCountMap2);
	}

	/**
	 * Tests {@linkplain GroupTypeCountMap#hashCode()
	 */
	@Test
	@UnitTestMethod(name = "hashCode", args = {})
	public void testHashCode() {
		/*
		 * Equal objects have equal hash codes
		 */

		// order should not matter
		GroupTypeCountMap.Builder builder = GroupTypeCountMap.builder();
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_1, 5);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_2, 7);
		GroupTypeCountMap groupTypeCountMap1 = builder.build();

		builder.setCount(XTestGroupTypeId.GROUP_TYPE_2, 7);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_1, 5);
		GroupTypeCountMap groupTypeCountMap2 = builder.build();

		assertEquals(groupTypeCountMap1.hashCode(), groupTypeCountMap2.hashCode());

		// implied zero values should not matter
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_3, 0);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_2, 7);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_1, 5);
		groupTypeCountMap1 = builder.build();

		builder.setCount(XTestGroupTypeId.GROUP_TYPE_2, 7);
		builder.setCount(XTestGroupTypeId.GROUP_TYPE_1, 5);
		groupTypeCountMap2 = builder.build();

		assertEquals(groupTypeCountMap1.hashCode(), groupTypeCountMap2.hashCode());

	}

	/**
	 * Tests {@linkplain GroupTypeCountMap#getGroupCount(GroupTypeId)
	 */
	@Test
	@UnitTestMethod(name = "getGroupCount", args = { GroupTypeId.class })
	public void testGetGroupCount() {
		// covered by testBuilder() test method
	}

	/**
	 * Tests {@linkplain GroupTypeCountMap#toString()
	 */
	@Test
	@UnitTestMethod(name = "toString", args = {})
	public void testToString() {

		GroupTypeCountMap.Builder builder = GroupTypeCountMap.builder();

		int count = 1;
		for (XTestGroupTypeId xTestGroupTypeId : XTestGroupTypeId.values()) {
			builder.setCount(xTestGroupTypeId, count++);
		}
		GroupTypeCountMap groupTypeCountMap = builder.build();

		String expectedValue = "GroupTypeCountMap [GROUP_TYPE_1=1, GROUP_TYPE_2=2, GROUP_TYPE_3=3, GROUP_TYPE_4=4, GROUP_TYPE_5=5, GROUP_TYPE_6=6]";
		String actualValue = groupTypeCountMap.toString();

		assertEquals(expectedValue, actualValue);
	}

	/**
	 * Tests {@linkplain GroupTypeCountMap#builder()
	 */
	@Test
	@UnitTestMethod(name = "builder", args = {})
	public void testBuilder() {
		final long seed = SEED_PROVIDER.getSeedValue(0);
		RandomGenerator randomGenerator = SeedProvider.getRandomGenerator(seed);

		for (int i = 0; i < 20; i++) {
			Map<XTestGroupTypeId, Integer> expectedValues = new LinkedHashMap<>();
			GroupTypeCountMap.Builder builder = GroupTypeCountMap.builder();

			for (XTestGroupTypeId xTestGroupTypeId : XTestGroupTypeId.values()) {
				expectedValues.put(xTestGroupTypeId, 0);
				if (randomGenerator.nextBoolean()) {
					int count = randomGenerator.nextInt(3);
					builder.setCount(xTestGroupTypeId, count);
					expectedValues.put(xTestGroupTypeId, count);
				}
			}
			GroupTypeCountMap groupTypeCountMap = builder.build();

			for (XTestGroupTypeId xTestGroupTypeId : XTestGroupTypeId.values()) {
				int expectedValue = expectedValues.get(xTestGroupTypeId);
				int actualValue = groupTypeCountMap.getGroupCount(xTestGroupTypeId);
				assertEquals(expectedValue, actualValue);
			}
		}

		// precondition checks
		assertThrows(IllegalArgumentException.class, () -> GroupTypeCountMap.builder().setCount(null, 10));
		assertThrows(IllegalArgumentException.class, () -> GroupTypeCountMap.builder().setCount(XTestGroupTypeId.GROUP_TYPE_1, -1));

	}

	// public java.util.Set gcm.simulation.GroupTypeCountMap.getGroupTypeIds()
	/**
	 * Tests {@linkplain GroupTypeCountMap#getGroupTypeIds()
	 */
	@Test
	@UnitTestMethod(name = "getGroupTypeIds", args = {})
	public void testGetGroupTypeIds() {
		final long seed = SEED_PROVIDER.getSeedValue(1);
		RandomGenerator randomGenerator = SeedProvider.getRandomGenerator(seed);

		for (int i = 0; i < 20; i++) {
			Set<GroupTypeId> expectedGroupTypeIds = new LinkedHashSet<>();
			GroupTypeCountMap.Builder builder = GroupTypeCountMap.builder();

			for (XTestGroupTypeId xTestGroupTypeId : XTestGroupTypeId.values()) {

				if (randomGenerator.nextBoolean()) {
					expectedGroupTypeIds.add(xTestGroupTypeId);
					builder.setCount(xTestGroupTypeId, 1);
				}
			}
			GroupTypeCountMap groupTypeCountMap = builder.build();

			Set<GroupTypeId> actualGroupTypeIds = groupTypeCountMap.getGroupTypeIds();
			assertEquals(expectedGroupTypeIds, actualGroupTypeIds);
		}

	}
}
