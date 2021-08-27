package ch.swaechter.jooqexample.application.crud;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.impl.TableImpl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class provides a generified version of a table repository. A developer can extend it and use the core methods
 * to create new rows, fetch many/all rows and update/delete them.
 *
 * @param <DTO>    DTO that is used after reading the data. We encapsulate all data in DTOs
 * @param <Record> Record that represents an insertable/readable entry
 * @param <Table>  Table that will be used to read/insert/update rows from.
 * @author Simon Wächter
 */
// Inspired by: https://github.com/jOOQ/jOOQ/issues/5984
public abstract class SimpleRepository<DTO, Record extends UpdatableRecord<Record>, Table extends TableImpl<Record>> {

    // Disable the jOOQ banner printing. I feel bad because we use and enforce FLOSS software like PostgreSQL and don't fancy Lukas.
    static {
        System.getProperties().setProperty("org.jooq.no-logo", "true");
    }

    /**
     * The DSL context that wraps the initial connection.
     */
    private final DSLContext dslContext;

    /**
     * Table so we know from where we read data and insert/update/delete data.
     */

    private final Table table;

    /**
     * Create a new abstract repository. Caller hat to implement the mapper methods.
     *
     * @param dslContext DSL context
     * @param table      SQL table
     */
    public SimpleRepository(DSLContext dslContext, Table table) {
        this.dslContext = dslContext;
        this.table = table;
    }

    /**
     * Create one new SQL row.
     *
     * @param dto DTO to convert and insert into the SQL table
     * @return Inserted row
     */
    protected Optional<DTO> createOne(DTO dto) {
        try {
            Record record = dslContext.newRecord(table);
            mapDtoToRecord(dto, record);
            record.store();
            return Optional.of(mapRecordToDto(record));
        } catch (Exception exception) {
            throw new RuntimeException("Unable to create object. Please see log files for more information");
        }
    }

    /**
     * Read none to many rows.
     *
     * @param function Function to generate an SQL query
     * @return None to many rows
     */
    protected List<DTO> readMany(Function<Table, Condition> function) {
        List<Record> records = dslContext.fetch(table, function.apply(table));
        return records.stream().map(this::mapRecordToDto).collect(Collectors.toList());
    }

    /**
     * Read all rows.
     *
     * @return All rows
     */
    protected List<DTO> readAll() {
        List<Record> records = dslContext.fetch(table);
        return records.stream().map(this::mapRecordToDto).collect(Collectors.toList());
    }

    /**
     * Read none or one row. This method is probably completely broken due it's error handling and Lukas will tell me
     * how to do it better.
     *
     * @param function Function to generate an SQL query that will select none or one row
     * @return Missing row or mapped row itself
     */
    protected Optional<DTO> readOne(Function<Table, Condition> function) {
        try {
            Record record = dslContext.fetchOne(table, function.apply(table));
            return Optional.of(mapRecordToDto(record));
        } catch (Exception exception) { // TODO: How to fix this broken code? Is there a more fine granular exception like NoRowException or so?
            return Optional.empty();
        }
    }

    /**
     * Update a single row.
     *
     * @param dto Row to update
     */
    protected void updateOne(DTO dto) {
        Record record = dslContext.newRecord(table);
        mapDtoToRecord(dto, record);
        record.update();
    }

    /**
     * Delete none to many rows.
     *
     * @param function Function to generate an SQL query to delete the rows
     */
    protected void deleteMany(Function<Table, Condition> function) {
        dslContext.delete(table).where(function.apply(table)).execute();
    }

    /**
     * Map a DTO object to a SQL result/record set.
     *
     * @param dto    DTO to map
     * @param record Record to inserted/updated etc.
     */
    protected abstract void mapDtoToRecord(DTO dto, Record record);

    /**
     * Map a result/record set to a DTO
     *
     * @param record SQL record with the row data
     * @return Mapped DTO
     */
    protected abstract DTO mapRecordToDto(Record record);
}
