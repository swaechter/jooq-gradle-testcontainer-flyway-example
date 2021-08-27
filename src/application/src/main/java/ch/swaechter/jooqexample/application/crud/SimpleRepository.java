package ch.swaechter.jooqexample.application.crud;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.impl.TableImpl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

// Inspired by: https://github.com/jOOQ/jOOQ/issues/5984
public abstract class SimpleRepository<DTO, Record extends UpdatableRecord<Record>, Table extends TableImpl<Record>> {

    // Disable the jOOQ banner printing and feel bad because we use and enforce FLOSS software like PostgreSQL
    static {
        System.getProperties().setProperty("org.jooq.no-logo", "true");
    }

    private final DSLContext dslContext;

    private final Table table;

    public SimpleRepository(DSLContext dslContext, Table table) {
        this.dslContext = dslContext;
        this.table = table;
    }

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

    protected List<DTO> readMany(Function<Table, Condition> function) {
        List<Record> records = dslContext.fetch(table, function.apply(table));
        return records.stream().map(this::mapRecordToDto).collect(Collectors.toList());
    }

    protected List<DTO> readAll() {
        List<Record> records = dslContext.fetch(table);
        return records.stream().map(this::mapRecordToDto).collect(Collectors.toList());
    }

    protected Optional<DTO> readOne(Function<Table, Condition> function) {
        try {
            Record record = dslContext.fetchOne(table, function.apply(table));
            return Optional.of(mapRecordToDto(record));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    protected Optional<DTO> updateOne(DTO dto) {
        try {
            Record record = dslContext.newRecord(table);
            mapDtoToRecord(dto, record);
            record.update();
            return Optional.of(mapRecordToDto(record));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    protected void deleteMany(Function<Table, Condition> function) {
        dslContext.delete(table).where(function.apply(table)).execute();
    }

    protected DSLContext getDslContext() {
        return dslContext;
    }

    protected abstract void mapDtoToRecord(DTO dto, Record record);

    protected abstract DTO mapRecordToDto(Record record);
}
