package twx.core.db.dbspec;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.logging.Logger;

public class DbSpecTest {
    static final Logger log = Logger.getLogger(DbSpecTest.class.getName());
    DbSpec sut;

    @Test
    void isInstantiatedWithNew() {
        assertNotNull(new DbSpec("Name"));
    }

    @Nested
    public class givenNewSpec {
        @BeforeEach
        void createNewSpec() {
            sut = new DbSpec("Name");
        }

        @Test
        void shouldBeRoot() {
            assertTrue(sut.isRoot());
            assertNull(sut.getParent());
            assertEquals(0, sut.getLevel());
        }

        @Test
        void shouldHaveOneChilds() {
            assertFalse(sut.isLeaf());
            assertEquals(1, sut.getChildCount());
        }

        @Test
        void shouldReturnItselfAsSpec() {
            assertEquals(sut, sut.getSpec());
        }

        @Test
        void shouldHaveValidName() {
            assertEquals("Name", sut.getName());
            assertEquals("Name", sut.getFullName());
        }

        @Test
        void shouldHaveDefaultSchema() {
            assertNotNull(sut.getDefaultSchema());
            assertNotNull(sut.getChild(DbSpec.DEFAULT_SCHEMA_NAME));
            assertEquals(1, sut.getSchemas().size());
        }

        @Test
        void shouldGiveValidJSON() {
            String str = sut.toJSON().toString(2);
            assertNotNull(str);
            log.info(str);
        }

        @Nested
        class afterSchemaAdd {
            @BeforeEach
            void addSchema() {
                sut.addSchema("Test");
            }

            @Test
            void shouldHaveSchemas() {
                assertEquals(2, sut.getSchemas().size());
                assertNotNull(sut.getSchema("Test"));
                assertNotNull(sut.getChild("Test"));
            }

            @Test
            void shouldGiveValidParentAndSpec() {
                DbSchema schema = sut.getSchema("Test");
                assertEquals(sut, schema.getParent());
                assertEquals(sut, schema.getSpec());
            }

            @Test
            void shouldHaveTwoChilds() {
                assertFalse(sut.isLeaf());
                assertEquals(2, sut.getChildCount());
            }

            @Test
            void shouldGiveValidJSON() {
                String str = sut.toJSON().toString(2);
                assertNotNull(str);
                log.info(str);
            }
        }
    }
}
