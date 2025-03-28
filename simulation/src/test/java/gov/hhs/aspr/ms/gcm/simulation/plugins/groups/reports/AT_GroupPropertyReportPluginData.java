package gov.hhs.aspr.ms.gcm.simulation.plugins.groups.reports;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.gcm.simulation.nucleus.StandardVersioning;
import gov.hhs.aspr.ms.gcm.simulation.plugins.groups.support.GroupError;
import gov.hhs.aspr.ms.gcm.simulation.plugins.groups.support.GroupPropertyId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.groups.support.GroupTypeId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.groups.testsupport.TestGroupPropertyId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.groups.testsupport.TestGroupTypeId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.properties.support.PropertyError;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportError;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportLabel;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportPeriod;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.SimpleReportLabel;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;
import gov.hhs.aspr.ms.util.wrappers.MultiKey;

public class AT_GroupPropertyReportPluginData {

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "builder", args = {})
	public void testBuilder() {
		GroupPropertyReportPluginData.Builder builder = GroupPropertyReportPluginData.builder();
		assertNotNull(builder);
	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.Builder.class, name = "build", args = {})
	public void testBuild() {
		// the specific capabilities are covered elsewhere

		// precondition test: if the report period is not assigned
		ContractException contractException = assertThrows(ContractException.class, () -> //
		GroupPropertyReportPluginData.builder()//
				.setReportLabel(new SimpleReportLabel(getClass()))//
				.build());
		assertEquals(ReportError.NULL_REPORT_PERIOD, contractException.getErrorType());

		// precondition test: if the report label is not assigned
		contractException = assertThrows(ContractException.class, () -> //
		GroupPropertyReportPluginData.builder()//
				.setReportPeriod(ReportPeriod.DAILY)//
				.build());
		assertEquals(ReportError.NULL_REPORT_LABEL, contractException.getErrorType());

	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.Builder.class, name = "setReportLabel", args = {
			ReportLabel.class })
	public void testSetReportLabel() {

		for (int i = 0; i < 30; i++) {
			ReportLabel expectedReportLabel = new SimpleReportLabel(i);
			GroupPropertyReportPluginData groupPropertyReportPluginData = //
					GroupPropertyReportPluginData.builder()//
							.setReportPeriod(ReportPeriod.DAILY)//
							.setReportLabel(expectedReportLabel)//
							.build();

			assertEquals(expectedReportLabel, groupPropertyReportPluginData.getReportLabel());
		}

		// precondition: if the report label is null
		ContractException contractException = assertThrows(ContractException.class, () -> {
			GroupPropertyReportPluginData.builder().setReportLabel(null);
		});
		assertEquals(ReportError.NULL_REPORT_LABEL, contractException.getErrorType());
	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.Builder.class, name = "setReportPeriod", args = {
			ReportPeriod.class })
	public void testSetReportPeriod() {

		ReportLabel reportLabel = new SimpleReportLabel("report label");
		for (ReportPeriod reportPeriod : ReportPeriod.values()) {

			GroupPropertyReportPluginData groupPropertyReportPluginData = //
					GroupPropertyReportPluginData.builder()//
							.setReportPeriod(reportPeriod)//
							.setReportLabel(reportLabel)//
							.build();

			assertEquals(reportPeriod, groupPropertyReportPluginData.getReportPeriod());
		}

		// precondition: if the report period is null
		ContractException contractException = assertThrows(ContractException.class, () -> {
			GroupPropertyReportPluginData.builder().setReportPeriod(null);
		});
		assertEquals(ReportError.NULL_REPORT_PERIOD, contractException.getErrorType());
	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.Builder.class, name = "setDefaultInclusion", args = {
			boolean.class })
	public void testSetDefaultInclusion() {
		ReportLabel reportLabel = new SimpleReportLabel("report label");
		ReportPeriod reportPeriod = ReportPeriod.DAILY;

		// show the default value is true
		GroupPropertyReportPluginData groupPropertyReportPluginData = //
				GroupPropertyReportPluginData.builder()//
						.setReportPeriod(reportPeriod)//
						.setReportLabel(reportLabel)//
						.build();
		assertEquals(true, groupPropertyReportPluginData.getDefaultInclusionPolicy());

		groupPropertyReportPluginData = //
				GroupPropertyReportPluginData.builder()//
						.setReportPeriod(reportPeriod)//
						.setReportLabel(reportLabel)//
						.setDefaultInclusion(true)//
						.build();
		assertEquals(true, groupPropertyReportPluginData.getDefaultInclusionPolicy());

		groupPropertyReportPluginData = //
				GroupPropertyReportPluginData.builder()//
						.setReportPeriod(reportPeriod)//
						.setReportLabel(reportLabel)//
						.setDefaultInclusion(false)//
						.build();
		assertEquals(false, groupPropertyReportPluginData.getDefaultInclusionPolicy());

	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.Builder.class, name = "includeGroupProperty", args = {
			GroupTypeId.class, GroupPropertyId.class })
	public void testIncludeGroupProperty() {
		ReportLabel reportLabel = new SimpleReportLabel("report label");
		ReportPeriod reportPeriod = ReportPeriod.DAILY;

		GroupPropertyReportPluginData groupPropertyReportPluginData = //
				GroupPropertyReportPluginData.builder()//
						.setReportPeriod(reportPeriod)//
						.setReportLabel(reportLabel)//
						.build();
		assertTrue(groupPropertyReportPluginData.getIncludedProperties(TestGroupTypeId.GROUP_TYPE_1).isEmpty());

		// show that inclusion alone works
		Set<TestGroupPropertyId> expectedGroupPropertyIds = new LinkedHashSet<>();
		Set<MultiKey> expectedMultiKeys = new LinkedHashSet<>();

		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK);
		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_1_2_INTEGER_MUTABLE_NO_TRACK);
		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_2_3_DOUBLE_MUTABLE_TRACK);

		GroupPropertyReportPluginData.Builder builder = GroupPropertyReportPluginData.builder()//
				.setReportPeriod(reportPeriod)//
				.setReportLabel(reportLabel);//

		for (TestGroupPropertyId testGroupPropertyId : expectedGroupPropertyIds) {
			builder.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
			expectedMultiKeys.add(new MultiKey(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId));
		}

		Set<MultiKey> actualMultiKeys = new LinkedHashSet<>();
		groupPropertyReportPluginData = builder.build();
		for (GroupTypeId testGroupTypeId : groupPropertyReportPluginData.getGroupTypeIds()) {
			for (GroupPropertyId testGroupPropertyId : groupPropertyReportPluginData
					.getIncludedProperties(testGroupTypeId)) {
				actualMultiKeys.add(new MultiKey(testGroupTypeId, testGroupPropertyId));
			}
		}

		assertEquals(expectedMultiKeys, actualMultiKeys);

		// show that inclusion will override exclusion

		builder = GroupPropertyReportPluginData.builder()//
				.setReportPeriod(reportPeriod)//
				.setReportLabel(reportLabel);//

		for (TestGroupPropertyId testGroupPropertyId : expectedGroupPropertyIds) {
			builder.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
			builder.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
			expectedMultiKeys.add(new MultiKey(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId));
		}

		groupPropertyReportPluginData = builder.build();

		actualMultiKeys = new LinkedHashSet<>();
		groupPropertyReportPluginData = builder.build();
		for (GroupTypeId testGroupTypeId : groupPropertyReportPluginData.getGroupTypeIds()) {
			for (GroupPropertyId testGroupPropertyId : groupPropertyReportPluginData
					.getIncludedProperties(testGroupTypeId)) {
				actualMultiKeys.add(new MultiKey(testGroupTypeId, testGroupPropertyId));
			}
		}

		assertEquals(expectedMultiKeys, actualMultiKeys);

		// precondition: if the group type id is null
		ContractException contractException = assertThrows(ContractException.class, () -> {
			GroupPropertyReportPluginData.builder().includeGroupProperty(null,
					TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK);
		});
		assertEquals(GroupError.NULL_GROUP_TYPE_ID, contractException.getErrorType());

		// precondition: if the group property id is null
		contractException = assertThrows(ContractException.class, () -> {
			GroupPropertyReportPluginData.builder().includeGroupProperty(TestGroupTypeId.GROUP_TYPE_1, null);
		});
		assertEquals(PropertyError.NULL_PROPERTY_ID, contractException.getErrorType());

	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.Builder.class, name = "excludeGroupProperty", args = {
			GroupTypeId.class, GroupPropertyId.class })
	public void testExcludeGroupProperty() {
		ReportLabel reportLabel = new SimpleReportLabel("report label");
		ReportPeriod reportPeriod = ReportPeriod.DAILY;

		GroupPropertyReportPluginData groupPropertyReportPluginData = //
				GroupPropertyReportPluginData.builder()//
						.setReportPeriod(reportPeriod)//
						.setReportLabel(reportLabel)//
						.build();
		assertTrue(groupPropertyReportPluginData.getIncludedProperties(TestGroupTypeId.GROUP_TYPE_1).isEmpty());

		// show that exclusion alone works
		Set<TestGroupPropertyId> expectedGroupPropertyIds = new LinkedHashSet<>();
		Set<MultiKey> expectedMultiKeys = new LinkedHashSet<>();

		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK);
		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_1_2_INTEGER_MUTABLE_NO_TRACK);
		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_2_3_DOUBLE_MUTABLE_TRACK);

		GroupPropertyReportPluginData.Builder builder = GroupPropertyReportPluginData.builder()//
				.setReportPeriod(reportPeriod)//
				.setReportLabel(reportLabel);//

		for (TestGroupPropertyId testGroupPropertyId : expectedGroupPropertyIds) {
			builder.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
			expectedMultiKeys.add(new MultiKey(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId));
		}

		Set<MultiKey> actualMultiKeys = new LinkedHashSet<>();
		groupPropertyReportPluginData = builder.build();
		for (GroupTypeId testGroupTypeId : groupPropertyReportPluginData.getGroupTypeIds()) {
			for (GroupPropertyId testGroupPropertyId : groupPropertyReportPluginData
					.getExcludedProperties(testGroupTypeId)) {
				actualMultiKeys.add(new MultiKey(testGroupTypeId, testGroupPropertyId));
			}
		}

		assertEquals(expectedMultiKeys, actualMultiKeys);

		// show that exclusion will override exclusion

		builder = GroupPropertyReportPluginData.builder()//
				.setReportPeriod(reportPeriod)//
				.setReportLabel(reportLabel);//

		for (TestGroupPropertyId testGroupPropertyId : expectedGroupPropertyIds) {
			builder.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
			builder.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);

			expectedMultiKeys.add(new MultiKey(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId));
		}

		groupPropertyReportPluginData = builder.build();

		actualMultiKeys = new LinkedHashSet<>();
		groupPropertyReportPluginData = builder.build();
		for (GroupTypeId testGroupTypeId : groupPropertyReportPluginData.getGroupTypeIds()) {
			for (GroupPropertyId testGroupPropertyId : groupPropertyReportPluginData
					.getExcludedProperties(testGroupTypeId)) {
				actualMultiKeys.add(new MultiKey(testGroupTypeId, testGroupPropertyId));
			}
		}

		assertEquals(expectedMultiKeys, actualMultiKeys);

		// precondition: if the group type id is null
		ContractException contractException = assertThrows(ContractException.class, () -> {
			GroupPropertyReportPluginData.builder().excludeGroupProperty(null,
					TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK);
		});
		assertEquals(GroupError.NULL_GROUP_TYPE_ID, contractException.getErrorType());

		// precondition: if the group property id is null
		contractException = assertThrows(ContractException.class, () -> {
			GroupPropertyReportPluginData.builder().excludeGroupProperty(TestGroupTypeId.GROUP_TYPE_1, null);
		});
		assertEquals(PropertyError.NULL_PROPERTY_ID, contractException.getErrorType());
	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "getReportLabel", args = {})
	public void testGetReportLabel() {
		for (int i = 0; i < 30; i++) {
			ReportLabel expectedReportLabel = new SimpleReportLabel(i);
			GroupPropertyReportPluginData groupPropertyReportPluginData = //
					GroupPropertyReportPluginData.builder()//
							.setReportPeriod(ReportPeriod.DAILY)//
							.setReportLabel(expectedReportLabel)//
							.build();

			assertEquals(expectedReportLabel, groupPropertyReportPluginData.getReportLabel());
		}

	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "getReportPeriod", args = {})
	public void testGetReportPeriod() {
		ReportLabel reportLabel = new SimpleReportLabel("report label");
		for (ReportPeriod reportPeriod : ReportPeriod.values()) {

			GroupPropertyReportPluginData groupPropertyReportPluginData = //
					GroupPropertyReportPluginData.builder()//
							.setReportPeriod(reportPeriod)//
							.setReportLabel(reportLabel)//
							.build();

			assertEquals(reportPeriod, groupPropertyReportPluginData.getReportPeriod());
		}
	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "getIncludedProperties", args = {
			GroupTypeId.class })

	public void testGetIncludedProperties() {
		ReportLabel reportLabel = new SimpleReportLabel("report label");
		ReportPeriod reportPeriod = ReportPeriod.DAILY;

		GroupPropertyReportPluginData groupPropertyReportPluginData = //
				GroupPropertyReportPluginData.builder()//
						.setReportPeriod(reportPeriod)//
						.setReportLabel(reportLabel)//
						.build();
		assertTrue(groupPropertyReportPluginData.getIncludedProperties(TestGroupTypeId.GROUP_TYPE_1).isEmpty());

		// show that inclusion alone works
		Set<TestGroupPropertyId> expectedGroupPropertyIds = new LinkedHashSet<>();
		Set<MultiKey> expectedMultiKeys = new LinkedHashSet<>();

		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK);
		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_1_2_INTEGER_MUTABLE_NO_TRACK);
		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_2_3_DOUBLE_MUTABLE_TRACK);

		GroupPropertyReportPluginData.Builder builder = GroupPropertyReportPluginData.builder()//
				.setReportPeriod(reportPeriod)//
				.setReportLabel(reportLabel);//

		for (TestGroupPropertyId testGroupPropertyId : expectedGroupPropertyIds) {
			builder.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
			expectedMultiKeys.add(new MultiKey(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId));
		}

		Set<MultiKey> actualMultiKeys = new LinkedHashSet<>();
		groupPropertyReportPluginData = builder.build();
		for (GroupTypeId testGroupTypeId : groupPropertyReportPluginData.getGroupTypeIds()) {
			for (GroupPropertyId testGroupPropertyId : groupPropertyReportPluginData
					.getIncludedProperties(testGroupTypeId)) {
				actualMultiKeys.add(new MultiKey(testGroupTypeId, testGroupPropertyId));
			}
		}

		assertEquals(expectedMultiKeys, actualMultiKeys);

		// show that inclusion will override exclusion

		builder = GroupPropertyReportPluginData.builder()//
				.setReportPeriod(reportPeriod)//
				.setReportLabel(reportLabel);//

		for (TestGroupPropertyId testGroupPropertyId : expectedGroupPropertyIds) {
			builder.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
			builder.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
			expectedMultiKeys.add(new MultiKey(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId));
		}

		groupPropertyReportPluginData = builder.build();

		actualMultiKeys = new LinkedHashSet<>();
		groupPropertyReportPluginData = builder.build();
		for (GroupTypeId testGroupTypeId : groupPropertyReportPluginData.getGroupTypeIds()) {
			for (GroupPropertyId testGroupPropertyId : groupPropertyReportPluginData
					.getIncludedProperties(testGroupTypeId)) {
				actualMultiKeys.add(new MultiKey(testGroupTypeId, testGroupPropertyId));
			}
		}

		assertEquals(expectedMultiKeys, actualMultiKeys);

		// precondition: if the group type id is null
		ContractException contractException = assertThrows(ContractException.class, () -> {
			GroupPropertyReportPluginData groupPropertyReportPluginData2 = GroupPropertyReportPluginData.builder()
					.setReportLabel(reportLabel).setReportPeriod(reportPeriod).build();
			groupPropertyReportPluginData2.getIncludedProperties(null);
		});
		assertEquals(GroupError.NULL_GROUP_TYPE_ID, contractException.getErrorType());

	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "getExcludedProperties", args = {
			GroupTypeId.class })
	public void testGetExcludedProperties() {
		ReportLabel reportLabel = new SimpleReportLabel("report label");
		ReportPeriod reportPeriod = ReportPeriod.DAILY;

		GroupPropertyReportPluginData groupPropertyReportPluginData = //
				GroupPropertyReportPluginData.builder()//
						.setReportPeriod(reportPeriod)//
						.setReportLabel(reportLabel)//
						.build();
		assertTrue(groupPropertyReportPluginData.getIncludedProperties(TestGroupTypeId.GROUP_TYPE_1).isEmpty());

		// show that exclusion alone works
		Set<TestGroupPropertyId> expectedGroupPropertyIds = new LinkedHashSet<>();
		Set<MultiKey> expectedMultiKeys = new LinkedHashSet<>();

		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK);
		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_1_2_INTEGER_MUTABLE_NO_TRACK);
		expectedGroupPropertyIds.add(TestGroupPropertyId.GROUP_PROPERTY_2_3_DOUBLE_MUTABLE_TRACK);

		GroupPropertyReportPluginData.Builder builder = GroupPropertyReportPluginData.builder()//
				.setReportPeriod(reportPeriod)//
				.setReportLabel(reportLabel);//

		for (TestGroupPropertyId testGroupPropertyId : expectedGroupPropertyIds) {
			builder.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
			expectedMultiKeys.add(new MultiKey(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId));
		}

		Set<MultiKey> actualMultiKeys = new LinkedHashSet<>();
		groupPropertyReportPluginData = builder.build();
		for (GroupTypeId testGroupTypeId : groupPropertyReportPluginData.getGroupTypeIds()) {
			for (GroupPropertyId testGroupPropertyId : groupPropertyReportPluginData
					.getExcludedProperties(testGroupTypeId)) {
				actualMultiKeys.add(new MultiKey(testGroupTypeId, testGroupPropertyId));
			}
		}

		assertEquals(expectedMultiKeys, actualMultiKeys);

		// show that exclusion will override exclusion

		builder = GroupPropertyReportPluginData.builder()//
				.setReportPeriod(reportPeriod)//
				.setReportLabel(reportLabel);//

		for (TestGroupPropertyId testGroupPropertyId : expectedGroupPropertyIds) {
			builder.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
			builder.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);

			expectedMultiKeys.add(new MultiKey(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId));
		}

		groupPropertyReportPluginData = builder.build();

		actualMultiKeys = new LinkedHashSet<>();
		groupPropertyReportPluginData = builder.build();
		for (GroupTypeId testGroupTypeId : groupPropertyReportPluginData.getGroupTypeIds()) {
			for (GroupPropertyId testGroupPropertyId : groupPropertyReportPluginData
					.getExcludedProperties(testGroupTypeId)) {
				actualMultiKeys.add(new MultiKey(testGroupTypeId, testGroupPropertyId));
			}
		}

		assertEquals(expectedMultiKeys, actualMultiKeys);

		// precondition: if the group type id is null
		ContractException contractException = assertThrows(ContractException.class, () -> {
			GroupPropertyReportPluginData groupPropertyReportPluginData2 = GroupPropertyReportPluginData.builder()
					.setReportLabel(reportLabel).setReportPeriod(reportPeriod).build();
			groupPropertyReportPluginData2.getExcludedProperties(null);
		});
		assertEquals(GroupError.NULL_GROUP_TYPE_ID, contractException.getErrorType());
	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "getDefaultInclusionPolicy", args = {})
	public void testGetDefaultInclusionPolicy() {
		ReportLabel reportLabel = new SimpleReportLabel("report label");
		ReportPeriod reportPeriod = ReportPeriod.DAILY;

		// show the default value is true
		GroupPropertyReportPluginData groupPropertyReportPluginData = //
				GroupPropertyReportPluginData.builder()//
						.setReportPeriod(reportPeriod)//
						.setReportLabel(reportLabel)//
						.build();
		assertEquals(true, groupPropertyReportPluginData.getDefaultInclusionPolicy());

		groupPropertyReportPluginData = //
				GroupPropertyReportPluginData.builder()//
						.setReportPeriod(reportPeriod)//
						.setReportLabel(reportLabel)//
						.setDefaultInclusion(true)//
						.build();
		assertEquals(true, groupPropertyReportPluginData.getDefaultInclusionPolicy());

		groupPropertyReportPluginData = //
				GroupPropertyReportPluginData.builder()//
						.setReportPeriod(reportPeriod)//
						.setReportLabel(reportLabel)//
						.setDefaultInclusion(false)//
						.build();
		assertEquals(false, groupPropertyReportPluginData.getDefaultInclusionPolicy());
	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "toBuilder", args = {})
	public void testToBuilder() {

		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(7759639255438669162L);

		for (int i = 0; i < 10; i++) {

			// build a GroupPropertyReportPluginData from random inputs
			ReportLabel reportLabel = new SimpleReportLabel(randomGenerator.nextInt());
			ReportPeriod reportPeriod = ReportPeriod.values()[randomGenerator.nextInt(ReportPeriod.values().length)];

			GroupPropertyReportPluginData.Builder builder = //
					GroupPropertyReportPluginData.builder()//
							.setReportPeriod(reportPeriod)//
							.setReportLabel(reportLabel);

			for (int j = 0; j < 10; j++) {
				TestGroupPropertyId testGroupPropertyId = TestGroupPropertyId
						.getRandomTestGroupPropertyId(randomGenerator);

				if (testGroupPropertyId != TestGroupPropertyId.GROUP_PROPERTY_3_3_DOUBLE_IMMUTABLE_NO_TRACK) {
					if (randomGenerator.nextBoolean()) {
						builder.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
					} else {
						builder.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
					}
				}
			}

			builder.setDefaultInclusion(randomGenerator.nextBoolean()).build();

			GroupPropertyReportPluginData groupPropertyReportPluginData = builder.build();

			// show that the returned clone builder will build an identical instance if no
			// mutations are made
			GroupPropertyReportPluginData.Builder cloneBuilder = groupPropertyReportPluginData.toBuilder();
			assertNotNull(cloneBuilder);
			assertEquals(groupPropertyReportPluginData, cloneBuilder.build());

			// show that the clone builder builds a distinct instance if any mutation is
			// made

			// setReportLabel
			cloneBuilder = groupPropertyReportPluginData.toBuilder();
			cloneBuilder.setReportLabel(new SimpleReportLabel("report label"));
			assertNotEquals(groupPropertyReportPluginData, cloneBuilder.build());

			// setDefaultInclusion
			cloneBuilder = groupPropertyReportPluginData.toBuilder();
			cloneBuilder.setDefaultInclusion(!groupPropertyReportPluginData.getDefaultInclusionPolicy());
			assertNotEquals(groupPropertyReportPluginData, cloneBuilder.build());

			// setReportPeriod
			cloneBuilder = groupPropertyReportPluginData.toBuilder();
			cloneBuilder.setReportPeriod(reportPeriod.next());
			assertNotEquals(groupPropertyReportPluginData, cloneBuilder.build());

			// includeGroupProperty
			cloneBuilder = groupPropertyReportPluginData.toBuilder();
			cloneBuilder.includeGroupProperty(
					TestGroupPropertyId.GROUP_PROPERTY_3_3_DOUBLE_IMMUTABLE_NO_TRACK.getTestGroupTypeId(),
					TestGroupPropertyId.GROUP_PROPERTY_3_3_DOUBLE_IMMUTABLE_NO_TRACK);
			assertNotEquals(groupPropertyReportPluginData, cloneBuilder.build());

			// includeGroupProperty
			cloneBuilder = groupPropertyReportPluginData.toBuilder();
			cloneBuilder.excludeGroupProperty(
					TestGroupPropertyId.GROUP_PROPERTY_3_3_DOUBLE_IMMUTABLE_NO_TRACK.getTestGroupTypeId(),
					TestGroupPropertyId.GROUP_PROPERTY_3_3_DOUBLE_IMMUTABLE_NO_TRACK);
			assertNotEquals(groupPropertyReportPluginData, cloneBuilder.build());
		}
	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "getVersion", args = {})
	public void testGetVersion() {
		ReportLabel reportLabel = new SimpleReportLabel(0);

		GroupPropertyReportPluginData pluginData = GroupPropertyReportPluginData.builder()
				.setReportLabel(reportLabel)
				.setReportPeriod(ReportPeriod.DAILY)
				.build();

		assertEquals(StandardVersioning.VERSION, pluginData.getVersion());
	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "checkVersionSupported", args = {
			String.class })
	public void testCheckVersionSupported() {
		List<String> versions = Arrays.asList(StandardVersioning.VERSION);

		for (String version : versions) {
			assertTrue(GroupPropertyReportPluginData.checkVersionSupported(version));
			assertFalse(GroupPropertyReportPluginData.checkVersionSupported(version + "badVersion"));
			assertFalse(GroupPropertyReportPluginData.checkVersionSupported("badVersion"));
			assertFalse(GroupPropertyReportPluginData.checkVersionSupported(version + "0"));
			assertFalse(GroupPropertyReportPluginData.checkVersionSupported(version + ".0.0"));
		}
	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "equals", args = { Object.class })
	public void testEquals() {

		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(7759639255438669162L);
		for (int i = 0; i < 10; i++) {
			// build a GroupPropertyReportPluginData from the same random
			// inputs
			GroupPropertyReportPluginData.Builder builder1 = GroupPropertyReportPluginData.builder();
			GroupPropertyReportPluginData.Builder builder2 = GroupPropertyReportPluginData.builder();

			ReportLabel reportLabel = new SimpleReportLabel(randomGenerator.nextInt(100));
			builder1.setReportLabel(reportLabel);
			builder2.setReportLabel(reportLabel);

			ReportPeriod reportPeriod = ReportPeriod.values()[randomGenerator.nextInt(ReportPeriod.values().length)];
			builder1.setReportPeriod(reportPeriod);
			builder2.setReportPeriod(reportPeriod);

			for (int j = 0; j < 10; j++) {
				TestGroupPropertyId testGroupPropertyId = TestGroupPropertyId
						.getRandomTestGroupPropertyId(randomGenerator);
				if (randomGenerator.nextBoolean()) {
					builder1.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
					builder2.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
				} else {
					builder1.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
					builder2.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
				}
			}

			boolean defaultInclusion = randomGenerator.nextBoolean();
			builder1.setDefaultInclusion(defaultInclusion).build();
			builder2.setDefaultInclusion(defaultInclusion).build();

			GroupPropertyReportPluginData groupPropertyReportPluginData1 = builder1.build();
			GroupPropertyReportPluginData groupPropertyReportPluginData2 = builder2.build();

			assertEquals(groupPropertyReportPluginData1, groupPropertyReportPluginData2);

			// show that plugin datas with different inputs are not equal

			// change the default inclusion
			groupPropertyReportPluginData2 = //
					groupPropertyReportPluginData1.toBuilder()//
							.setDefaultInclusion(!defaultInclusion)//
							.build();
			assertNotEquals(groupPropertyReportPluginData2, groupPropertyReportPluginData1);

			// change the report period
			int ord = reportPeriod.ordinal() + 1;
			ord = ord % ReportPeriod.values().length;
			reportPeriod = ReportPeriod.values()[ord];
			groupPropertyReportPluginData2 = //
					groupPropertyReportPluginData1.toBuilder()//
							.setReportPeriod(reportPeriod)//
							.build();
			assertNotEquals(groupPropertyReportPluginData2, groupPropertyReportPluginData1);

			// change the report label
			reportLabel = new SimpleReportLabel(1000);
			groupPropertyReportPluginData2 = //
					groupPropertyReportPluginData1.toBuilder()//
							.setReportLabel(reportLabel)//
							.build();
			assertNotEquals(groupPropertyReportPluginData2, groupPropertyReportPluginData1);

			// change an included property id
			for (GroupTypeId testGroupTypeId : groupPropertyReportPluginData1.getGroupTypeIds()) {
				if (!groupPropertyReportPluginData1.getIncludedProperties(testGroupTypeId).isEmpty()) {
					GroupPropertyId testGroupPropertyId = groupPropertyReportPluginData1
							.getIncludedProperties(testGroupTypeId).iterator().next();
					groupPropertyReportPluginData2 = //
							groupPropertyReportPluginData1.toBuilder()//
									.excludeGroupProperty(testGroupTypeId, testGroupPropertyId)//
									.build();
					assertNotEquals(groupPropertyReportPluginData2, groupPropertyReportPluginData1);
				}
			}
			// change an excluded property id
			for (GroupTypeId testGroupTypeId : groupPropertyReportPluginData1.getGroupTypeIds()) {
				if (!groupPropertyReportPluginData1.getExcludedProperties(testGroupTypeId).isEmpty()) {
					GroupPropertyId testGroupPropertyId = groupPropertyReportPluginData1
							.getExcludedProperties(testGroupTypeId).iterator().next();
					groupPropertyReportPluginData2 = //
							groupPropertyReportPluginData1.toBuilder()//
									.includeGroupProperty(testGroupTypeId, testGroupPropertyId)//
									.build();
					assertNotEquals(groupPropertyReportPluginData2, groupPropertyReportPluginData1);
				}
			}
		}

	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "hashCode", args = {})
	public void testHashCode() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(9079768427072825406L);

		Set<Integer> observedHashCodes = new LinkedHashSet<>();
		for (int i = 0; i < 50; i++) {
			// build a GroupPropertyReportPluginData from the same random
			// inputs
			GroupPropertyReportPluginData.Builder builder1 = GroupPropertyReportPluginData.builder();
			GroupPropertyReportPluginData.Builder builder2 = GroupPropertyReportPluginData.builder();

			ReportLabel reportLabel = new SimpleReportLabel(randomGenerator.nextInt(100));
			builder1.setReportLabel(reportLabel);
			builder2.setReportLabel(reportLabel);

			ReportPeriod reportPeriod = ReportPeriod.values()[randomGenerator.nextInt(ReportPeriod.values().length)];
			builder1.setReportPeriod(reportPeriod);
			builder2.setReportPeriod(reportPeriod);

			for (int j = 0; j < 10; j++) {
				TestGroupPropertyId testGroupPropertyId = TestGroupPropertyId
						.getRandomTestGroupPropertyId(randomGenerator);
				if (randomGenerator.nextBoolean()) {
					builder1.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
					builder2.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
				} else {
					builder1.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
					builder2.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
				}
			}

			boolean defaultInclusion = randomGenerator.nextBoolean();
			builder1.setDefaultInclusion(defaultInclusion).build();
			builder2.setDefaultInclusion(defaultInclusion).build();

			GroupPropertyReportPluginData groupPropertyReportPluginData1 = builder1.build();
			GroupPropertyReportPluginData groupPropertyReportPluginData2 = builder2.build();

			// show that the hash code is stable
			int hashCode = groupPropertyReportPluginData1.hashCode();
			assertEquals(hashCode, groupPropertyReportPluginData1.hashCode());
			assertEquals(hashCode, groupPropertyReportPluginData1.hashCode());
			assertEquals(hashCode, groupPropertyReportPluginData1.hashCode());
			assertEquals(hashCode, groupPropertyReportPluginData1.hashCode());

			// show that equal objects have equal hash codes
			assertEquals(groupPropertyReportPluginData1.hashCode(), groupPropertyReportPluginData2.hashCode());

			// collect the hashcode
			observedHashCodes.add(groupPropertyReportPluginData1.hashCode());
		}

		/*
		 * The hash codes should be dispersed -- we only show that they are unique
		 * values -- this is dependent on the random seed
		 */
		assertEquals(50, observedHashCodes.size());

	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "getGroupTypeIds", args = {})
	public void testGetGroupTypeIds() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(626906625322362256L);

		for (int i = 0; i < 50; i++) {
			GroupPropertyReportPluginData.Builder builder1 = GroupPropertyReportPluginData.builder();

			ReportLabel reportLabel = new SimpleReportLabel(randomGenerator.nextInt(100));
			builder1.setReportLabel(reportLabel);

			ReportPeriod reportPeriod = ReportPeriod.values()[randomGenerator.nextInt(ReportPeriod.values().length)];
			builder1.setReportPeriod(reportPeriod);

			Set<GroupTypeId> expectedGroupTypeIds = new LinkedHashSet<>();

			int propertyCount = randomGenerator.nextInt(3) + 1;
			for (int j = 0; j < propertyCount; j++) {
				TestGroupPropertyId testGroupPropertyId = TestGroupPropertyId
						.getRandomTestGroupPropertyId(randomGenerator);
				expectedGroupTypeIds.add(testGroupPropertyId.getTestGroupTypeId());
				if (randomGenerator.nextBoolean()) {
					builder1.includeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
				} else {
					builder1.excludeGroupProperty(testGroupPropertyId.getTestGroupTypeId(), testGroupPropertyId);
				}
			}

			boolean defaultInclusion = randomGenerator.nextBoolean();
			builder1.setDefaultInclusion(defaultInclusion).build();

			GroupPropertyReportPluginData groupPropertyReportPluginData = builder1.build();

			assertEquals(expectedGroupTypeIds, groupPropertyReportPluginData.getGroupTypeIds());
		}

	}

	@Test
	@UnitTestMethod(target = GroupPropertyReportPluginData.class, name = "toString", args = {})
	public void testToString() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(2394011517139293620L);
		for (int i = 0; i < 10; i++) {
			GroupPropertyReportPluginData.Builder builder = GroupPropertyReportPluginData.builder();

			ReportLabel reportLabel = new SimpleReportLabel(randomGenerator.nextInt(100));
			builder.setReportLabel(reportLabel);

			ReportPeriod reportPeriod = ReportPeriod.values()[randomGenerator.nextInt(ReportPeriod.values().length)];
			builder.setReportPeriod(reportPeriod);

			Map<GroupTypeId, Set<GroupPropertyId>> includedIds = new LinkedHashMap<>();
			Map<GroupTypeId, Set<GroupPropertyId>> excludedIds = new LinkedHashMap<>();

			for (TestGroupTypeId testGroupTypeId : TestGroupTypeId.getShuffledTestGroupTypeIds(randomGenerator)) {
				for (TestGroupPropertyId testGroupPropertyId : TestGroupPropertyId
						.getShuffledTestGroupPropertyIds(testGroupTypeId, randomGenerator)) {
					if (randomGenerator.nextBoolean()) {
						builder.includeGroupProperty(testGroupTypeId, testGroupPropertyId);
						Set<GroupPropertyId> set = includedIds.get(testGroupTypeId);
						if (set == null) {
							set = new LinkedHashSet<>();
							includedIds.put(testGroupTypeId, set);
						}
						set.add(testGroupPropertyId);
						set = excludedIds.get(testGroupTypeId);
						if (set != null) {
							set.remove(testGroupPropertyId);
						}
					} else {
						builder.excludeGroupProperty(testGroupTypeId, testGroupPropertyId);
						Set<GroupPropertyId> set = excludedIds.get(testGroupTypeId);
						if (set == null) {
							set = new LinkedHashSet<>();
							excludedIds.put(testGroupTypeId, set);
						}
						set.add(testGroupPropertyId);
						set = includedIds.get(testGroupTypeId);
						if (set != null) {
							set.remove(testGroupPropertyId);
						}
					}
				}
			}

			boolean defaultInclusion = randomGenerator.nextBoolean();
			builder.setDefaultInclusion(defaultInclusion).build();

			GroupPropertyReportPluginData groupPropertyReportPluginData = builder.build();

			StringBuilder sb = new StringBuilder();
			sb.append("GroupPropertyReportPluginData [data=");

			StringBuilder superDataBuilder = new StringBuilder();
			superDataBuilder.append("Data [reportLabel=");
			superDataBuilder.append(reportLabel);
			superDataBuilder.append(", reportPeriod=");
			superDataBuilder.append(reportPeriod);

			StringBuilder dataBuilder = new StringBuilder();
			dataBuilder.append(superDataBuilder.toString());
			dataBuilder.append(", includedProperties=");
			dataBuilder.append(includedIds);
			dataBuilder.append(", excludedProperties=");
			dataBuilder.append(excludedIds);
			dataBuilder.append(", defaultInclusionPolicy=");
			dataBuilder.append(defaultInclusion);
			dataBuilder.append("]");

			sb.append(dataBuilder.toString());
			sb.append("]");

			assertEquals(sb.toString(), groupPropertyReportPluginData.toString());
		}
	}
}
