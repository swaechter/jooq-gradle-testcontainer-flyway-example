package ch.swaechter.jooqexample.application;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public class ApplicationTest {

    @Container
    public static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
        .withDatabaseName("jooqexample")
        .withUsername("postgres")
        .withPassword("123456aA");

    @Test
    public void testApplication() throws Exception {
        // Create the application
        Application application = new Application();

        // Create the datasource and migrate the schemas. Please create this database. Schemas will be applied
        DataSource dataSource = application.createDataSource("localhost", postgreSQLContainer.getFirstMappedPort(), "jooqexample", "postgres", "123456aA");
        application.executeFlywayMigration(dataSource);

        // Execute SQL account stuff
        application.executeSql(dataSource);
    }
}
