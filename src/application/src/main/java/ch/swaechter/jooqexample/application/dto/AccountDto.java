package ch.swaechter.jooqexample.application.dto;

import java.util.UUID;

public class AccountDto {

    private UUID id;

    private String userName;

    private String emailAddress;

    public AccountDto() {
    }

    public AccountDto(UUID id, String userName, String emailAddress) {
        this.id = id;
        this.userName = userName;
        this.emailAddress = emailAddress;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
