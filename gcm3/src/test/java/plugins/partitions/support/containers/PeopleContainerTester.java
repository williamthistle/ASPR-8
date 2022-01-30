package plugins.partitions.support.containers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

import nucleus.AgentContext;
import nucleus.Context;
import nucleus.Simulation;
import nucleus.Simulation.Builder;
import nucleus.testsupport.actionplugin.ActionPlugin;
import nucleus.testsupport.actionplugin.AgentActionPlan;
import plugins.components.ComponentPlugin;
import plugins.partitions.PartitionsPlugin;
import plugins.partitions.testsupport.attributes.AttributesPlugin;
import plugins.partitions.testsupport.attributes.initialdata.AttributeInitialData;
import plugins.partitions.testsupport.attributes.support.TestAttributeId;
import plugins.people.PeoplePlugin;
import plugins.people.datacontainers.PersonDataView;
import plugins.people.initialdata.PeopleInitialData;
import plugins.people.support.PersonId;
import plugins.reports.ReportPlugin;
import plugins.reports.initialdata.ReportsInitialData;
import plugins.stochastics.StochasticsPlugin;
import plugins.stochastics.datacontainers.StochasticsDataView;
import plugins.stochastics.initialdata.StochasticsInitialData;

/*
 * Static support class for testing PopulationContainer implementer classes
 */

public class PeopleContainerTester {

	private static void testConsumer(final int initialPopultionSize, long seed, final Consumer<AgentContext> consumer) {
		final Builder builder = Simulation.builder();
		// define some person attributes
		final AttributeInitialData.Builder attributesBuilder = AttributeInitialData.builder();
		for (final TestAttributeId testAttributeId : TestAttributeId.values()) {
			attributesBuilder.defineAttribute(testAttributeId, testAttributeId.getAttributeDefinition());
		}
		builder.addPlugin(AttributesPlugin.PLUGIN_ID, new AttributesPlugin(attributesBuilder.build())::init);

		final PeopleInitialData.Builder peopleBuilder = PeopleInitialData.builder();
		for (int i = 0; i < initialPopultionSize; i++) {
			peopleBuilder.addPersonId(new PersonId(i));
		}
		builder.addPlugin(PeoplePlugin.PLUGIN_ID, new PeoplePlugin(peopleBuilder.build())::init);
		builder.addPlugin(ReportPlugin.PLUGIN_ID, new ReportPlugin(ReportsInitialData.builder().build())::init);
		builder.addPlugin(StochasticsPlugin.PLUGIN_ID, new StochasticsPlugin(StochasticsInitialData.builder().setSeed(seed).build())::init);
		builder.addPlugin(ComponentPlugin.PLUGIN_ID, new ComponentPlugin()::init);
		builder.addPlugin(PartitionsPlugin.PLUGIN_ID, new PartitionsPlugin()::init);

		/*
		 * Add an agent that executes the consumer.
		 *
		 * Add a second agent to show that the initial population exists and the
		 * attribute ids exist.
		 *
		 */
		final ActionPlugin.Builder pluginBuilder = ActionPlugin.builder();

		/*
		 * Add an agent to show that the partition data view exists
		 */
		pluginBuilder.addAgent("agent");
		pluginBuilder.addAgentActionPlan("agent", new AgentActionPlan(0, (c) -> {
			consumer.accept(c);
		}));

		// build and add the action plugin to the engine
		final ActionPlugin actionPlugin = pluginBuilder.build();
		builder.addPlugin(ActionPlugin.PLUGIN_ID, actionPlugin::init);

		// build and execute the engine
		builder.build().execute();

		// show that all actions were executed
		assertTrue(actionPlugin.allActionsExecuted());

	}

	public static void testGetPeople(Function<Context, PeopleContainer> provider, long seed) {
		testConsumer(100, seed, (c) -> {

			// get the people container to test
			PeopleContainer peopleContainer = provider.apply(c);

			// get some data views that will be needed below
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			StochasticsDataView stochasticsDataView = c.getDataView(StochasticsDataView.class).get();
			RandomGenerator randomGenerator = stochasticsDataView.getRandomGenerator();

			// show that the simulation contains the correct number of people
			assertEquals(100, personDataView.getPopulationCount());

			// add about half of the people to the people we expect to find in
			// the people container
			List<PersonId> expectedPeople = new ArrayList<>();
			for (PersonId personId : personDataView.getPeople()) {
				if (randomGenerator.nextBoolean()) {
					expectedPeople.add(personId);
				}
			}

			// add the people to the people container
			for (PersonId personId : expectedPeople) {
				peopleContainer.safeAdd(personId);
			}

			// show that the people we added are present in the people container
			List<PersonId> peopleList = peopleContainer.getPeople();
			assertEquals(expectedPeople.size(), peopleList.size());
			assertEquals(new LinkedHashSet<>(expectedPeople), new LinkedHashSet<>(peopleList));

			// remove up to 10 people in a random order
			Random random = new Random(randomGenerator.nextLong());
			Collections.shuffle(expectedPeople, random);
			int n = FastMath.min(10, expectedPeople.size());

			for (int i = 0; i < n; i++) {
				PersonId personId = expectedPeople.remove(0);
				peopleContainer.remove(personId);
			}

			// show that the people container still has the correct people
			peopleList = peopleContainer.getPeople();
			assertEquals(expectedPeople.size(), peopleList.size());
			assertEquals(new LinkedHashSet<>(expectedPeople), new LinkedHashSet<>(peopleList));

		});

	}

	public static void testSafeAdd(Function<Context, PeopleContainer> provider, long seed) {
		testConsumer(100, seed, (c) -> {

			// get the people container to test
			PeopleContainer peopleContainer = provider.apply(c);

			// get some data views that will be needed below
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			StochasticsDataView stochasticsDataView = c.getDataView(StochasticsDataView.class).get();
			RandomGenerator randomGenerator = stochasticsDataView.getRandomGenerator();

			// show that the simulation contains the correct number of people
			assertEquals(100, personDataView.getPopulationCount());

			// add about half of the people to the people we expect to find in
			// the people container
			List<PersonId> expectedPeople = new ArrayList<>();
			for (PersonId personId : personDataView.getPeople()) {
				if (randomGenerator.nextBoolean()) {
					expectedPeople.add(personId);
				}
			}

			// add the people to the people container
			for (PersonId personId : expectedPeople) {
				peopleContainer.safeAdd(personId);
				// show that the people container does contain the person
				assertTrue(peopleContainer.contains(personId));

				// add the person again to later show that the addition was safe
				// against duplication
				peopleContainer.safeAdd(personId);
			}

			// show that the people container still has the correct people with
			// no duplications
			List<PersonId> peopleList = peopleContainer.getPeople();
			assertEquals(expectedPeople.size(), peopleList.size());
			assertEquals(new LinkedHashSet<>(expectedPeople), new LinkedHashSet<>(peopleList));

		});
	}

	public static void testUnsafeAdd(Function<Context, PeopleContainer> provider, long seed) {
		testConsumer(100, seed, (c) -> {

			// get the people container to test
			PeopleContainer peopleContainer = provider.apply(c);

			// get some data views that will be needed below
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			StochasticsDataView stochasticsDataView = c.getDataView(StochasticsDataView.class).get();
			RandomGenerator randomGenerator = stochasticsDataView.getRandomGenerator();

			// show that the simulation contains the correct number of people
			assertEquals(100, personDataView.getPopulationCount());

			// add about half of the people to the people we expect to find in
			// the people container
			List<PersonId> expectedPeople = new ArrayList<>();
			for (PersonId personId : personDataView.getPeople()) {
				if (randomGenerator.nextBoolean()) {
					expectedPeople.add(personId);
				}
			}

			/*
			 * add the people to the people container -- we will not add
			 * duplicates since this is the unsafe add.
			 */
			for (PersonId personId : expectedPeople) {
				peopleContainer.unsafeAdd(personId);

				// show that the people container does contain the person
				assertTrue(peopleContainer.contains(personId));
			}

		});
	}

	public static void testRemove(Function<Context, PeopleContainer> provider, long seed) {
		testConsumer(100, seed, (c) -> {

			// get the people container to test
			PeopleContainer peopleContainer = provider.apply(c);

			// get some data views that will be needed below
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			StochasticsDataView stochasticsDataView = c.getDataView(StochasticsDataView.class).get();
			RandomGenerator randomGenerator = stochasticsDataView.getRandomGenerator();

			// show that the simulation contains the correct number of people
			assertEquals(100, personDataView.getPopulationCount());

			// add about half of the people to the people we expect to find in
			// the people container
			List<PersonId> expectedPeople = new ArrayList<>();
			for (PersonId personId : personDataView.getPeople()) {
				if (randomGenerator.nextBoolean()) {
					expectedPeople.add(personId);
				}
			}

			// add the people to the people container
			for (PersonId personId : expectedPeople) {
				peopleContainer.safeAdd(personId);				
			}

			// show that the people container has the correct people with
			// no duplications
			List<PersonId> peopleList = peopleContainer.getPeople();
			assertEquals(expectedPeople.size(), peopleList.size());
			assertEquals(new LinkedHashSet<>(expectedPeople), new LinkedHashSet<>(peopleList));

			// remove the people from the people container, showing that the
			// person is removed
			for (PersonId personId : expectedPeople) {
				peopleContainer.remove(personId);
				assertFalse(peopleContainer.contains(personId));
			}

		});
	}

	public static void testSize(Function<Context, PeopleContainer> provider, long seed) {
		testConsumer(100, seed, (c) -> {

			// get the people container to test
			PeopleContainer peopleContainer = provider.apply(c);

			// get some data views that will be needed below
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			StochasticsDataView stochasticsDataView = c.getDataView(StochasticsDataView.class).get();
			RandomGenerator randomGenerator = stochasticsDataView.getRandomGenerator();

			// show that the simulation contains the correct number of people
			assertEquals(100, personDataView.getPopulationCount());

			// add about half of the people to the people we expect to find in
			// the people container
			List<PersonId> expectedPeople = new ArrayList<>();
			for (PersonId personId : personDataView.getPeople()) {
				if (randomGenerator.nextBoolean()) {
					expectedPeople.add(personId);
				}
			}

			// add the people to the people container, showing that the size
			// increments accordingly
			int expectedSize = 0;
			for (PersonId personId : expectedPeople) {
				peopleContainer.safeAdd(personId);
				expectedSize++;
				assertEquals(expectedSize, peopleContainer.size());
			}

			// remove the people from the people container, showing that the
			// size decrements accordingly
			for (PersonId personId : expectedPeople) {
				peopleContainer.remove(personId);
				expectedSize--;
				assertEquals(expectedSize, peopleContainer.size());
			}

		});
	}

	public static void testContains(Function<Context, PeopleContainer> provider, long seed) {
		testConsumer(100, seed, (c) -> {

			// get the people container to test
			PeopleContainer peopleContainer = provider.apply(c);

			// get some data views that will be needed below
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			StochasticsDataView stochasticsDataView = c.getDataView(StochasticsDataView.class).get();
			RandomGenerator randomGenerator = stochasticsDataView.getRandomGenerator();

			// show that the simulation contains the correct number of people
			assertEquals(100, personDataView.getPopulationCount());

			// add about half of the people to the people we expect to find in
			// the people container
			List<PersonId> expectedPeople = new ArrayList<>();
			List<PersonId> peopleNotIncluded = new ArrayList<>();
			for (PersonId personId : personDataView.getPeople()) {
				if (randomGenerator.nextBoolean()) {
					expectedPeople.add(personId);
				} else {
					peopleNotIncluded.add(personId);
				}

			}

			// add the people to the people container
			for (PersonId personId : expectedPeople) {
				peopleContainer.safeAdd(personId);
				assertTrue(peopleContainer.contains(personId));
			}

			// show that the people that were not added are not contained
			for (PersonId personId : peopleNotIncluded) {
				peopleContainer.remove(personId);
				assertFalse(peopleContainer.contains(personId));
			}

			// remove people and show they are no long contained
			for (PersonId personId : expectedPeople) {
				peopleContainer.remove(personId);
				assertFalse(peopleContainer.contains(personId));
			}

		});
	}

	public static void testGetRandomPersonId(Function<Context, PeopleContainer> provider, long seed) {
		testConsumer(100, seed, (c) -> {

			// get the people container to test
			PeopleContainer peopleContainer = provider.apply(c);

			// get some data views that will be needed below
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			StochasticsDataView stochasticsDataView = c.getDataView(StochasticsDataView.class).get();
			RandomGenerator randomGenerator = stochasticsDataView.getRandomGenerator();

			// show that the simulation contains the correct number of people
			assertEquals(100, personDataView.getPopulationCount());

			// add about half of the people to the people we expect to find in
			// the people container
			List<PersonId> expectedPeopleList = new ArrayList<>();
			List<PersonId> peopleNotIncluded = new ArrayList<>();
			for (PersonId personId : personDataView.getPeople()) {
				if (randomGenerator.nextBoolean()) {
					expectedPeopleList.add(personId);
				} else {
					peopleNotIncluded.add(personId);
				}

			}

			assertNull(peopleContainer.getRandomPersonId(randomGenerator));

			// add the people to the people container
			for (PersonId personId : expectedPeopleList) {
				peopleContainer.safeAdd(personId);
			}

			/*
			 * Make some random selections from the people container. Show that
			 * each selection is a person contained in the people container
			 */
			Set<PersonId> expectedPeopleSet = new LinkedHashSet<>(expectedPeopleList);
			for (int i = 0; i < 1000; i++) {
				PersonId randomPersonId = peopleContainer.getRandomPersonId(randomGenerator);
				assertTrue(expectedPeopleSet.contains(randomPersonId));
			}

			// remove up to 20 people in a random order
			Random random = new Random(randomGenerator.nextLong());
			Collections.shuffle(expectedPeopleList, random);
			int n = FastMath.min(20, expectedPeopleList.size());

			for (int i = 0; i < n; i++) {
				PersonId personId = expectedPeopleList.remove(0);
				peopleContainer.remove(personId);
				expectedPeopleSet.remove(personId);
			}

			// show that the people selected after the removal are still
			// consistent with the people in the people container
			for (int i = 0; i < 1000; i++) {
				PersonId randomPersonId = peopleContainer.getRandomPersonId(randomGenerator);
				assertTrue(expectedPeopleSet.contains(randomPersonId));
			}

		});
	}
}