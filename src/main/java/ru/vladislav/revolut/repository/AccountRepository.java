package ru.vladislav.revolut.repository;

import com.querydsl.core.Tuple;
import ru.vladislav.revolut.domain.AccountReadOnlyEntity;
import ru.vladislav.revolut.domain.QAccountEntity;
import ru.vladislav.revolut.querydsl.QueryDSLConnection;

import java.util.List;
import java.util.stream.Collectors;

public class AccountRepository {

    // for typed queries
    private static final QAccountEntity qAccount = QAccountEntity.account;

    public List<AccountReadOnlyEntity> findAllByIdsWithLock(QueryDSLConnection queryDSLConn, List<Integer> ids) {
        List<Tuple> accounts = queryDSLConn.select(qAccount.all())
                .from(qAccount)
                .where(qAccount.id.in(ids))
                .forUpdate()
                .fetch();
        return accounts.stream()
                .map(AccountReadOnlyEntity::new)
                .collect(Collectors.toList());
    }

    public AccountReadOnlyEntity findOneById(QueryDSLConnection queryDSLConn, int id) {
        Tuple account = queryDSLConn.select(qAccount.all())
                .from(qAccount)
                .where(qAccount.id.eq(id))
                .fetchOne();
        return account==null ? null : new AccountReadOnlyEntity(account);
    }

    public long updateBalanceById(QueryDSLConnection queryDSLConn, long balance, int id) {
        return queryDSLConn.update(qAccount)
                .set(qAccount.balance, balance)
                .where(qAccount.id.eq(id))
                .execute();
    }
}
