/**
 * 
 */
package twx.core.concurrency;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import twx.core.concurrency.imp.*;

class AtomicManagerTest {
	AtomicManager 	sut = AtomicManager.getInstance();
	
	@Nested 
	class GivenAtomicManager {
		@Test
	    @DisplayName("then there should be only one instance")
	    void thenThereShouldBeOnlyOneInstance() {
			assertEquals(sut, AtomicManager.getInstance() );
		}
	}

/*	
	@Test
	void testGetInstance() {
		assertEquals(sut, AtomicManager.getInstance());
	}

	@Test
	void testGetById() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteById() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteAll() {
		fail("Not yet implemented");
	}

	@Test
	void testExists() {
		fail("Not yet implemented");
	}

	@Test
	void testGet() {
		fail("Not yet implemented");
	}

	@Test
	void testSet() {
		fail("Not yet implemented");
	}

	@Test
	void testIncrementAndGet() {
		fail("Not yet implemented");
	}

	@Test
	void testDecrementAndGet() {
		fail("Not yet implemented");
	}

	@Test
	void testAddAndGet() {
		fail("Not yet implemented");
	}

	@Test
	void testCompareAndSet() {
		fail("Not yet implemented");
	}
*/
}
