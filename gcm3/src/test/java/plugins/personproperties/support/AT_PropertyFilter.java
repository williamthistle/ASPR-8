package plugins.personproperties.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import nucleus.AgentContext;
import nucleus.Context;
import nucleus.Simulation;
import nucleus.Simulation.Builder;
import nucleus.NucleusError;
import nucleus.testsupport.actionplugin.ActionPlugin;
import nucleus.testsupport.actionplugin.AgentActionPlan;
import plugins.compartments.CompartmentPlugin;
import plugins.compartments.initialdata.CompartmentInitialData;
import plugins.compartments.testsupport.TestCompartmentId;
import plugins.components.ComponentPlugin;
import plugins.partitions.PartitionsPlugin;
import plugins.partitions.support.Equality;
import plugins.partitions.support.Filter;
import plugins.partitions.support.FilterSensitivity;
import plugins.partitions.support.PartitionError;
import plugins.people.PeoplePlugin;
import plugins.people.datacontainers.PersonDataView;
import plugins.people.initialdata.PeopleInitialData;
import plugins.people.support.PersonError;
import plugins.people.support.PersonId;
import plugins.personproperties.PersonPropertiesPlugin;
import plugins.personproperties.datacontainers.PersonPropertyDataView;
import plugins.personproperties.events.mutation.PersonPropertyValueAssignmentEvent;
import plugins.personproperties.events.observation.PersonPropertyChangeObservationEvent;
import plugins.personproperties.initialdata.PersonPropertyInitialData;
import plugins.personproperties.testsupport.TestPersonPropertyId;
import plugins.properties.PropertiesPlugin;
import plugins.properties.support.PropertyError;
import plugins.regions.RegionPlugin;
import plugins.regions.initialdata.RegionInitialData;
import plugins.regions.testsupport.TestRegionId;
import plugins.reports.ReportPlugin;
import plugins.reports.initialdata.ReportsInitialData;
import plugins.stochastics.StochasticsPlugin;
import plugins.stochastics.datacontainers.StochasticsDataView;
import plugins.stochastics.initialdata.StochasticsInitialData;
import util.ContractException;
import util.annotations.UnitTest;
import util.annotations.UnitTestConstructor;
import util.annotations.UnitTestMethod;

/**
 * Test unit for {@link PropertyFilter}.
 *
 * @author Shawn Hatch
 *
 */
@UnitTest(target = PropertyFilter.class)
public class AT_PropertyFilter {

	private void testConsumer(int initialPopulation, long seed, Consumer<AgentContext> consumer) {
		ActionPlugin.Builder pluginBuilder = ActionPlugin.builder();
		pluginBuilder.addAgent("agent");
		pluginBuilder.addAgentActionPlan("agent", new AgentActionPlan(0, consumer));
		testConsumers(initialPopulation, seed, pluginBuilder.build());
	}

	private void testConsumers(int initialPopulation, long seed, ActionPlugin actionPlugin) {

		Builder builder = Simulation.builder();

		// add the person property plugin
		PersonPropertyInitialData.Builder personPropertyBuilder = PersonPropertyInitialData.builder();
		for (TestPersonPropertyId testPersonPropertyId : TestPersonPropertyId.values()) {
			personPropertyBuilder.definePersonProperty(testPersonPropertyId, testPersonPropertyId.getPropertyDefinition());
		}

		builder.addPlugin(PersonPropertiesPlugin.PLUGIN_ID, new PersonPropertiesPlugin(personPropertyBuilder.build())::init);

		// add the people plugin
		builder.addPlugin(PartitionsPlugin.PLUGIN_ID, new PartitionsPlugin()::init);
		PeopleInitialData.Builder peopleBuilder = PeopleInitialData.builder();
		List<PersonId> people = new ArrayList<>();
		for (int i = 0; i < initialPopulation; i++) {
			people.add(new PersonId(i));
		}

		for (PersonId personId : people) {
			peopleBuilder.addPersonId(personId);
		}

		builder.addPlugin(PeoplePlugin.PLUGIN_ID, new PeoplePlugin(peopleBuilder.build())::init);

		// add the properties plugin
		builder.addPlugin(PropertiesPlugin.PLUGIN_ID, new PropertiesPlugin()::init);

		// add the compartments plugin
		CompartmentInitialData.Builder compartmentBuilder = CompartmentInitialData.builder();

		// add the compartments
		for (TestCompartmentId testCompartmentId : TestCompartmentId.values()) {
			compartmentBuilder.setCompartmentInitialBehaviorSupplier(testCompartmentId, () -> (c2) -> {
			});
		}

		// assign people to compartments
		TestCompartmentId testCompartmentId = TestCompartmentId.COMPARTMENT_1;
		for (PersonId personId : people) {
			compartmentBuilder.setPersonCompartment(personId, testCompartmentId.next());
		}

		builder.addPlugin(CompartmentPlugin.PLUGIN_ID, new CompartmentPlugin(compartmentBuilder.build())::init);

		// add the regions plugin
		RegionInitialData.Builder regionBuilder = RegionInitialData.builder();

		// add the regions
		for (TestRegionId testRegionId : TestRegionId.values()) {
			regionBuilder.setRegionComponentInitialBehaviorSupplier(testRegionId, () -> (c2) -> {
			});
		}

		// assign people to regions
		TestRegionId testRegionId = TestRegionId.REGION_1;
		for (PersonId personId : people) {
			regionBuilder.setPersonRegion(personId, testRegionId.next());
		}

		builder.addPlugin(RegionPlugin.PLUGIN_ID, new RegionPlugin(regionBuilder.build())::init);

		// add the component plugin
		builder.addPlugin(ComponentPlugin.PLUGIN_ID, new ComponentPlugin()::init);

		// add the report plugin
		builder.addPlugin(ReportPlugin.PLUGIN_ID, new ReportPlugin(ReportsInitialData.builder().build())::init);

		// add the stochastics plugin
		builder.addPlugin(StochasticsPlugin.PLUGIN_ID, new StochasticsPlugin(StochasticsInitialData.builder().setSeed(seed).build())::init);

		// add the action plugin
		builder.addPlugin(ActionPlugin.PLUGIN_ID, actionPlugin::init);

		// build and execute the engine
		builder.build().execute();

		// show that all actions were executed
		assertTrue(actionPlugin.allActionsExecuted());

	}

	/**
	 * Tests
	 * {@link PropertyFilter#PropertyFilter(Context, PersonPropertyId, Equality, Object)}
	 */
	@Test
	@UnitTestConstructor(args = { Context.class, PersonPropertyId.class, Equality.class, long.class })
	public void testConstructor() {
		// nothing to test
	}

	/**
	 * Tests
	 * {@link PropertyFilter#PropertyFilter(Context, PersonPropertyId, Equality, Object)}
	 */
	@Test
	@UnitTestMethod(name = "validate", args = {})
	public void testValidate() {
		
		testConsumer(100, 7889475921077680704L, (c)->{
			final Filter filter = new PropertyFilter(TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK, Equality.EQUAL, 12);
			assertNotNull(filter);
			
			

			ContractException contractException = assertThrows(ContractException.class, () -> new PropertyFilter(TestPersonPropertyId.getUnknownPersonPropertyId(), Equality.EQUAL, 12).validate(c));
			assertEquals(PersonPropertyError.UNKNOWN_PERSON_PROPERTY_ID, contractException.getErrorType());

			contractException = assertThrows(ContractException.class, () -> new PropertyFilter(null, Equality.EQUAL, 12L).validate(c));
			assertEquals(PersonPropertyError.NULL_PERSON_PROPERTY_ID, contractException.getErrorType());

			contractException = assertThrows(ContractException.class, () -> new PropertyFilter(TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK, null, 12).validate(c));
			assertEquals(PartitionError.NULL_EQUALITY_OPERATOR, contractException.getErrorType());

			contractException = assertThrows(ContractException.class, () -> new PropertyFilter(TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK, Equality.EQUAL, "bad value").validate(c));
			assertEquals(PropertyError.INCOMPATIBLE_VALUE, contractException.getErrorType());

		});

		
	}

	/**
	 * Tests {@link PropertyFilter#getFilterSensitivities()}
	 */
	@Test
	@UnitTestMethod(name = "getFilterSensitivities", args = {})
	public void testGetFilterSensitivities() {

		Filter filter = new PropertyFilter(TestPersonPropertyId.PERSON_PROPERTY_1_BOOLEAN_MUTABLE_NO_TRACK, Equality.EQUAL, 12);

		Set<FilterSensitivity<?>> filterSensitivities = filter.getFilterSensitivities();
		assertNotNull(filterSensitivities);
		assertEquals(filterSensitivities.size(), 1);

		FilterSensitivity<?> filterSensitivity = filterSensitivities.iterator().next();
		assertEquals(PersonPropertyChangeObservationEvent.class, filterSensitivity.getEventClass());
	}

	/**
	 * Tests {@link PropertyFilter#evaluate(Context, PersonId)}
	 */
	@Test
	@UnitTestMethod(name = "evaluate", args = { Context.class, PersonId.class })
	public void testEvaluate() {
		
		testConsumer(100, 9037413907425227057L, (c)->{
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			PersonPropertyDataView personPropertyDataView = c.getDataView(PersonPropertyDataView.class).get();
			StochasticsDataView stochasticsDataView = c.getDataView(StochasticsDataView.class).get();
			RandomGenerator randomGenerator = stochasticsDataView.getRandomGenerator();

			TestPersonPropertyId testPersonPropertyId = TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK;
			
			Filter filter = new PropertyFilter(testPersonPropertyId, Equality.GREATER_THAN, 12);

			for (PersonId personId : personDataView.getPeople()) {
				int value = randomGenerator.nextInt(10) + 7;
				c.resolveEvent(new PersonPropertyValueAssignmentEvent(personId, testPersonPropertyId, value));;
			}

			for (PersonId personId : personDataView.getPeople()) {
				Integer value = personPropertyDataView.getPersonPropertyValue(personId, testPersonPropertyId);
				boolean expected = value > 12;
				boolean actual = filter.evaluate(c, personId);
				assertEquals(expected, actual);
			}

			/* precondition: if the context is null */
			ContractException contractException = assertThrows(ContractException.class, () -> filter.evaluate(null, new PersonId(0)));
			assertEquals(NucleusError.NULL_CONTEXT,contractException.getErrorType());

			/* precondition: if the person id is null */
			contractException = assertThrows(ContractException.class, () -> filter.evaluate(c, null));
			assertEquals(PersonError.NULL_PERSON_ID,contractException.getErrorType());

			/* precondition: if the person id is unknown */
			contractException = assertThrows(ContractException.class, () -> filter.evaluate(c, new PersonId(123412342)));
			assertEquals(PersonError.UNKNOWN_PERSON_ID,contractException.getErrorType());
		});
	}
}
