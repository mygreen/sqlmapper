package com.github.mygreen.sqlmapper.core.query.auto;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.query.QuerySupport;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.Predicate;

import lombok.Getter;
import lombok.NonNull;

/**
 * 任意の条件でSQLを自動生成する削除です。
 *
 * @author T.TSUCHIE
 * @param <T> 処理対象となるエンティティの型
 *
 */
public class AutoAnyDeleteImpl<T> extends QuerySupport<T> implements AutoAnyDelete<T> {

    @Getter
    private final Class<T> baseClass;

    @Getter
    private final EntityPath<T> entityPath;

    @Getter
    private final EntityMeta entityMeta;

    /**
     * クライテリアです。
     */
    @Getter
    private Predicate where;

    @SuppressWarnings("unchecked")
    public AutoAnyDeleteImpl(@NonNull SqlMapperContext context, @NonNull EntityPath<T> entityPath) {
        super(context);
        this.entityPath = entityPath;
        this.entityMeta = context.getEntityMetaFactory().create(entityPath.getType());
        this.baseClass = (Class<T>)entityMeta.getEntityType();
    }

    @Override
    public AutoAnyDeleteImpl<T> where(@NonNull Predicate where) {
        this.where = where;
        return this;
    }

    @Override
    public int execute() {

        AutoAnyDeleteExecutor executor = new AutoAnyDeleteExecutor(this);
        executor.prepare();
        return executor.execute();
    }

}
