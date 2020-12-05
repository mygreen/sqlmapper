package com.github.mygreen.sqlmapper.core.query.auto;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.query.QuerySupport;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.Predicate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * 任意の条件でSQLを自動生成する削除です。
 *
 * @author T.TSUCHIE
 * @param <T> 処理対象となるエンティティの型
 *
 */
public class AutoAnyDelete<T> extends QuerySupport<T> {

    @Getter(AccessLevel.PACKAGE)
    private final Class<T> baseClass;

    @Getter(AccessLevel.PACKAGE)
    private final EntityPath<T> entityPath;

    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    /**
     * クライテリアです。
     */
    @Getter(AccessLevel.PACKAGE)
    private Predicate where;

    @SuppressWarnings("unchecked")
    public AutoAnyDelete(@NonNull SqlMapperContext context, @NonNull EntityPath<T> entityPath) {
        super(context);
        this.entityPath = entityPath;
        this.entityMeta = context.getEntityMetaFactory().create(entityPath.getType());
        this.baseClass = (Class<T>)entityMeta.getEntityType();
    }

    /**
     * 検索条件を指定します。
     * @param where 検索条件。
     * @return 自身のインスタンス。
     */
    public AutoAnyDelete<T> where(@NonNull Predicate where) {
        this.where = where;
        return this;
    }

    /**
     * クエリを実行します。
     * @return 削除したレコード件数を返します。
     */
    public int execute() {

        AutoAnyDeleteExecutor executor = new AutoAnyDeleteExecutor(this);
        executor.prepare();
        return executor.execute();
    }

}
