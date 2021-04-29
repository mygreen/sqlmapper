package com.github.mygreen.sqlmapper.metamodel.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

import lombok.Getter;
import lombok.Setter;

/**
 * クエリの情報を保持するクラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <Q> クエリのクラスタイプ
 */
public class QueryMeta {

    /**
     * 抽出対象テーブルのエンティティ
     */
    @Getter
    @Setter
    private EntityPath<?> entityPath;

    /**
     * 検索条件
     */
    @Getter
    @Setter
    private Predicate where;

    /**
     * 取得するレコード数の上限値です。
     * <p>負の値の時は無視します。
     */
    @Getter
    @Setter
    private int limit = -1;

    /**
     * 取得するレコード数の開始位置です。
     * <p>負の値の時は無視します。
     */
    @Getter
    @Setter
    private int offset = -1;

    /**
     * ソート順です。
     */
    @Getter
    private List<OrderSpecifier> orders = new ArrayList<>();

    /**
     * 抽出対象のプロパティです。
     */
    @Getter
    private final Set<PropertyPath<?>> includesProperties = new LinkedHashSet<>();

    public void addOrder(OrderSpecifier... orders) {
        this.orders.addAll(Arrays.asList(orders));
    }

    public void addInclude(PropertyPath<?>... properties) {
        this.includesProperties.addAll(Arrays.asList(properties));
    }

}
