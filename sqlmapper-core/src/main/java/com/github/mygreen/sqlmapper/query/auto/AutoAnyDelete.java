package com.github.mygreen.sqlmapper.query.auto;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.query.QueryBase;
import com.github.mygreen.sqlmapper.where.Where;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * 任意の条件で削除を実行します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class AutoAnyDelete<T> extends QueryBase<T> {

    @Getter(AccessLevel.PACKAGE)
    private final Class<T> baseClass;

    @Getter(AccessLevel.PACKAGE)
    private final EntityMeta entityMeta;

    /**
     * クライテリアです。
     */
    @Getter(AccessLevel.PACKAGE)
    private Where criteria;

    public AutoAnyDelete(@NonNull SqlMapperContext context, @NonNull Class<T> baseClass) {
        super(context);
        this.baseClass = baseClass;
        this.entityMeta = context.getEntityMetaFactory().create(baseClass);
    }

    /**
     * 検索条件を指定します。
     * @param where 検索条件。
     * @return 自身のインスタンス。
     */
    public AutoAnyDelete<T> where(@NonNull Where where) {
        this.criteria = where;
        return this;
    }

    /**
     * クエリを実行します。
     * @return 削除したレコード件数を返します。
     */
    public int execute() {

        assertNotCompleted("execute");

        AutoAnyDeleteExecutor executor = new AutoAnyDeleteExecutor(this);
        try {
            executor.prepare();
            return executor.execute();

        } finally {
            completed();
        }

    }

}
