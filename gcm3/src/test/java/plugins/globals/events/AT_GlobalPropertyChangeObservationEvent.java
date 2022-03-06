package plugins.globals.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import nucleus.ActorContext;
import nucleus.EventLabel;
import nucleus.EventLabeler;
import nucleus.Plugin;
import nucleus.Simulation;
import nucleus.Simulation.Builder;
import nucleus.SimulationContext;
import nucleus.testsupport.testplugin.ScenarioPlanCompletionObserver;
import nucleus.testsupport.testplugin.TestActorPlan;
import nucleus.testsupport.testplugin.TestPlugin;
import nucleus.testsupport.testplugin.TestPluginData;
import plugins.globals.GlobalPlugin;
import plugins.globals.GlobalPluginData;
import plugins.globals.support.GlobalPropertyId;
import plugins.globals.support.SimpleGlobalPropertyId;
import plugins.globals.testsupport.TestGlobalPropertyId;
import plugins.properties.PropertiesPlugin;
import plugins.properties.support.PropertyDefinition;
import plugins.reports.ReportsPlugin;
import plugins.reports.ReportsPluginData;
import util.annotations.UnitTest;
import util.annotations.UnitTestConstructor;
import util.annotations.UnitTestMethod;

@UnitTest(target = GlobalPropertyChangeObservationEvent.class)

public class AT_GlobalPropertyChangeObservationEvent {

	@Test
	@UnitTestConstructor(args = { GlobalPropertyId.class, Object.class, Object.class })
	public void testConstructor() {
		GlobalPropertyId globalPropertyId = new SimpleGlobalPropertyId("id");
		Integer previousValue = 12;
		Integer currentValue = 13;
		GlobalPropertyChangeObservationEvent globalPropertyChangeObservationEvent = new GlobalPropertyChangeObservationEvent(globalPropertyId, previousValue, currentValue);

		assertNotNull(globalPropertyChangeObservationEvent);

	}

	@Test
	@UnitTestMethod(name = "getGlobalPropertyId", args = { GlobalPropertyId.class, Object.class, Object.class })
	public void testGetGlobalPropertyId() {
		GlobalPropertyId globalPropertyId = new SimpleGlobalPropertyId("id");
		Integer previousValue = 12;
		Integer currentValue = 13;
		GlobalPropertyChangeObservationEvent globalPropertyChangeObservationEvent = new GlobalPropertyChangeObservationEvent(globalPropertyId, previousValue, currentValue);

		assertEquals(globalPropertyId, globalPropertyChangeObservationEvent.getGlobalPropertyId());

	}

	@Test
	@UnitTestMethod(name = "getGlobalPropertyId", args = { GlobalPropertyId.class, Object.class, Object.class })
	public void testGetPreviousPropertyValue() {
		GlobalPropertyId globalPropertyId = new SimpleGlobalPropertyId("id");
		Integer previousValue = 12;
		Integer currentValue = 13;
		GlobalPropertyChangeObservationEvent globalPropertyChangeObservationEvent = new GlobalPropertyChangeObservationEvent(globalPropertyId, previousValue, currentValue);

		assertEquals(previousValue, globalPropertyChangeObservationEvent.getPreviousPropertyValue());
	}

	@Test
	@UnitTestMethod(name = "getCurrentPropertyValue", args = {})
	public void testGetCurrentPropertyValue() {
		GlobalPropertyId globalPropertyId = new SimpleGlobalPropertyId("id");
		Integer previousValue = 12;
		Integer currentValue = 13;
		GlobalPropertyChangeObservationEvent globalPropertyChangeObservationEvent = new GlobalPropertyChangeObservationEvent(globalPropertyId, previousValue, currentValue);

		assertEquals(currentValue, globalPropertyChangeObservationEvent.getCurrentPropertyValue());
	}

	@Test
	@UnitTestMethod(name = "toString", args = {})
	public void testToString() {
		GlobalPropertyId globalPropertyId = new SimpleGlobalPropertyId("id");
		Integer previousValue = 12;
		Integer currentValue = 13;
		GlobalPropertyChangeObservationEvent globalPropertyChangeObservationEvent = new GlobalPropertyChangeObservationEvent(globalPropertyId, previousValue, currentValue);
		String expectedValue = "GlobalPropertyChangeObservationEvent [globalPropertyId=id, previousPropertyValue=12, currentPropertyValue=13]";
		String actualValue = globalPropertyChangeObservationEvent.toString();

		assertEquals(expectedValue, actualValue);

	}

	@Test
	@UnitTestMethod(name = "getEventLabel", args = { SimulationContext.class, GlobalPropertyId.class })
	public void testGetEventLabel() {
		testConsumer((c) -> {
			for (TestGlobalPropertyId testGlobalPropertyId : TestGlobalPropertyId.values()) {
				EventLabel<GlobalPropertyChangeObservationEvent> eventLabel = GlobalPropertyChangeObservationEvent.getEventLabel(c, testGlobalPropertyId);
				assertEquals(GlobalPropertyChangeObservationEvent.class, eventLabel.getEventClass());
				assertEquals(testGlobalPropertyId, eventLabel.getPrimaryKeyValue());
				assertEquals(GlobalPropertyChangeObservationEvent.getEventLabeler().getId(), eventLabel.getLabelerId());
			}
		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabeler", args = {})
	public void testGetEventLabeler() {
		testConsumer((c) -> {

			EventLabeler<GlobalPropertyChangeObservationEvent> eventLabeler = GlobalPropertyChangeObservationEvent.getEventLabeler();

			// show that the event labeler has the expected event class
			assertEquals(GlobalPropertyChangeObservationEvent.class, eventLabeler.getEventClass());

			for (TestGlobalPropertyId testGlobalPropertyId : TestGlobalPropertyId.values()) {
				// show that the event labeler id matches the labeler id
				// associated with the corresponding event label
				EventLabel<GlobalPropertyChangeObservationEvent> eventLabel = GlobalPropertyChangeObservationEvent.getEventLabel(c, testGlobalPropertyId);
				assertEquals(eventLabel.getLabelerId(), eventLabeler.getId());

				// show that the event labeler produces the expected event
				// label

				// create an event
				GlobalPropertyChangeObservationEvent event = new GlobalPropertyChangeObservationEvent(testGlobalPropertyId, 30, 40);

				// derive the expected event label for this event
				EventLabel<GlobalPropertyChangeObservationEvent> expectedEventLabel = GlobalPropertyChangeObservationEvent.getEventLabel(c, testGlobalPropertyId);

				// have the event labeler produce an event label and show it
				// is equal to the expected event label
				EventLabel<GlobalPropertyChangeObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);
				assertEquals(expectedEventLabel, actualEventLabel);

			}
		});
	}

	@Test
	@UnitTestMethod(name = "getPrimaryKeyValue", args = {})
	public void testGetPrimaryKeyValue() {
		GlobalPropertyId globalPropertyId = new SimpleGlobalPropertyId("id");
		Integer previousValue = 12;
		Integer currentValue = 13;
		GlobalPropertyChangeObservationEvent globalPropertyChangeObservationEvent = new GlobalPropertyChangeObservationEvent(globalPropertyId, previousValue, currentValue);
		assertEquals(globalPropertyId, globalPropertyChangeObservationEvent.getPrimaryKeyValue());
	}

	/*
	 * Runs the engine by loading all plugins necessary to support compartments
	 * and executes the given consumer as an AgentActionPlan.
	 */
	private void testConsumer(Consumer<ActorContext> consumer) {

		Builder builder = Simulation.builder();

		GlobalPluginData.Builder initialDatabuilder = GlobalPluginData.builder();
		int defaultValue = 0;
		for (TestGlobalPropertyId testGlobalPropertyId : TestGlobalPropertyId.values()) {
			PropertyDefinition propertyDefinition = PropertyDefinition.builder().setDefaultValue(defaultValue++).setType(Integer.class).build();
			initialDatabuilder.defineGlobalProperty(testGlobalPropertyId, propertyDefinition);
		}
		GlobalPluginData globalInitialData = initialDatabuilder.build();

		builder.addPlugin(GlobalPlugin.getPlugin(globalInitialData));
		builder.addPlugin(ReportsPlugin.getReportPlugin(ReportsPluginData.builder().build()));
		builder.addPlugin(PropertiesPlugin.getPlugin());
		

		TestPluginData.Builder pluginBuilder = TestPluginData.builder();

		
		pluginBuilder.addTestActorPlan("agent", new TestActorPlan(0, consumer));

		TestPluginData testPluginData = pluginBuilder.build();
		Plugin testPlugin = TestPlugin.getPlugin(testPluginData);
		builder.addPlugin(testPlugin);

		// build and execute the engine
		ScenarioPlanCompletionObserver scenarioPlanCompletionObserver = new ScenarioPlanCompletionObserver();
		builder.setOutputConsumer(scenarioPlanCompletionObserver::handleOutput).build().execute();

		// show that all actions were executed
		assertTrue(scenarioPlanCompletionObserver.allPlansExecuted());

	}

}
