package ch.swaechter.jooqexample.generator;

import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.meta.postgres.PostgresDatabase;
import org.jooq.tools.jdbc.JDBCUtils;
import org.postgresql.Driver;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.util.Properties;

/**
 * Provide a custom jOOQ PostgreSQL database wrapper that starts a PostgreSQL instance via testcontainers and applies
 * all Flyway schemas. This instance is only used to compile the Java classes at compile time (No further usage like
 * on runtime).
 *
 * @author Simon Wächter
 */
public class StandalonePostgreSqlDatabase extends PostgresDatabase {

    private static final String DEFAULT_FLYWAY_LOCATION = "filesystem:src/main/resources/db/migration";

    private static final String DEFAULT_DOCKER_IMAGE = "postgres:13";

    private PostgreSQLContainer<?> postgreSQLContainer;

    private Connection connection;

    @Override
    protected DSLContext create0() {
        if (connection == null) {
            try {
                // Create the PostgreSQL container
                postgreSQLContainer = new PostgreSQLContainer<>(DEFAULT_DOCKER_IMAGE)
                    .withDatabaseName("database")
                    .withUsername("username")
                    .withPassword("password");

                // Start the PostgreSQL container
                postgreSQLContainer.start();

                // Create and connect the JDBC driver
                Driver driver = new Driver();
                Properties properties = new Properties();
                properties.put("user", postgreSQLContainer.getUsername());
                properties.put("password", postgreSQLContainer.getPassword());
                connection = driver.connect(postgreSQLContainer.getJdbcUrl(), properties);

                // Use the common Flyway location as single input source
                String[] locations = {DEFAULT_FLYWAY_LOCATION};

                // Use the datasource of the test container to execute the Flyway migration
                Flyway.configure().dataSource(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword())
                    .locations(locations)
                    .schemas("public")
                    .load()
                    .migrate();

                // Set the current connection so it can be reused
                setConnection(connection);
            } catch (Exception exception) {
                throw new DataAccessException("Unable to start the database container and migrate the schemas: " + exception.getMessage(), exception);
            }
        }
        return DSL.using(connection, SQLDialect.POSTGRES);
    }

    @Override
    public void close() {
        // Close the connection properly
        JDBCUtils.safeClose(connection);
        connection = null;
        super.close(); // Empty method call

        // Stop the container
        postgreSQLContainer.stop();
    }
}
