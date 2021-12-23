package plugins.resources.events.observation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import nucleus.Context;
import nucleus.Event;
import nucleus.EventLabel;
import nucleus.EventLabeler;
import plugins.compartments.datacontainers.CompartmentDataView;
import plugins.compartments.datacontainers.CompartmentLocationDataView;
import plugins.compartments.support.CompartmentError;
import plugins.compartments.support.CompartmentId;
import plugins.compartments.testsupport.TestCompartmentId;
import plugins.people.datacontainers.PersonDataView;
import plugins.people.support.PersonError;
import plugins.people.support.PersonId;
import plugins.regions.datacontainers.RegionDataView;
import plugins.regions.datacontainers.RegionLocationDataView;
import plugins.regions.support.RegionError;
import plugins.regions.support.RegionId;
import plugins.regions.testsupport.TestRegionId;
import plugins.resources.datacontainers.ResourceDataView;
import plugins.resources.support.ResourceError;
import plugins.resources.support.ResourceId;
import plugins.resources.testsupport.ResourcesActionSupport;
import plugins.resources.testsupport.TestResourceId;
import plugins.stochastics.datacontainers.StochasticsDataView;
import util.ContractException;
import util.annotations.UnitTest;
import util.annotations.UnitTestConstructor;
import util.annotations.UnitTestMethod;

@UnitTest(target = PersonResourceChangeObservationEvent.class)
public class AT_PersonResourceChangeObservationEvent implements Event {

	@Test
	@UnitTestConstructor(args = { PersonId.class, ResourceId.class, long.class, long.class })
	public void testConstructor() {
		// nothing to test
	}

	@Test
	@UnitTestMethod(name = "getResourceId", args = {})
	public void testGetResourceId() {
		PersonId personId = new PersonId(7645);
		ResourceId resourceId = TestResourceId.RESOURCE_2;
		long previousResourceLevel = 45L;
		long currentResourceLevel = 398L;
		PersonResourceChangeObservationEvent personResourceChangeObservationEvent = new PersonResourceChangeObservationEvent(personId, resourceId, previousResourceLevel, currentResourceLevel);
		assertEquals(resourceId, personResourceChangeObservationEvent.getResourceId());
	}

	@Test
	@UnitTestMethod(name = "getCurrentResourceLevel", args = {})
	public void testGetCurrentResourceLevel() {
		PersonId personId = new PersonId(7645);
		ResourceId resourceId = TestResourceId.RESOURCE_2;
		long previousResourceLevel = 45L;
		long currentResourceLevel = 398L;
		PersonResourceChangeObservationEvent personResourceChangeObservationEvent = new PersonResourceChangeObservationEvent(personId, resourceId, previousResourceLevel, currentResourceLevel);
		assertEquals(currentResourceLevel, personResourceChangeObservationEvent.getCurrentResourceLevel());
	}

	@Test
	@UnitTestMethod(name = "getPersonId", args = {})
	public void testGetPersonId() {
		PersonId personId = new PersonId(7645);
		ResourceId resourceId = TestResourceId.RESOURCE_2;
		long previousResourceLevel = 45L;
		long currentResourceLevel = 398L;
		PersonResourceChangeObservationEvent personResourceChangeObservationEvent = new PersonResourceChangeObservationEvent(personId, resourceId, previousResourceLevel, currentResourceLevel);
		assertEquals(personId, personResourceChangeObservationEvent.getPersonId());
	}

	@Test
	@UnitTestMethod(name = "getPreviousResourceLevel", args = {})
	public void testGetPreviousResourceLevel() {
		PersonId personId = new PersonId(7645);
		ResourceId resourceId = TestResourceId.RESOURCE_2;
		long previousResourceLevel = 45L;
		long currentResourceLevel = 398L;
		PersonResourceChangeObservationEvent personResourceChangeObservationEvent = new PersonResourceChangeObservationEvent(personId, resourceId, previousResourceLevel, currentResourceLevel);
		assertEquals(previousResourceLevel, personResourceChangeObservationEvent.getPreviousResourceLevel());
	}

	@Test
	@UnitTestMethod(name = "getEventLabelByCompartmentAndResource", args = { Context.class, CompartmentId.class, ResourceId.class })
	public void testGetEventLabelByCompartmentAndResource() {
		ResourcesActionSupport.testConsumer(10, 7912737444879496875L, (c) -> {

			CompartmentLocationDataView compartmentLocationDataView = c.getDataView(CompartmentLocationDataView.class).get();
			CompartmentDataView compartmentDataView = c.getDataView(CompartmentDataView.class).get();
			ResourceDataView resourceDataView = c.getDataView(ResourceDataView.class).get();
			Set<CompartmentId> compartmentIds = compartmentDataView.getCompartmentIds();
			Set<ResourceId> resourceIds = resourceDataView.getResourceIds();

			Set<EventLabel<PersonResourceChangeObservationEvent>> eventLabels = new LinkedHashSet<>();

			for (CompartmentId compartmentId : compartmentIds) {
				for (ResourceId resourceId : resourceIds) {

					EventLabel<PersonResourceChangeObservationEvent> eventLabel = PersonResourceChangeObservationEvent.getEventLabelByCompartmentAndResource(c, compartmentId, resourceId);

					// show that the event label has the correct event class
					assertEquals(PersonResourceChangeObservationEvent.class, eventLabel.getEventClass());

					// show that the event label has the correct primary key
					assertEquals(resourceId, eventLabel.getPrimaryKeyValue());

					// show that the event label has the same id as its
					// associated labeler
					EventLabeler<PersonResourceChangeObservationEvent> eventLabeler = PersonResourceChangeObservationEvent.getEventLabelerForCompartmentAndResource(compartmentLocationDataView);
					assertEquals(eventLabeler.getId(), eventLabel.getLabelerId());

					// show that two event labels with the same inputs are equal
					EventLabel<PersonResourceChangeObservationEvent> eventLabel2 = PersonResourceChangeObservationEvent.getEventLabelByCompartmentAndResource(c, compartmentId, resourceId);
					assertEquals(eventLabel, eventLabel2);

					// show that equal event labels have equal hash codes
					assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

					// show that two event labels with different inputs are not
					// equal
					assertTrue(eventLabels.add(eventLabel));
				}
			}

			// precondition tests

			// if the compartment id is null
			ContractException contractException = assertThrows(ContractException.class,
					() -> PersonResourceChangeObservationEvent.getEventLabelByCompartmentAndResource(c, null, TestResourceId.RESOURCE_1));
			assertEquals(CompartmentError.NULL_COMPARTMENT_ID, contractException.getErrorType());

			// if the compartment id is unknown
			contractException = assertThrows(ContractException.class,
					() -> PersonResourceChangeObservationEvent.getEventLabelByCompartmentAndResource(c, TestCompartmentId.getUnknownCompartmentId(), TestResourceId.RESOURCE_1));
			assertEquals(CompartmentError.UNKNOWN_COMPARTMENT_ID, contractException.getErrorType());

			// if the resource id is null
			contractException = assertThrows(ContractException.class, () -> PersonResourceChangeObservationEvent.getEventLabelByCompartmentAndResource(c, TestCompartmentId.COMPARTMENT_1, null));
			assertEquals(ResourceError.NULL_RESOURCE_ID, contractException.getErrorType());

			// if the resource id is unknown
			contractException = assertThrows(ContractException.class,
					() -> PersonResourceChangeObservationEvent.getEventLabelByCompartmentAndResource(c, TestCompartmentId.COMPARTMENT_1, TestResourceId.getUnknownResourceId()));
			assertEquals(ResourceError.UNKNOWN_RESOURCE_ID, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForCompartmentAndResource", args = {})
	public void testGetEventLabelerForCompartmentAndResource() {
		ResourcesActionSupport.testConsumer(30, 5829392632134617932L, (c) -> {
			CompartmentLocationDataView compartmentLocationDataView = c.getDataView(CompartmentLocationDataView.class).get();
			CompartmentDataView compartmentDataView = c.getDataView(CompartmentDataView.class).get();
			ResourceDataView resourceDataView = c.getDataView(ResourceDataView.class).get();
			Set<CompartmentId> compartmentIds = compartmentDataView.getCompartmentIds();
			Set<ResourceId> resourceIds = resourceDataView.getResourceIds();
			StochasticsDataView stochasticsDataView = c.getDataView(StochasticsDataView.class).get();
			RandomGenerator randomGenerator = stochasticsDataView.getRandomGenerator();

			// create an event labeler
			EventLabeler<PersonResourceChangeObservationEvent> eventLabeler = PersonResourceChangeObservationEvent.getEventLabelerForCompartmentAndResource(compartmentLocationDataView);

			// show that the event labeler has the correct event class
			assertEquals(PersonResourceChangeObservationEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (CompartmentId compartmentId : compartmentIds) {
				List<PersonId> peopleInCompartment = compartmentLocationDataView.getPeopleInCompartment(compartmentId);
				if (peopleInCompartment.size() > 0) {
					for (ResourceId resourceId : resourceIds) {
						PersonId personId = peopleInCompartment.get(randomGenerator.nextInt(peopleInCompartment.size()));

						// derive the expected event label for this event
						EventLabel<PersonResourceChangeObservationEvent> expectedEventLabel = PersonResourceChangeObservationEvent.getEventLabelByCompartmentAndResource(c, compartmentId, resourceId);

						// show that the event label and event labeler have
						// equal id
						// values
						assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getId());

						// create an event
						PersonResourceChangeObservationEvent event = new PersonResourceChangeObservationEvent(personId, resourceId, 10L, 30L);

						// show that the event labeler produces the correct
						// event
						// label
						EventLabel<PersonResourceChangeObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

						assertEquals(expectedEventLabel, actualEventLabel);

					}
				}
			}

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelByRegionAndResource", args = { Context.class, RegionId.class, ResourceId.class })
	public void testGetEventLabelByRegionAndResource() {
		ResourcesActionSupport.testConsumer(10, 7912737444879496875L, (c) -> {

			RegionLocationDataView regionLocationDataView = c.getDataView(RegionLocationDataView.class).get();
			RegionDataView regionDataView = c.getDataView(RegionDataView.class).get();
			ResourceDataView resourceDataView = c.getDataView(ResourceDataView.class).get();
			Set<RegionId> regionIds = regionDataView.getRegionIds();
			Set<ResourceId> resourceIds = resourceDataView.getResourceIds();

			Set<EventLabel<PersonResourceChangeObservationEvent>> eventLabels = new LinkedHashSet<>();

			for (RegionId regionId : regionIds) {
				for (ResourceId resourceId : resourceIds) {

					EventLabel<PersonResourceChangeObservationEvent> eventLabel = PersonResourceChangeObservationEvent.getEventLabelByRegionAndResource(c, regionId, resourceId);

					// show that the event label has the correct event class
					assertEquals(PersonResourceChangeObservationEvent.class, eventLabel.getEventClass());

					// show that the event label has the correct primary key
					assertEquals(resourceId, eventLabel.getPrimaryKeyValue());

					// show that the event label has the same id as its
					// associated labeler
					EventLabeler<PersonResourceChangeObservationEvent> eventLabeler = PersonResourceChangeObservationEvent.getEventLabelerForRegionAndResource(regionLocationDataView);
					assertEquals(eventLabeler.getId(), eventLabel.getLabelerId());

					// show that two event labels with the same inputs are equal
					EventLabel<PersonResourceChangeObservationEvent> eventLabel2 = PersonResourceChangeObservationEvent.getEventLabelByRegionAndResource(c, regionId, resourceId);
					assertEquals(eventLabel, eventLabel2);

					// show that equal event labels have equal hash codes
					assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

					// show that two event labels with different inputs are not
					// equal
					assertTrue(eventLabels.add(eventLabel));
				}
			}

			// precondition tests

			// if the region id is null
			ContractException contractException = assertThrows(ContractException.class,
					() -> PersonResourceChangeObservationEvent.getEventLabelByRegionAndResource(c, null, TestResourceId.RESOURCE_1));
			assertEquals(RegionError.NULL_REGION_ID, contractException.getErrorType());

			// if the region id is unknown
			contractException = assertThrows(ContractException.class,
					() -> PersonResourceChangeObservationEvent.getEventLabelByRegionAndResource(c, TestRegionId.getUnknownRegionId(), TestResourceId.RESOURCE_1));
			assertEquals(RegionError.UNKNOWN_REGION_ID, contractException.getErrorType());

			// if the resource id is null
			contractException = assertThrows(ContractException.class, () -> PersonResourceChangeObservationEvent.getEventLabelByRegionAndResource(c, TestRegionId.REGION_1, null));
			assertEquals(ResourceError.NULL_RESOURCE_ID, contractException.getErrorType());

			// if the resource id is unknown
			contractException = assertThrows(ContractException.class,
					() -> PersonResourceChangeObservationEvent.getEventLabelByRegionAndResource(c, TestRegionId.REGION_1, TestResourceId.getUnknownResourceId()));
			assertEquals(ResourceError.UNKNOWN_RESOURCE_ID, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForRegionAndResource", args = {})
	public void testGetEventLabelerForRegionAndResource() {
		ResourcesActionSupport.testConsumer(30, 5829392632134617932L, (c) -> {
			RegionLocationDataView regionLocationDataView = c.getDataView(RegionLocationDataView.class).get();
			RegionDataView regionDataView = c.getDataView(RegionDataView.class).get();
			ResourceDataView resourceDataView = c.getDataView(ResourceDataView.class).get();
			Set<RegionId> regionIds = regionDataView.getRegionIds();
			Set<ResourceId> resourceIds = resourceDataView.getResourceIds();
			StochasticsDataView stochasticsDataView = c.getDataView(StochasticsDataView.class).get();
			RandomGenerator randomGenerator = stochasticsDataView.getRandomGenerator();

			// create an event labeler
			EventLabeler<PersonResourceChangeObservationEvent> eventLabeler = PersonResourceChangeObservationEvent.getEventLabelerForRegionAndResource(regionLocationDataView);

			// show that the event labeler has the correct event class
			assertEquals(PersonResourceChangeObservationEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (RegionId regionId : regionIds) {
				List<PersonId> peopleInRegion = regionLocationDataView.getPeopleInRegion(regionId);
				if (peopleInRegion.size() > 0) {
					for (ResourceId resourceId : resourceIds) {
						PersonId personId = peopleInRegion.get(randomGenerator.nextInt(peopleInRegion.size()));

						// derive the expected event label for this event
						EventLabel<PersonResourceChangeObservationEvent> expectedEventLabel = PersonResourceChangeObservationEvent.getEventLabelByRegionAndResource(c, regionId, resourceId);

						// show that the event label and event labeler have
						// equal id
						// values
						assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getId());

						// create an event
						PersonResourceChangeObservationEvent event = new PersonResourceChangeObservationEvent(personId, resourceId, 10L, 30L);

						// show that the event labeler produces the correct
						// event
						// label
						EventLabel<PersonResourceChangeObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

						assertEquals(expectedEventLabel, actualEventLabel);

					}
				}
			}

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelByPersonAndResource", args = { Context.class, PersonId.class, ResourceId.class })
	public void testGetEventLabelByPersonAndResource() {
		ResourcesActionSupport.testConsumer(10, 7912737444879496875L, (c) -> {

			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			ResourceDataView resourceDataView = c.getDataView(ResourceDataView.class).get();
			List<PersonId> people = personDataView.getPeople();
			Set<ResourceId> resourceIds = resourceDataView.getResourceIds();

			Set<EventLabel<PersonResourceChangeObservationEvent>> eventLabels = new LinkedHashSet<>();

			for (PersonId personId : people) {
				for (ResourceId resourceId : resourceIds) {

					EventLabel<PersonResourceChangeObservationEvent> eventLabel = PersonResourceChangeObservationEvent.getEventLabelByPersonAndResource(c, personId, resourceId);

					// show that the event label has the correct event class
					assertEquals(PersonResourceChangeObservationEvent.class, eventLabel.getEventClass());

					// show that the event label has the correct primary key
					assertEquals(resourceId, eventLabel.getPrimaryKeyValue());

					// show that the event label has the same id as its
					// associated labeler
					EventLabeler<PersonResourceChangeObservationEvent> eventLabeler = PersonResourceChangeObservationEvent.getEventLabelerForPersonAndResource();
					assertEquals(eventLabeler.getId(), eventLabel.getLabelerId());

					// show that two event labels with the same inputs are equal
					EventLabel<PersonResourceChangeObservationEvent> eventLabel2 = PersonResourceChangeObservationEvent.getEventLabelByPersonAndResource(c, personId, resourceId);
					assertEquals(eventLabel, eventLabel2);

					// show that equal event labels have equal hash codes
					assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

					// show that two event labels with different inputs are not
					// equal
					assertTrue(eventLabels.add(eventLabel));
				}
			}

			// precondition tests

			// if the person id is null
			ContractException contractException = assertThrows(ContractException.class,
					() -> PersonResourceChangeObservationEvent.getEventLabelByPersonAndResource(c, null, TestResourceId.RESOURCE_1));
			assertEquals(PersonError.NULL_PERSON_ID, contractException.getErrorType());

			// if the person id is unknown
			contractException = assertThrows(ContractException.class, () -> PersonResourceChangeObservationEvent.getEventLabelByPersonAndResource(c, new PersonId(11110), TestResourceId.RESOURCE_1));
			assertEquals(PersonError.UNKNOWN_PERSON_ID, contractException.getErrorType());

			// if the resource id is null
			contractException = assertThrows(ContractException.class, () -> PersonResourceChangeObservationEvent.getEventLabelByPersonAndResource(c, new PersonId(0), null));
			assertEquals(ResourceError.NULL_RESOURCE_ID, contractException.getErrorType());

			// if the resource id is unknown
			contractException = assertThrows(ContractException.class,
					() -> PersonResourceChangeObservationEvent.getEventLabelByPersonAndResource(c, new PersonId(0), TestResourceId.getUnknownResourceId()));
			assertEquals(ResourceError.UNKNOWN_RESOURCE_ID, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForPersonAndResource", args = {})
	public void testGetEventLabelerForPersonAndResource() {
		ResourcesActionSupport.testConsumer(30, 5829392632134617932L, (c) -> {
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			ResourceDataView resourceDataView = c.getDataView(ResourceDataView.class).get();
			List<PersonId> people = personDataView.getPeople();
			Set<ResourceId> resourceIds = resourceDataView.getResourceIds();

			// create an event labeler
			EventLabeler<PersonResourceChangeObservationEvent> eventLabeler = PersonResourceChangeObservationEvent.getEventLabelerForPersonAndResource();

			// show that the event labeler has the correct event class
			assertEquals(PersonResourceChangeObservationEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (PersonId personId : people) {

				for (ResourceId resourceId : resourceIds) {

					// derive the expected event label for this event
					EventLabel<PersonResourceChangeObservationEvent> expectedEventLabel = PersonResourceChangeObservationEvent.getEventLabelByPersonAndResource(c, personId, resourceId);

					// show that the event label and event labeler have
					// equal id
					// values
					assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getId());

					// create an event
					PersonResourceChangeObservationEvent event = new PersonResourceChangeObservationEvent(personId, resourceId, 10L, 30L);

					// show that the event labeler produces the correct
					// event
					// label
					EventLabel<PersonResourceChangeObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

					assertEquals(expectedEventLabel, actualEventLabel);

				}

			}

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelByResource", args = { Context.class, ResourceId.class })
	public void testGetEventLabelByResource() {
		ResourcesActionSupport.testConsumer(10, 7912737444879496875L, (c) -> {

			ResourceDataView resourceDataView = c.getDataView(ResourceDataView.class).get();
			Set<ResourceId> resourceIds = resourceDataView.getResourceIds();

			Set<EventLabel<PersonResourceChangeObservationEvent>> eventLabels = new LinkedHashSet<>();

			for (ResourceId resourceId : resourceIds) {

				EventLabel<PersonResourceChangeObservationEvent> eventLabel = PersonResourceChangeObservationEvent.getEventLabelByResource(c, resourceId);

				// show that the event label has the correct event class
				assertEquals(PersonResourceChangeObservationEvent.class, eventLabel.getEventClass());

				// show that the event label has the correct primary key
				assertEquals(resourceId, eventLabel.getPrimaryKeyValue());

				// show that the event label has the same id as its
				// associated labeler
				EventLabeler<PersonResourceChangeObservationEvent> eventLabeler = PersonResourceChangeObservationEvent.getEventLabelerForResource();
				assertEquals(eventLabeler.getId(), eventLabel.getLabelerId());

				// show that two event labels with the same inputs are equal
				EventLabel<PersonResourceChangeObservationEvent> eventLabel2 = PersonResourceChangeObservationEvent.getEventLabelByResource(c, resourceId);
				assertEquals(eventLabel, eventLabel2);

				// show that equal event labels have equal hash codes
				assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

				// show that two event labels with different inputs are not
				// equal
				assertTrue(eventLabels.add(eventLabel));
			}

			// precondition tests

			// if the resource id is null
			ContractException contractException = assertThrows(ContractException.class, () -> PersonResourceChangeObservationEvent.getEventLabelByResource(c, null));
			assertEquals(ResourceError.NULL_RESOURCE_ID, contractException.getErrorType());

			// if the resource id is unknown
			contractException = assertThrows(ContractException.class, () -> PersonResourceChangeObservationEvent.getEventLabelByResource(c, TestResourceId.getUnknownResourceId()));
			assertEquals(ResourceError.UNKNOWN_RESOURCE_ID, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForResource", args = {})
	public void testGetEventLabelerForResource() {
		ResourcesActionSupport.testConsumer(30, 5829392632134617932L, (c) -> {
			ResourceDataView resourceDataView = c.getDataView(ResourceDataView.class).get();
			Set<ResourceId> resourceIds = resourceDataView.getResourceIds();

			// create an event labeler
			EventLabeler<PersonResourceChangeObservationEvent> eventLabeler = PersonResourceChangeObservationEvent.getEventLabelerForResource();

			// show that the event labeler has the correct event class
			assertEquals(PersonResourceChangeObservationEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (ResourceId resourceId : resourceIds) {

				// derive the expected event label for this event
				EventLabel<PersonResourceChangeObservationEvent> expectedEventLabel = PersonResourceChangeObservationEvent.getEventLabelByResource(c, resourceId);

				// show that the event label and event labeler have
				// equal id
				// values
				assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getId());

				// create an event
				PersonResourceChangeObservationEvent event = new PersonResourceChangeObservationEvent(new PersonId(0), resourceId, 10L, 30L);

				// show that the event labeler produces the correct
				// event
				// label
				EventLabel<PersonResourceChangeObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

				assertEquals(expectedEventLabel, actualEventLabel);

			}

		});
	}

	@Test
	@UnitTestMethod(name = "getPrimaryKeyValue", args = {})
	public void testGetPrimaryKeyValue() {

		for (TestResourceId testResourceId : TestResourceId.values()) {
			PersonId personId = new PersonId(7645);
			long previousResourceLevel = 45L;
			long currentResourceLevel = 398L;
			PersonResourceChangeObservationEvent personResourceChangeObservationEvent = new PersonResourceChangeObservationEvent(personId, testResourceId, previousResourceLevel, currentResourceLevel);
			assertEquals(testResourceId, personResourceChangeObservationEvent.getPrimaryKeyValue());
		}
	}

}
