package twx.core.db.dbspec;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import twx.core.db.model.DbModel;
import twx.core.db.model.DbSchema;

import java.util.logging.Logger;

public class DbSpecTest {
    static final Logger log = Logger.getLogger(DbSpecTest.class.getName());
    DbModel sut;

    @Test
    void isInstantiatedWithNew() {
        assertNotNull(new DbModel("Name"));
    }

    @Nested
    public class givenNewSpec {
        @BeforeEach
        void createNewSpec() {
            sut = new DbModel("Name");
        }

        @Test
        void shouldBeRoot() {
            assertTrue(sut.isRoot());
            assertNull(sut.getParent());
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
        void shouldHaveNoSchema() {
            assertNull(sut.getDefaultSchema());
            assertEquals(0, sut.getSchemas().size());
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
            void addSchemas() {
                // sut.addDefaultSchema();
                sut.addSchema("Test");
            }

            @Test
            void shouldHaveSchemas() {
                // assertEquals(1, sut.getSchemas().size());
                // assertNotNull(sut.getSchema("Test"));
                // assertNotNull(sut.getDefaultSchema());
            }

            @Test
            void shouldGiveValidParentAndSpec() {
                DbSchema schema = sut.getSchema("Test");
                assertEquals(sut, schema.getParent());
                assertEquals(sut, schema.getSpec());
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
