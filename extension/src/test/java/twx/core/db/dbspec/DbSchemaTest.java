package twx.core.db.dbspec;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import twx.core.db2.model.DbModel;
import twx.core.db2.model.DbSchema;
import twx.core.db2.model.DbTable;

import java.util.logging.Logger;

public class DbSchemaTest {
    static final Logger log = Logger.getLogger(DbSpecTest.class.getName());
    DbModel spec;
    DbSchema sut;

    @Nested
    public class givenTestSchema {
        @BeforeEach
        void createNewSpec() {
            spec = new DbModel("Spec");
            sut = spec.addSchema("Schema");
            log.info("DbSchemaTest.BeforeEach()");
        }

        @Test
        void shouldNotBeRoot() {
            assertFalse(sut.isRoot());
            assertNotNull(sut.getParent());
        }

        @Test
        void shouldHaveNoChilds() {
            assertEquals(0, sut.getTables().size());
        }

        @Test
        void shouldGiveValidParentAndSpec() {
            assertEquals(spec, sut.getParent());
            assertEquals(spec, sut.getModel());
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