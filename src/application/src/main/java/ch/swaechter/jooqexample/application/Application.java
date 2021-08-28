package ch.swaechter.jooqexample.application;

import ch.swaechter.jooqexample.application.crud.AccountRepository;
import ch.swaechter.jooqexample.application.dto.AccountDto;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.UUID;

public class Application {

    public static void main(String[] arguments) throws Exception {
        // Create the application
        Application application = new Application();

        // Create the datasource and migrate the schemas. Please create this database. Schemas will be applied
        DataSource dataSource = application.createDataSource("localhost", 5432, "jooqexample", "postgres", "123456aA");
        application.executeFlywayMigration(dataSource);

        // Execute SQL account stuff
        application.executeSql(dataSource);
    }

    public DataSource createDataSource(String hostname, Integer port, String database, String username, String password) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{hostname});
        dataSource.setPortNumbers(new int[]{port});
        dataSource.setDatabaseName(database);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    public void executeFlywayMigration(DataSource dataSource) throws Exception {
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        try {
            flyway.migrate();
        } catch (FlywayException exception) {
            throw new Exception("Unable to migrate schema with Flyway: " + exception.getMessage(), exception);
        }
    }

    public void executeSql(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Connection established: " + connection.getClass().getSimpleName());

            // Create the DSL context
            try (DSLContext dslContext = DSL.using(connection)) {
                // Execute the Flyway migration
                executeFlywayMigration(dataSource);

                // Create the account repository to access the data
                AccountRepository accountRepository = new AccountRepository(dslContext);

                // Create two new accounts
                String[] userNames = new String[]{"Simon", "Lukas"};
                for (String userName : userNames) {
                    UUID id = UUID.randomUUID();
                    accountRepository.saveAccount(new AccountDto(id, userName + "_" + id, userName + "_" + id + "@gmail.com"));
                }

                // List all accounts (Will accumulate over time)
                for (AccountDto accountDto : accountRepository.getAccounts()) {
                    System.out.println("ID: " + accountDto.getId() + " | Username: " + accountDto.getUserName() + " | Email: " + accountDto.getEmailAddress());
                }
            }
        } catch (Exception exception) {
            System.err.println("An error occurred: " + exception.getMessage());
        }
    }
}
