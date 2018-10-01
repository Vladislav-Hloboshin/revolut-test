package ru.vladislav.revolut.domain;

import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.RelationalPathBase;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QAccountEntity is a Querydsl query type for account table
 */
public class QAccountEntity extends RelationalPathBase<QAccountEntity> {

    public static final QAccountEntity account = new QAccountEntity("ACCOUNT");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Long> balance = createNumber("balance", Long.class);

    public QAccountEntity(String variable) {
        super(QAccountEntity.class, forVariable(variable), "", "ACCOUNT");
    }

}

