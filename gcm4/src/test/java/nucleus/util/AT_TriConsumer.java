package nucleus.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tools.annotations.UnitTest;
import tools.annotations.UnitTestMethod;
import util.wrappers.MutableInteger;

@UnitTest(target = TriConsumer.class)
public class AT_TriConsumer {

	@Test
	@UnitTestMethod(name = "andThen", args= {TriConsumer.class})
	public void testAndThen() {
		
		MutableInteger value = new MutableInteger();
		
		TriConsumer<Integer, String, Double> t1 = (i,s,d)->{
			value.increment(5);
		};
		TriConsumer<Integer, String, Double> t2 = (i,s,d)->{
			value.increment(17);
		};
		
		t1.andThen(t2).accept(6, "4", 2.6);;
		
		assertEquals(22,value.getValue());
	}
	
}
