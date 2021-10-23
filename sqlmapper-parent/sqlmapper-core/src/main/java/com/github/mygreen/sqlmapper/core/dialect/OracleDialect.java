package com.github.mygreen.sqlmapper.core.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.lang.Nullable;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.query.SelectForUpdateType;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.type.standard.BooleanType;
import com.github.mygreen.sqlmapper.core.type.standard.NumberableBooleanType;

/**
 * Oracle v12+用の方言の定義。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class OracleDialect extends DialectBase {

    /**
     * DB側が整数型のとき、Javaのboolean型にマッピングします。
     */
    protected final NumberableBooleanType primitiveBooleanType = new NumberableBooleanType(true);

    /**
     * DB側が整数型のとき、JavaのラッパーのBoolean型にマッピングします。
     */
    protected final NumberableBooleanType objectiveBooleanType = new NumberableBooleanType(false);

    /**
     * {@inheritDoc}
     *
     * @return {@literal "oracle"}を返します。
     */
    @Override
    public String getName() {
        return "oracle";
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * <ul>
     *  <li>{@link GenerationType#IDENTITY} : {@literal true}</li>
     *  <li>{@link GenerationType#SEQUENCE} : {@literal true}</li>
     *  <li>{@link GenerationType#TABLE} : {@literal true}</li>
     *  <li>{@link GenerationType#UUID} : {@literal true}</li>
     * </ul>
     */
    @Override
    public boolean supportsGenerationType(GenerationType generationType) {
        switch(generationType) {
            case IDENTITY:
                return true;
            case SEQUENCE:
                return true;
            case TABLE:
                return true;
            case UUID:
                return true;
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link OracleSequenceMaxValueIncrementer} のインスタンスを返す。
     */
    @Override
    public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName) {
        return new OracleSequenceMaxValueIncrementer(dataSource, sequenceName);
    }

    /**
     * {@inheritDoc}
     *
     * @return 与えられた値が {@literal boolean/Boolean}のとき、整数型に変換する {@link NumberableBooleanType} に変換します。
     */
    @Override
    public ValueType<?> getValueType(@Nullable ValueType<?> valueType) {
        if(valueType == null) {
            return null;
        }

        if(valueType instanceof BooleanType) {
            if(((BooleanType)valueType).isForPrimitive()) {
                return primitiveBooleanType;
            } else {
                return objectiveBooleanType;
            }
        }
        return valueType;
    }

    /**
     * {@inheritDoc}
     *
     * @return コメントの形式 /{@literal *}+ヒント{@literal *}/ の形式で返します。
     */
    @Override
    public String getHintComment(final String hint) {
        return "/*+ " + hint + " */ ";
    }

    /**
     * {@inheritDoc}
     *
     * @return {@literal OFFSET/FETCH} を使用して、LIMIT句を組み立てます。
     */
    @Override
    public String convertLimitSql(String sql, int offset, int limit) {

        StringBuilder buf = new StringBuilder(sql.length() + 20);
        buf.append(sql);
        if (offset > 0) {
            buf.append(" offset ")
                .append(offset)
                .append(" fetch first ")
                .append(limit)
                .append(" rows only");
        } else {
            buf.append(" fetch first ")
                .append(limit)
                .append(" rows only");
        }

        return buf.toString();

    }

    /**
     * {@inheritDoc}
     *
     * @return 必ず{@literal true} を返します。
     */
    @Override
    public boolean supportsSelectForUpdate(final SelectForUpdateType type) {
        // 全てのタイプをサポートする
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * <ul>
     *   <li>{@link SelectForUpdateType#NORMAL} : {@literal for update}</li>
     *   <li>{@link SelectForUpdateType#NOWAIT} : {@literal for update nowait}</li>
     *   <li>{@link SelectForUpdateType#WAIT} : {@literal for update wait <waitSeconds>}</li>
     * </ul>
     */
    @Override
    public String getForUpdateSql(final SelectForUpdateType type, final int waitSeconds) {

        StringBuilder buf = new StringBuilder(20)
                .append(" for update");

        switch(type) {
            case NORMAL:
                break;
            case NOWAIT:
                buf.append(" nowait");
                break;
            case WAIT:
                buf.append(" wait ").append(waitSeconds);
                break;
        }

        return buf.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@literal true} を返します。
     */
    @Override
    public boolean needsParameterForResultSet() {
        return true;
    }
}
