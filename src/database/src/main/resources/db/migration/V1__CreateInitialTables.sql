CREATE TABLE account(
    id UUID NOT NULL,
    user_name TEXT NOT NULL,
    email_address TEXT NOT NULL,
    CONSTRAINT account_primarykey PRIMARY KEY(id),
    CONSTRAINT account_unique_username UNIQUE(user_name)
);
