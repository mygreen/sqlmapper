package com.github.mygreen.sqlmapper.core.testdata;

import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.UtilDatePath;

/**
 * {@link EntityBase} のメタモデル
 * ベースクラス
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class MEntityBase<E extends EntityBase> extends EntityPathBase<E> {

    public MEntityBase(Class<? extends E> type, String name) {
        super(type, name);
    }

    public final UtilDatePath createAt = createUtilDate("createAt");

    public final UtilDatePath updateAt = createUtilDate("updateAt");

    public final NumberPath<Long> version = createNumber("version", Long.class);

}
