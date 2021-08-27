package ch.swaechter.jooqexample.application;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

public class Application {

    private static final String POSTGRESQL_HOSTNAME = "localhost";

    private static final Integer POSTGRESQL_PORT = 5432;

    private static final String POSTGRESQL_DATABASE = "jooqexample"; // Please create this database. Schemas will be applied

    private static final String POSTGRESQL_USERNAME = "postgres";

    private static final String POSTGRESQL_PASSWORD = "123456aA";

    public static void main(String[] arguments) throws Exception {
        // Create the datasource and migrate the schemas
        DataSource dataSource = createDataSource();
        executeFlywayMigration(dataSource);

        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Connection established: " + connection.getClass().getSimpleName());
            executeFlywayMigration(dataSource);
        } catch (Exception exception) {
            System.err.println("An error occurred: " + exception.getMessage());
        }
    }

    private static DataSource createDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{POSTGRESQL_HOSTNAME});
        dataSource.setPortNumbers(new int[]{POSTGRESQL_PORT});
        dataSource.setDatabaseName(POSTGRESQL_DATABASE);
        dataSource.setUser(POSTGRESQL_USERNAME);
        dataSource.setPassword(POSTGRESQL_PASSWORD);
        return dataSource;
    }

    private static void executeFlywayMigration(DataSource dataSource) throws Exception {
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        try {
            flyway.migrate();
        } catch (FlywayException exception) {
            throw new Exception("Unable to migrate schema with Flyway: " + exception.getMessage(), exception);
        }
    }
}
