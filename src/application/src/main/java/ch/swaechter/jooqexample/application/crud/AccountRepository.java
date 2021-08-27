package ch.swaechter.jooqexample.application.crud;

import ch.swaechter.jooqexample.api.tables.Account;
import ch.swaechter.jooqexample.api.tables.records.AccountRecord;
import ch.swaechter.jooqexample.application.dto.AccountDto;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Optional;

import static ch.swaechter.jooqexample.api.tables.Account.ACCOUNT;

public class AccountRepository extends SimpleRepository<AccountDto, AccountRecord, Account> {

    public AccountRepository(DSLContext dslContext) {
        super(dslContext, ACCOUNT);
    }

    public Optional<AccountDto> saveAccount(AccountDto accountDto) {
        return createOne(accountDto);
    }

    public List<AccountDto> getAccounts() {
        return readAll();
    }

    @Override
    protected void mapDtoToRecord(AccountDto accountDto, AccountRecord record) {
        if (accountDto.getId() != null) {
            record.setId(accountDto.getId());
        }
        record.setUserName(accountDto.getUserName());
        record.setEmailAddress(accountDto.getEmailAddress());
    }

    @Override
    protected AccountDto mapRecordToDto(AccountRecord record) {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(record.getId());
        accountDto.setUserName(record.getUserName());
        accountDto.setEmailAddress(record.getEmailAddress());
        return accountDto;
    }
}
