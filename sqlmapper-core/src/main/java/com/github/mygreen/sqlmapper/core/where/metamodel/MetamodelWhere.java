package com.github.mygreen.sqlmapper.core.where.metamodel;

import com.github.mygreen.sqlmapper.core.where.Where;
import com.github.mygreen.sqlmapper.core.where.WhereVisitor;
import com.github.mygreen.sqlmapper.metamodel.Predicate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * メタモデルを扱う条件式。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class MetamodelWhere implements Where {

    /**
     * 終端の条件式
     */
    @Getter
    private final Predicate predicate;

    @Override
    public void accept(WhereVisitor visitor) {

        visitor.visit(this);

    }
}
