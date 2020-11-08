package com.github.mygreen.sqlmapper.core.where.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VisitorContext {

    /**
     * 検索対象となるテーブルのエンティティ情報
     */
    @Getter
    private final EntityMeta entityMeta;

    /**
     * SQLの方言情報
     */
    @Getter
    private final Dialect dialect;

    /**
     * SQL中のパラメータ変数。
     *
     */
    @Getter
    private List<Object> paramValues = new ArrayList<>();

    /**
     * 組み立てたクライテリア
     */
    private StringBuilder criteria = new StringBuilder();

    /**
     * 親の情報を引き継いでインスタンスを作成します。
     * <p>引き継ぐ情報。
     * <ul>
     *  <li>{@link #entityMeta}</li>
     *  <li>{@link #dialect}</li>
     * </ul>
     * @param parent 親のコンテキスト
     */
    public VisitorContext(VisitorContext parent) {
        this.entityMeta = parent.entityMeta;
        this.dialect = parent.dialect;
    }

    public void addParamValue(Object value) {
        this.paramValues.add(value);
    }

    public void addParamValues(Collection<?> values) {
        this.paramValues.addAll(values);
    }

    public StringBuilder appendSql(String sql) {
        this.criteria.append(sql);
        return criteria;
    }

    public String getCriteria() {
        return criteria.toString();
    }

}
