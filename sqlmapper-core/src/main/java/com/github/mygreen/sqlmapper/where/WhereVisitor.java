package com.github.mygreen.sqlmapper.where;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.query.IllegalQueryException;
import com.github.mygreen.sqlmapper.type.ValueType;

import lombok.RequiredArgsConstructor;

/**
 * {@link SimpleWhere} で構築した条件分を走査しSQLを組み立てるVisitor。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class WhereVisitor {

    /**
     * 検索対象となるテーブルのエンティティ情報
     */
    private final EntityMeta entityMeta;

    /**
     * SQLを組み立てる際のパラメータ情報
     */
    private final WhereVisitorParamContext paramContext;

    /**
     * 組み立てたクライテリア
     */
    private StringBuilder criteria = new StringBuilder();

    /**
     * 組み立てたクライテリアを取得します。
     * @return クライテリア
     */
    public String getCriteria() {
        return criteria.toString();
    }

    /**
     * 変数名を払い出す。
     * @return 変数名
     */
    private String createArgName() {
        return "_arg" + paramContext.nextArgIndex();
    }

    /**
     * 指定した件数の変数名を払い出す。
     * @param count 払い出す件数。
     * @return 変数名
     */
    private String[] createArgNames(int count) {

        String[] names = new String[count];
        for(int i=0; i < count; i++) {
            names[i] = createArgName();
        }
        return names;
    }

    /**
     * {@link Where} を処理します。
     * @param where 条件式
     * @throws IllegalArgumentException 不明な{@link Where}な場合にスローします。
     */
    public void visit(final Where where) {
        if(where instanceof SimpleWhere) {
            visit((SimpleWhere) where);
        } else if(where instanceof WhereBuilder) {
            visit((WhereBuilder) where);
        } else {
            throw new IllegalArgumentException("unknown where class : " + where.getClass().getName());
        }
    }

    /**
     * {@link SimpleWhere} を処理します。
     * <p>条件式を {@literal AND}でつなげます。</p>
     * @param where 条件式
     */
    public void visit(final SimpleWhere where) {

        for(ValueOperator operator : where.getOperators()) {
            if(criteria.length() > 0) {
                criteria.append(" AND ");
            }

            operator.accept(this);
        }

    }

    /**
     * {@link WhereBuilder} を処理します。
     * <p>条件式を {@literal OR}でつなげます。</p>
     * @param where 条件式
     */
    public void visit(final WhereBuilder where) {

        List<String> result = new ArrayList<>();

        // 演算子の式がまだ持っているときは、子要素に追加する。
        where.or();

        // 子要素の式を組み立てる。
        if(!where.getChildrenWhere().isEmpty()) {
            for(Where subWhere : where.getChildrenWhere()) {
                WhereVisitor subVisitor = new WhereVisitor(entityMeta, paramContext);
                subVisitor.visit(subWhere);

                String sql = subVisitor.getCriteria();
                if(!sql.isEmpty()) {
                    result.add(sql);
                }
            }
        }

        if(result.size() == 1) {
            // 1つしかない場合は、そのまま設定する。
            criteria.append(result.get(0));
        } else {
            for(String sql : result) {
                if(criteria.length() > 0) {
                    criteria.append(" OR ");
                }
                criteria.append("(").append(sql).append(")");
            }
        }

    }

    /**
     * {@link WhereValueOperator} を処理します。
     * <p>条件式を括弧で囲みます。{@literal (A AND B)} </p>
     * @param valueOperator 条件式
     */
    public void visit(WhereValueOperator valueOperator) {

        Where subWhere = valueOperator.getWhere();

        WhereVisitor subVisitor = new WhereVisitor(entityMeta, paramContext);
        subVisitor.visit(subWhere);

        criteria.append("(")
            .append(subVisitor.getCriteria())
            .append(")");

    }

    /**
     * {@link SingleValueOperator} を処理します。
     * @param valueOperator 演算式
     * @throws IllegalQueryException エンティティ情報の中に含まれないプロパティ名を指定した場合にスローされます。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void visit(final SingleValueOperator valueOperator) {

        String propertyName = valueOperator.getPropertyName();
        Optional<PropertyMeta> propertyMeta = entityMeta.getPropertyMeta(propertyName);
        if(propertyMeta.isEmpty()) {
            throw new IllegalQueryException("unknwon property : " + propertyName);
        }

        String argName = createArgName();
        ValueType valueType = propertyMeta.get().getValueType();
        valueType.bindValue(valueOperator.getValue(), paramContext.getParamSource(), argName);

        String columnName = propertyMeta.get().getColumnMeta().getName();
        String sql = valueOperator.getSql(columnName, ":" + argName);
        criteria.append(sql);

    }

    /**
     * {@link EmptyValueOperator} を処理します。
     * @param valueOperator 演算式
     * @throws IllegalQueryException エンティティ情報の中に含まれないプロパティ名を指定した場合にスローされます。
     */
    public void visit(final EmptyValueOperator valueOperator) {

        String propertyName = valueOperator.getPropertyName();
        Optional<PropertyMeta> propertyMeta = entityMeta.getPropertyMeta(propertyName);
        if(propertyMeta.isEmpty()) {
            throw new IllegalQueryException("unknwon property : " + propertyName);
        }

        String columnName = propertyMeta.get().getColumnMeta().getName();
        String sql = valueOperator.getSql(columnName);
        criteria.append(sql);

    }

    /**
     * {@link MultiValueOperator} を処理します。
     * @param valueOperator 演算式
     * @throws IllegalQueryException エンティティ情報の中に含まれないプロパティ名を指定した場合にスローされます。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void visit(final MultiValueOperator valueOperator) {

        String propertyName = valueOperator.getPropertyName();
        Optional<PropertyMeta> propertyMeta = entityMeta.getPropertyMeta(propertyName);
        if(propertyMeta.isEmpty()) {
            throw new IllegalQueryException("unknwon property : " + propertyName);
        }

        String[] argNames = createArgNames(valueOperator.getValueSize());
        ValueType valueType = propertyMeta.get().getValueType();
        for(int i=0; i < valueOperator.getValueSize(); i++) {
            valueType.bindValue(valueOperator.getValue(i), paramContext.getParamSource(), argNames[i]);
        }

        String[] varNames = Arrays.stream(argNames)
            .map(n -> ":" + n)
            .toArray(String[]::new);

        String columnName = propertyMeta.get().getColumnMeta().getName();
        String sql = valueOperator.getSql(columnName, varNames);
        criteria.append(sql);

    }
}
