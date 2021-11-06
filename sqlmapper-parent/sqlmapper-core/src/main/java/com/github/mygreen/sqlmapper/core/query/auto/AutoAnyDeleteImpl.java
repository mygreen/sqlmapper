package com.github.mygreen.sqlmapper.core.query.auto;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.Predicate;

import lombok.Getter;
import lombok.NonNull;

/**
 * 任意の条件を指定して削除を行うSQLを自動生成するクエリの実装です。
 *
 * @author T.TSUCHIE
 * @param <T> 処理対象となるエンティティの型
 *
 */
public class AutoAnyDeleteImpl<T> implements AutoAnyDelete<T> {

    /**
     * SqlMapperの設定情報。
     */
    @Getter
    private final SqlMapperContext context;

    @Getter
    private final Class<T> baseClass;

    @Getter
    private final EntityPath<T> entityPath;

    @Getter
    private final EntityMeta entityMeta;

    @Getter
    private Integer queryTimeout;

    /**
     * クライテリアです。
     */
    @Getter
    private Predicate where;

    @SuppressWarnings("unchecked")
    public AutoAnyDeleteImpl(@NonNull SqlMapperContext context, @NonNull EntityPath<T> entityPath) {
        this.context = context;
        this.entityPath = entityPath;
        this.entityMeta = context.getEntityMetaFactory().create(entityPath.getType());
        this.baseClass = (Class<T>)entityMeta.getEntityType();
    }

    @Override
    public AutoAnyDeleteImpl<T> queryTimeout(int seconds) {
        this.queryTimeout = seconds;
        return this;
    }


    @Override
    public AutoAnyDeleteImpl<T> where(@NonNull Predicate where) {
        this.where = where;
        return this;
    }

    @Override
    public int execute() {
        return new AutoAnyDeleteExecutor(this)
                .execute();
    }

}
