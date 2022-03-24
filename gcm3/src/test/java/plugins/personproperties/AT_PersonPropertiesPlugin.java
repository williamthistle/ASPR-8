package plugins.personproperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import annotations.UnitTest;
import annotations.UnitTestMethod;
import nucleus.Plugin;
import nucleus.PluginId;
import plugins.partitions.PartitionsPluginId;
import plugins.people.PeoplePluginId;
import plugins.personproperties.testsupport.PersonPropertiesActionSupport;
import plugins.regions.RegionPluginId;

@UnitTest(target = PersonPropertiesPlugin.class)
public class AT_PersonPropertiesPlugin {

	@Test
	@UnitTestMethod(name = "getPersonPropertyPlugin", args = {PersonPropertiesPluginData.class})
	public void testGetRegionPlugin() {
		PersonPropertiesPluginData personPropertiesPluginData = PersonPropertiesPluginData.builder().build();
		Plugin personPropertiesPlugin = PersonPropertiesPlugin.getPersonPropertyPlugin(personPropertiesPluginData);

		assertEquals(1,personPropertiesPlugin.getPluginDatas().size());
		assertTrue(personPropertiesPlugin.getPluginDatas().contains(personPropertiesPluginData));

		assertEquals(PersonPropertiesPluginId.PLUGIN_ID, personPropertiesPlugin.getPluginId());

		Set<PluginId> expectedDependencies = new LinkedHashSet<>();
		expectedDependencies.add(PartitionsPluginId.PLUGIN_ID);
		expectedDependencies.add(PeoplePluginId.PLUGIN_ID);
		expectedDependencies.add(RegionPluginId.PLUGIN_ID);
		
		assertEquals(expectedDependencies, personPropertiesPlugin.getPluginDependencies());

		PersonPropertiesActionSupport.testConsumer(0, 6578534453778788L, (c) -> {
			assertTrue(c.getDataManager(PersonPropertiesDataManager.class).isPresent());
		});

	}

}
