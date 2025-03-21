package gov.hhs.aspr.ms.gcm.simulation.plugins.resources.reports;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.gcm.simulation.nucleus.ReportContext;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.testsupport.testplugin.TestActorPlan;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.testsupport.testplugin.TestOutputConsumer;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.testsupport.testplugin.TestPluginData;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.testsupport.testplugin.TestSimulation;
import gov.hhs.aspr.ms.gcm.simulation.plugins.properties.support.PropertyDefinition;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportHeader;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportItem;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.ReportLabel;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.SimpleReportLabel;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.datamanagers.ResourcesDataManager;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.datamanagers.ResourcesPluginData;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.support.ResourcePropertyId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.support.ResourcePropertyInitialization;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.testsupport.ResourcesTestPluginFactory;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.testsupport.ResourcesTestPluginFactory.Factory;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.testsupport.TestResourceId;
import gov.hhs.aspr.ms.gcm.simulation.plugins.resources.testsupport.TestResourcePropertyId;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_ResourcePropertyReport {

	public enum TestAuxiliaryResourcePropertyId implements ResourcePropertyId {

		AUX_RESOURCE_PROPERTY_1_BOOLEAN_MUTABLE(TestResourceId.RESOURCE_1,
				PropertyDefinition.builder().setType(Boolean.class).setDefaultValue(false).build()),
		AUX_RESOURCE_PROPERTY_2_INTEGER_MUTABLE(TestResourceId.RESOURCE_2,
				PropertyDefinition.builder().setType(Integer.class).setDefaultValue(0).build());

		private final TestResourceId testResourceId;
		private final PropertyDefinition propertyDefinition;

		public PropertyDefinition getPropertyDefinition() {
			return propertyDefinition;
		}

		private TestAuxiliaryResourcePropertyId(TestResourceId testResourceId, PropertyDefinition propertyDefinition) {
			this.testResourceId = testResourceId;
			this.propertyDefinition = propertyDefinition;
		}

		public TestResourceId getTestResourceId() {
			return testResourceId;
		}

	}

	@Test
	@UnitTestConstructor(target = ResourcePropertyReport.class, args = { ResourcePropertyReportPluginData.class })
	public void testConstructor() {
		// nothing to test
	}

	@Test
	@UnitTestMethod(target = ResourcePropertyReport.class, name = "init", args = { ReportContext.class })
	public void testInit() {
		int initialPopulation = 20;

		TestPluginData.Builder pluginBuilder = TestPluginData.builder();

		/*
		 * create an agent and have it assign various resource properties at
		 * various times
		 */

		pluginBuilder.addTestActorPlan("actor", new TestActorPlan(0.0, (c) -> {
			/*
			 * note that this is time 0 and should show that property initial
			 * values are still reported correctly
			 */
			ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_3,
					TestResourcePropertyId.ResourceProperty_3_2_STRING_MUTABLE, "A");
		}));

		pluginBuilder.addTestActorPlan("actor", new TestActorPlan(1.0, (c) -> {
			// two settings of the same property
			ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_2,
					TestResourcePropertyId.ResourceProperty_2_2_INTEGER_MUTABLE, 45);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestResourcePropertyId.ResourceProperty_1_3_DOUBLE_MUTABLE, 36.7);
		}));

		pluginBuilder.addTestActorPlan("actor", new TestActorPlan(2.0, (c) -> {
			ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_4,
					TestResourcePropertyId.ResourceProperty_4_1_BOOLEAN_MUTABLE, true);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_2,
					TestResourcePropertyId.ResourceProperty_2_1_BOOLEAN_MUTABLE, false);

			// add new property definitions
			for (TestAuxiliaryResourcePropertyId propertyId : TestAuxiliaryResourcePropertyId.values()) {
				TestResourceId testResourceId = propertyId.getTestResourceId();
				PropertyDefinition propertyDefinition = propertyId.getPropertyDefinition();
				ResourcePropertyInitialization resourcePropertyInitialization = ResourcePropertyInitialization.builder()
						.setResourceId(testResourceId).setResourcePropertyId(propertyId)
						.setPropertyDefinition(propertyDefinition).build();
				resourcesDataManager.defineResourceProperty(resourcePropertyInitialization);
			}

		}));

		pluginBuilder.addTestActorPlan("actor", new TestActorPlan(3.0, (c) -> {
			ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_4,
					TestResourcePropertyId.ResourceProperty_4_1_BOOLEAN_MUTABLE, true);

			// note the duplicated value
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestResourcePropertyId.ResourceProperty_1_3_DOUBLE_MUTABLE, 2.5);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestResourcePropertyId.ResourceProperty_1_3_DOUBLE_MUTABLE, 2.5);

			// and now a third setting of the same property to a new value
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestResourcePropertyId.ResourceProperty_1_2_INTEGER_MUTABLE, 100);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestResourcePropertyId.ResourceProperty_1_2_INTEGER_MUTABLE, 60);

			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestAuxiliaryResourcePropertyId.AUX_RESOURCE_PROPERTY_1_BOOLEAN_MUTABLE, true);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_2,
					TestAuxiliaryResourcePropertyId.AUX_RESOURCE_PROPERTY_2_INTEGER_MUTABLE, 137);
		}));

		TestPluginData testPluginData = pluginBuilder.build();

		/*
		 * Collect the expected report items. Note that order does not matter. *
		 */
		Map<ReportItem, Integer> expectedReportItems = new LinkedHashMap<>();
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_1,
				TestResourcePropertyId.ResourceProperty_1_1_BOOLEAN_MUTABLE, false), 1);
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_1,
				TestResourcePropertyId.ResourceProperty_1_2_INTEGER_MUTABLE, 0), 1);
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_1,
				TestResourcePropertyId.ResourceProperty_1_3_DOUBLE_MUTABLE, 0.0), 1);
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_2,
				TestResourcePropertyId.ResourceProperty_2_1_BOOLEAN_MUTABLE, true), 1);
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_2,
				TestResourcePropertyId.ResourceProperty_2_2_INTEGER_MUTABLE, 5), 1);
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_3,
				TestResourcePropertyId.ResourceProperty_3_1_BOOLEAN_MUTABLE, false), 1);
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_3,
				TestResourcePropertyId.ResourceProperty_3_2_STRING_MUTABLE, ""), 1);
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_4,
				TestResourcePropertyId.ResourceProperty_4_1_BOOLEAN_MUTABLE, true), 1);
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_5,
				TestResourcePropertyId.ResourceProperty_5_1_INTEGER_IMMUTABLE, 7), 1);
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_5,
				TestResourcePropertyId.ResourceProperty_5_2_DOUBLE_IMMUTABLE, 2.7), 1);
		expectedReportItems.put(getReportItem(0.0, TestResourceId.RESOURCE_3,
				TestResourcePropertyId.ResourceProperty_3_2_STRING_MUTABLE, "A"), 1);
		expectedReportItems.put(getReportItem(1.0, TestResourceId.RESOURCE_2,
				TestResourcePropertyId.ResourceProperty_2_2_INTEGER_MUTABLE, 45), 1);
		expectedReportItems.put(getReportItem(1.0, TestResourceId.RESOURCE_1,
				TestResourcePropertyId.ResourceProperty_1_3_DOUBLE_MUTABLE, 36.7), 1);
		expectedReportItems.put(getReportItem(2.0, TestResourceId.RESOURCE_4,
				TestResourcePropertyId.ResourceProperty_4_1_BOOLEAN_MUTABLE, true), 1);
		expectedReportItems.put(getReportItem(2.0, TestResourceId.RESOURCE_2,
				TestResourcePropertyId.ResourceProperty_2_1_BOOLEAN_MUTABLE, false), 1);
		expectedReportItems.put(getReportItem(3.0, TestResourceId.RESOURCE_4,
				TestResourcePropertyId.ResourceProperty_4_1_BOOLEAN_MUTABLE, true), 1);
		expectedReportItems.put(getReportItem(3.0, TestResourceId.RESOURCE_1,
				TestResourcePropertyId.ResourceProperty_1_3_DOUBLE_MUTABLE, 2.5), 2);
		expectedReportItems.put(getReportItem(3.0, TestResourceId.RESOURCE_1,
				TestResourcePropertyId.ResourceProperty_1_2_INTEGER_MUTABLE, 100), 1);
		expectedReportItems.put(getReportItem(3.0, TestResourceId.RESOURCE_1,
				TestResourcePropertyId.ResourceProperty_1_2_INTEGER_MUTABLE, 60), 1);
		expectedReportItems.put(getReportItem(2.0, TestResourceId.RESOURCE_1,
				TestAuxiliaryResourcePropertyId.AUX_RESOURCE_PROPERTY_1_BOOLEAN_MUTABLE, false), 1);
		expectedReportItems.put(getReportItem(2.0, TestResourceId.RESOURCE_2,
				TestAuxiliaryResourcePropertyId.AUX_RESOURCE_PROPERTY_2_INTEGER_MUTABLE, 0), 1);
		expectedReportItems.put(getReportItem(3.0, TestResourceId.RESOURCE_1,
				TestAuxiliaryResourcePropertyId.AUX_RESOURCE_PROPERTY_1_BOOLEAN_MUTABLE, true), 1);
		expectedReportItems.put(getReportItem(3.0, TestResourceId.RESOURCE_2,
				TestAuxiliaryResourcePropertyId.AUX_RESOURCE_PROPERTY_2_INTEGER_MUTABLE, 137), 1);


		ResourcesPluginData.Builder resourcesBuilder = ResourcesPluginData.builder();

		for (TestResourceId testResourceId : TestResourceId.values()) {
			resourcesBuilder.addResource(testResourceId,0.0, testResourceId.getTimeTrackingPolicy());			
		}

		for (TestResourcePropertyId testResourcePropertyId : TestResourcePropertyId.values()) {
			TestResourceId testResourceId = testResourcePropertyId.getTestResourceId();
			PropertyDefinition propertyDefinition = testResourcePropertyId.getPropertyDefinition();
			resourcesBuilder.defineResourceProperty(testResourceId, testResourcePropertyId, propertyDefinition);
		}

		ResourcesPluginData resourcesPluginData = resourcesBuilder.build();

		 
		Factory factory = ResourcesTestPluginFactory.factory(initialPopulation, 8914112012010329946L, testPluginData);
		factory.setResourcesPluginData(resourcesPluginData);
		ResourcePropertyReportPluginData resourcePropertyReportPluginData = ResourcePropertyReportPluginData.builder().setReportLabel(REPORT_LABEL).build();
		factory.setResourcePropertyReportPluginData(resourcePropertyReportPluginData);
		
		TestOutputConsumer testOutputConsumer = TestSimulation	.builder()//
				.addPlugins(factory.getPlugins())//
				.build()//
				.execute();
		
		Map<ReportItem, Integer> actualReportItems = testOutputConsumer.getOutputItemMap(ReportItem.class);
		
		assertEquals(expectedReportItems, actualReportItems);

		ReportHeader reportHeader = testOutputConsumer.getOutputItem(ReportHeader.class).get();
		assertEquals(REPORT_HEADER, reportHeader);
	}

	@Test
	@UnitTestMethod(target = ResourcePropertyReport.class, name = "init", args = { ReportContext.class })
	public void testInit_State() {
		// Test with producing simulation state

		int initialPopulation = 20;

		TestPluginData.Builder pluginBuilder = TestPluginData.builder();

		/*
		 * create an agent and have it assign various resource properties at
		 * various times
		 */

		pluginBuilder.addTestActorPlan("actor", new TestActorPlan(0.0, (c) -> {
			/*
			 * note that this is time 0 and should show that property initial
			 * values are still reported correctly
			 */
			ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_3,
					TestResourcePropertyId.ResourceProperty_3_2_STRING_MUTABLE, "A");
		}));

		pluginBuilder.addTestActorPlan("actor", new TestActorPlan(1.0, (c) -> {
			// two settings of the same property
			ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_2,
					TestResourcePropertyId.ResourceProperty_2_2_INTEGER_MUTABLE, 45);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestResourcePropertyId.ResourceProperty_1_3_DOUBLE_MUTABLE, 36.7);
		}));

		pluginBuilder.addTestActorPlan("actor", new TestActorPlan(2.0, (c) -> {
			ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_4,
					TestResourcePropertyId.ResourceProperty_4_1_BOOLEAN_MUTABLE, true);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_2,
					TestResourcePropertyId.ResourceProperty_2_1_BOOLEAN_MUTABLE, false);

			// add new property definitions
			for (TestAuxiliaryResourcePropertyId propertyId : TestAuxiliaryResourcePropertyId.values()) {
				TestResourceId testResourceId = propertyId.getTestResourceId();
				PropertyDefinition propertyDefinition = propertyId.getPropertyDefinition();
				ResourcePropertyInitialization resourcePropertyInitialization = ResourcePropertyInitialization.builder()
						.setResourceId(testResourceId).setResourcePropertyId(propertyId)
						.setPropertyDefinition(propertyDefinition).build();
				resourcesDataManager.defineResourceProperty(resourcePropertyInitialization);
			}

		}));

		pluginBuilder.addTestActorPlan("actor", new TestActorPlan(3.0, (c) -> {
			ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_4,
					TestResourcePropertyId.ResourceProperty_4_1_BOOLEAN_MUTABLE, true);

			// note the duplicated value
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestResourcePropertyId.ResourceProperty_1_3_DOUBLE_MUTABLE, 2.5);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestResourcePropertyId.ResourceProperty_1_3_DOUBLE_MUTABLE, 2.5);

			// and now a third setting of the same property to a new value
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestResourcePropertyId.ResourceProperty_1_2_INTEGER_MUTABLE, 100);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestResourcePropertyId.ResourceProperty_1_2_INTEGER_MUTABLE, 60);

			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_1,
					TestAuxiliaryResourcePropertyId.AUX_RESOURCE_PROPERTY_1_BOOLEAN_MUTABLE, true);
			resourcesDataManager.setResourcePropertyValue(TestResourceId.RESOURCE_2,
					TestAuxiliaryResourcePropertyId.AUX_RESOURCE_PROPERTY_2_INTEGER_MUTABLE, 137);
		}));

		TestPluginData testPluginData = pluginBuilder.build();

		Factory factory = ResourcesTestPluginFactory.factory(initialPopulation, 8914112012010329946L, testPluginData);
		ResourcePropertyReportPluginData resourcePropertyReportPluginData = ResourcePropertyReportPluginData.builder()
				.setReportLabel(REPORT_LABEL)
				.build();
		factory.setResourcePropertyReportPluginData(resourcePropertyReportPluginData);

		TestOutputConsumer testOutputConsumer = TestSimulation	.builder()//
				.addPlugins(factory.getPlugins())//
				.setProduceSimulationStateOnHalt(true)//
				.setSimulationHaltTime(20)//
				.build()//
				.execute();

		Map<ResourcePropertyReportPluginData, Integer> outputItems = testOutputConsumer.getOutputItemMap(ResourcePropertyReportPluginData.class);
		assertEquals(1, outputItems.size());
		ResourcePropertyReportPluginData resourcePropertyReportPluginData2 = outputItems.keySet().iterator().next();
		assertEquals(resourcePropertyReportPluginData, resourcePropertyReportPluginData2);

		// Test without producing simulation state

		testOutputConsumer = TestSimulation	.builder()//
				.addPlugins(factory.getPlugins())//
				.setProduceSimulationStateOnHalt(false)//
				.setSimulationHaltTime(20)//
				.build()//
				.execute();

		outputItems = testOutputConsumer.getOutputItemMap(ResourcePropertyReportPluginData.class);
		assertEquals(0, outputItems.size());
	}

	private static ReportItem getReportItem(Object... values) {
		ReportItem.Builder builder = ReportItem.builder();
		builder.setReportLabel(REPORT_LABEL);
		for (Object value : values) {
			builder.addValue(value);
		}
		return builder.build();
	}

	private static final ReportLabel REPORT_LABEL = new SimpleReportLabel("resource property report");

	private static final ReportHeader REPORT_HEADER = ReportHeader.builder().setReportLabel(REPORT_LABEL).add("time").add("resource").add("property")
			.add("value").build();
}
