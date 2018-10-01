package ru.vladislav.revolut.domain;

import com.querydsl.core.Tuple;

public class AccountReadOnlyEntity {

    private final Tuple account;

    public AccountReadOnlyEntity(Tuple account) {
        this.account = account;
    }

    @SuppressWarnings("ConstantConditions")
    public int getId() {
        return account.get(QAccountEntity.account.id);
    }

    @SuppressWarnings("ConstantConditions")
    public long getBalance() {
        return account.get(QAccountEntity.account.balance);
    }
}
