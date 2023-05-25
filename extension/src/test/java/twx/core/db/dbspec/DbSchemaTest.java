package twx.core.db.dbspec;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.logging.Logger;

public class DbSchemaTest {
    static final Logger log = Logger.getLogger(DbSpecTest.class.getName());
    DbSpec spec;
    DbSchema sut;

    @Nested
    public class givenTestSchema {
        @BeforeEach
        void createNewSpec() {
            spec = new DbSpec("Spec");
            sut = spec.addSchema("Schema");
            log.info("DbSchemaTest.BeforeEach()");
        }

        @Test
        void shouldNotBeRoot() {
            assertFalse(sut.isRoot());
            assertNotNull(sut.getParent());
            assertEquals(1, sut.getLevel());
        }

        @Test
        void shouldHaveNoChilds() {
            assertTrue(sut.isLeaf());
            assertEquals(0, sut.getChildCount());
            assertEquals(0, sut.getTables().size());
        }

        @Test
        void shouldGiveValidParentAndSpec() {
            assertEquals(spec, sut.getParent());
            assertEquals(spec, sut.getSpec());
        }

        @Test
        void shouldHaveValidName() {
            assertEquals("Schema", sut.getName());
            assertEquals("Spec.Schema", sut.getFullName());
        }

        @Nested
        public class whenAddingATable {
            DbTable table;

            @BeforeEach
            void createNewSpec() {
                table = sut.addTable("Table");
                log.info("whenAddingATable.BeforeEach()");
            }

            @Test
            void shouldHaveOneChild() {
                assertFalse(sut.isLeaf());
                assertEquals(1, sut.getChildCount());
            }

            @Test
            void shouldGiveValidTable() {
                assertNotNull(sut.getTable("Table"));
            }

            @Test
            void shouldGiveValidJSON() {
                String str = spec.toJSON().toString(2);
                assertNotNull(str);
                log.info(str);
            }
        }
    }
}