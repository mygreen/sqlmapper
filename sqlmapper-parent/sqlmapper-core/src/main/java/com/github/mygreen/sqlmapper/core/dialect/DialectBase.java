package com.github.mygreen.sqlmapper.core.dialect;

import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.query.SelectForUpdateType;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.where.metamodel.OperationHandler;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.Getter;

/**
 * {@link Dialect}のベースとなるクラス。
 * 多くのDBに共通する設定はこのクラスで実装し、異なる部分を継承先で実装します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class DialectBase implements Dialect {

    /**
     * メタモデルによる各演算子の処理のマップ。
     */
    @Getter
    protected Map<Class<?>, OperationHandler<? extends Operator>> operationHandlerMap = new HashMap<>();

    /**
     * {@inheritDoc}
     *
     * @return {@link GenerationType#TABLE} を返します。
     */
    @Override
    public GenerationType getDefaultGenerationType() {
        return GenerationType.TABLE;
    }

    @Override
    public ValueType<?> getValueType(@Nullable ValueType<?> valueType) {
        return valueType;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@literal "count(*)"} を返します。
     */
    @Override
    public String getCountSql() {
        return "count(*)";
    }

    /**
     * {@inheritDoc}
     *
     * @return {@literal "select count(*) from (<sql>)"} を返します。
     */
    public String convertGetCountSql(final String sql) {
        Assert.hasLength(sql, "sql should be not empty.");
        return "select count(*) from ( " + sql + " )";
    }

    /**
     * {@inheritDoc}
     *
     * @return 空文字({@literal ""})を返します。
     */
    @Override
    public String getHintComment(final String hint) {
        return "";
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link SelectForUpdateType#NORMAL} を返します。
     */
    @Override
    public boolean supportsSelectForUpdate(final SelectForUpdateType type) {
        return type == SelectForUpdateType.NORMAL;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@literal "for update"} を返します。
     */
    @Override
    public String getForUpdateSql(final SelectForUpdateType type, final int waitSeconds) {
        return " for update";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertLimitSql(String sql, int offset, int limit) {

        if(offset < 0 && limit < 0) {
            throw new IllegalArgumentException("Either offset or limit should be greather than 0.");
        }

        StringBuilder buf = new StringBuilder(sql.length() + 20);
        buf.append(sql);
        if (limit >= 0) {
            buf.append(" limit ")
                .append(limit);
        }

        if (offset >= 0) {
            buf.append(" offset ")
                .append(offset);
        }

        return buf.toString();

    }

    /**
     * {@inheritDoc}
     *
     * @return {@literal false} を返します。
     */
    @Override
    public boolean needsParameterForResultSet() {
        return false;
    }

    /**
     * メタモデルに対する演算子に対する処理を登録します。
     * <p>登録する際に、{@literal OperationHandler#init()}を実行します。
     *
     * @since 0.3
     * @param <T> 演算子の種別
     * @param operatorClass 演算子種別のクラス
     * @param handler 演算子に対する処理
     */
    public <T extends Operator> void register(Class<T> operatorClass,  OperationHandler<T> handler) {
        this.operationHandlerMap.put(operatorClass, handler);
    }

}
