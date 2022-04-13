package plugins.regions.events;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import nucleus.EventLabel;
import nucleus.EventLabeler;
import nucleus.SimulationContext;
import plugins.regions.support.RegionId;
import plugins.regions.support.RegionPropertyId;
import plugins.regions.testsupport.RegionsActionSupport;
import plugins.regions.testsupport.TestRegionId;
import plugins.regions.testsupport.TestRegionPropertyId;
import plugins.util.properties.TimeTrackingPolicy;
import tools.annotations.UnitTest;
import tools.annotations.UnitTestConstructor;
import tools.annotations.UnitTestMethod;

@UnitTest(target = RegionPropertyChangeObservationEvent.class)
public class AT_RegionPropertyChangeObservationEvent {

	@Test
	@UnitTestConstructor(args = { RegionId.class, RegionPropertyId.class, Object.class, Object.class })
	public void testContstructor() {
		// Nothing to test here. All fields covered by other tests.
	}

	@Test
	@UnitTestMethod(name = "getRegionId", args = { RegionId.class, RegionPropertyId.class, Object.class, Object.class })
	public void testGetRegionId() {
		for (TestRegionId testRegionId : TestRegionId.values()) {
			RegionPropertyId regionPropertyId = TestRegionPropertyId.REGION_PROPERTY_1_BOOLEAN_MUTABLE;
			Object previousValue = true;
			Object currentValue = false;
			RegionPropertyChangeObservationEvent event = new RegionPropertyChangeObservationEvent(testRegionId, regionPropertyId, previousValue, currentValue);
			assertEquals(testRegionId, event.getRegionId());
		}
	}

	@Test
	@UnitTestMethod(name = "getRegionPropertyId", args = {})
	public void testGetRegionPropertyId() {
		for (TestRegionPropertyId testRegionPropertyId : TestRegionPropertyId.values()) {
			RegionId regionId = TestRegionId.REGION_2;
			Object previousValue = true;
			Object currentValue = false;
			RegionPropertyChangeObservationEvent event = new RegionPropertyChangeObservationEvent(regionId, testRegionPropertyId, previousValue, currentValue);
			assertEquals(testRegionPropertyId, event.getRegionPropertyId());
		}
	}

	@Test
	@UnitTestMethod(name = "getPreviousPropertyValue", args = {})
	public void testGetPreviousPropertyValue() {
		for (int i = 0; i < 10; i++) {
			RegionId regionId = TestRegionId.REGION_2;
			RegionPropertyId regionPropertyId = TestRegionPropertyId.REGION_PROPERTY_5_INTEGER_IMMUTABLE;
			Object previousValue = i;
			Object currentValue = false;
			RegionPropertyChangeObservationEvent event = new RegionPropertyChangeObservationEvent(regionId, regionPropertyId, previousValue, currentValue);
			assertEquals(previousValue, event.getPreviousPropertyValue());
		}
	}

	@Test
	@UnitTestMethod(name = "getCurrentPropertyValue", args = {})
	public void testGetCurrentPropertyValue() {
		for (int i = 0; i < 10; i++) {
			RegionId regionId = TestRegionId.REGION_2;
			RegionPropertyId regionPropertyId = TestRegionPropertyId.REGION_PROPERTY_5_INTEGER_IMMUTABLE;
			Object previousValue = true;
			Object currentValue = i;
			RegionPropertyChangeObservationEvent event = new RegionPropertyChangeObservationEvent(regionId, regionPropertyId, previousValue, currentValue);
			assertEquals(currentValue, event.getCurrentPropertyValue());
		}
	}

	@Test
	@UnitTestMethod(name = "toString", args = {})
	public void testToString() {
		RegionId regionId = TestRegionId.REGION_2;
		RegionPropertyId regionPropertyId = TestRegionPropertyId.REGION_PROPERTY_5_INTEGER_IMMUTABLE;
		Object previousValue = 45;
		Object currentValue = 88;
		RegionPropertyChangeObservationEvent event = new RegionPropertyChangeObservationEvent(regionId, regionPropertyId, previousValue, currentValue);
		String actualValue = event.toString();
		String expectedValue =	"RegionPropertyChangeObservationEvent [regionId=REGION_2, regionPropertyId=REGION_PROPERTY_5_INTEGER_IMMUTABLE, previousPropertyValue=45, currentPropertyValue=88]";
		assertEquals(expectedValue, actualValue);
	}

	@Test
	@UnitTestMethod(name = "getEventLabelByProperty", args = { SimulationContext.class, RegionPropertyId.class })
	public void testGetEventLabelByProperty() {
		RegionsActionSupport.testConsumer(0, 7066615060417862369L, TimeTrackingPolicy.DO_NOT_TRACK_TIME, (c) -> {
			for (TestRegionPropertyId testRegionPropertyId : TestRegionPropertyId.values()) {
				EventLabel<RegionPropertyChangeObservationEvent> eventLabel = RegionPropertyChangeObservationEvent.getEventLabelByProperty(c, testRegionPropertyId);
				assertEquals(RegionPropertyChangeObservationEvent.class, eventLabel.getEventClass());
				assertEquals(testRegionPropertyId, eventLabel.getPrimaryKeyValue());
				assertEquals(RegionPropertyChangeObservationEvent.getEventLabelerForProperty().getId(), eventLabel.getLabelerId());
			}
		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelByRegionAndProperty", args = { SimulationContext.class, RegionId.class, RegionPropertyId.class })
	public void testGetEventLabelByRegionAndProperty() {
		RegionsActionSupport.testConsumer(0, 7397296219745259412L, TimeTrackingPolicy.DO_NOT_TRACK_TIME, (c) -> {
			for (TestRegionPropertyId testRegionPropertyId : TestRegionPropertyId.values()) {
				for (TestRegionId testRegionId : TestRegionId.values()) {
					EventLabel<RegionPropertyChangeObservationEvent> eventLabel = RegionPropertyChangeObservationEvent.getEventLabelByRegionAndProperty(c, testRegionId, testRegionPropertyId);
					assertEquals(RegionPropertyChangeObservationEvent.class, eventLabel.getEventClass());
					assertEquals(testRegionPropertyId, eventLabel.getPrimaryKeyValue());
					assertEquals(RegionPropertyChangeObservationEvent.getEventLabelerForRegionAndProperty().getId(), eventLabel.getLabelerId());
				}
			}
		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForRegionAndProperty", args = {})
	public void testGetEventLabelerForRegionAndProperty() {
		RegionsActionSupport.testConsumer(0, 8940599178011626507L, TimeTrackingPolicy.DO_NOT_TRACK_TIME, (c) -> {
			// show that the event labeler can be constructed has the correct
			// values
			EventLabeler<RegionPropertyChangeObservationEvent> eventLabeler = RegionPropertyChangeObservationEvent.getEventLabelerForRegionAndProperty();
			assertEquals(RegionPropertyChangeObservationEvent.class, eventLabeler.getEventClass());

			for (TestRegionPropertyId testRegionPropertyId : TestRegionPropertyId.values()) {
				for (TestRegionId testRegionId : TestRegionId.values()) {
					assertEquals(RegionPropertyChangeObservationEvent.getEventLabelByRegionAndProperty(c, testRegionId, testRegionPropertyId).getLabelerId(), eventLabeler.getId());

					// show that the event labeler produces the expected event
					// label

					// create an event
					RegionPropertyChangeObservationEvent event = new RegionPropertyChangeObservationEvent(testRegionId, testRegionPropertyId, 15, 20);

					// derive the expected event label for this event
					EventLabel<RegionPropertyChangeObservationEvent> expectedEventLabel = RegionPropertyChangeObservationEvent.getEventLabelByRegionAndProperty(c, testRegionId, testRegionPropertyId);

					// have the event labeler produce an event label and show it
					// is equal to the expected event label
					EventLabel<RegionPropertyChangeObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);
					assertEquals(expectedEventLabel, actualEventLabel);

				}
			}
		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForProperty", args = {})
	public void testGetEventLabelerForProperty() {
		RegionsActionSupport.testConsumer(0, 6696076014058054790L, TimeTrackingPolicy.DO_NOT_TRACK_TIME, (c) -> {
			// show that the event labeler can be constructed has the correct
			// values
			EventLabeler<RegionPropertyChangeObservationEvent> eventLabeler = RegionPropertyChangeObservationEvent.getEventLabelerForProperty();
			assertEquals(RegionPropertyChangeObservationEvent.class, eventLabeler.getEventClass());

			for (TestRegionPropertyId testRegionPropertyId : TestRegionPropertyId.values()) {
				assertEquals(RegionPropertyChangeObservationEvent.getEventLabelByProperty(c, testRegionPropertyId).getLabelerId(), eventLabeler.getId());

				// show that the event labeler produces the expected event
				// label

				// create an event
				RegionPropertyChangeObservationEvent event = new RegionPropertyChangeObservationEvent(TestRegionId.REGION_1, testRegionPropertyId, 15, 20);

				// derive the expected event label for this event
				EventLabel<RegionPropertyChangeObservationEvent> expectedEventLabel = RegionPropertyChangeObservationEvent.getEventLabelByProperty(c, testRegionPropertyId);

				// have the event labeler produce an event label and show it
				// is equal to the expected event label
				EventLabel<RegionPropertyChangeObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);
				assertEquals(expectedEventLabel, actualEventLabel);

			}
		});
	}

	@Test
	@UnitTestMethod(name = "getPrimaryKeyValue", args = {})
	public void testGetPrimaryKeyValue() {
		for (TestRegionPropertyId testRegionPropertyId : TestRegionPropertyId.values()) {
			RegionPropertyChangeObservationEvent regionPropertyChangeObservationEvent = new RegionPropertyChangeObservationEvent(TestRegionId.REGION_2, testRegionPropertyId, 10, 15);
			assertEquals(testRegionPropertyId, regionPropertyChangeObservationEvent.getPrimaryKeyValue());
		}
	}
}
