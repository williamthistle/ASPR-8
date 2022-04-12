package plugins.globals;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.annotations.UnitTest;

@UnitTest(target = GlobalsPluginId.class)
public class AT_GlobalsPluginId {

	public void test() {
		assertNotNull(GlobalsPluginId.PLUGIN_ID);
	}
}
