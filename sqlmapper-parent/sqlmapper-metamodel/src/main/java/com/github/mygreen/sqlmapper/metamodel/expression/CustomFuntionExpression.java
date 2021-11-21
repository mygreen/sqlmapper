package com.github.mygreen.sqlmapper.metamodel.expression;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mygreen.sqlmapper.metamodel.operation.BooleanOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.LocalDateOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.LocalDateTimeOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.LocalTimeOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.NumberOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.SqlDateOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.SqlTimeOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.SqlTimestampOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.StringOperation;
import com.github.mygreen.sqlmapper.metamodel.operation.UtilDateOperation;
import com.github.mygreen.sqlmapper.metamodel.operator.FunctionOp;

import lombok.Getter;


/**
 * 任意の関数式を表現します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public abstract class CustomFuntionExpression<T> implements Expression<T> {

    protected final Expression<?> mixin;

    /**
     * 関数のクエリ
     */
    @Getter
    protected final String query;

    /**
     * 関数の引数
     */
    @Getter
    protected final List<Expression<?>> args;

    public CustomFuntionExpression(Expression<?> mixin, String query, Object... args) {
        if(query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query should be not empty.");
        }

        /*
         * クエリ中のプレースホルダーと引数の個数を比較する。
         * プレースホルダーをカウントするときには、文字列句('abc')などを考慮する必要があるが、
         * そこまで複雑なことはしないと思うので実施しない。
         */
        int countPlaceHolder = countPlaceHolder(query);
        int sizeArgs = (args == null ? 0 : args.length);
        if(countPlaceHolder != sizeArgs) {
            throw new IllegalArgumentException(String.format("'%s' is not match place holder count '%d'. ", query, countPlaceHolder));
        }

        this.mixin = mixin;
        this.query = query;
        if(sizeArgs == 0) {
            this.args = Collections.emptyList();
        } else {
            this.args = Arrays.stream(args)
                    .map(this::convertExpression)
                    .collect(Collectors.toList());
        }

    }

    /**
     * 文字列中のプレースホルダーの出現回数をカウントします。
     * @param text
     * @return 出現回数
     */
    private int countPlaceHolder(final String text) {

        int count = 0;
        int length = text.length();

        for(int i=0; i < length; i++) {
            char c = text.charAt(i);
            if(c == '?') {
                count++;
            }
        }

        return count;

    }

    /**
     * 値を式オブジェクトに変換する。
     * @param value 値
     * @return 変換した値
     */
    @SuppressWarnings("rawtypes")
    private Expression<?> convertExpression(Object value) {
        if(value instanceof Expression) {
            return (Expression)value;
        } else if(value instanceof Collection) {
            return Constant.createCollection((Collection)value);
        } else {
            return Constant.create(value);
        }
    }

    /**
     * 関数の戻り値の型を返します。
     * @return ブーリアン型を返します。
     */
    public BooleanExpression returnBoolean() {
        return new BooleanOperation(FunctionOp.CUSTOM, mixin, this);
    }

    /**
     * 関数の戻り値の型を返します。
     * @return 文字列型を返します。
     */
    public StringExpression returnString() {
        return new StringOperation(FunctionOp.CUSTOM, mixin, this);
    }

    /**
     * 関数の戻り値の型を返します。
     * @param type 数値の具象クラスを指定します。
     * @return 数値型を返します。
     */
    public <R extends Number & Comparable<R>> NumberExpression<R> returnNumber(Class<R> type) {
        return new NumberOperation<R>(type, FunctionOp.CUSTOM, mixin, this);
    }

    /**
     * 関数の戻り値の型を返します。
     * @return {@link LocalDate}型を返します。
     */
    public LocalDateExpression returnLocalDate() {
        return new LocalDateOperation(FunctionOp.CUSTOM, mixin, this);
    }

    /**
     * 関数の戻り値の型を返します。
     * @return {@link LocalTime}型を返します。
     */
    public LocalTimeExpression returnLocalTime() {
        return new LocalTimeOperation(FunctionOp.CUSTOM, mixin, this);
    }

    /**
     * 関数の戻り値の型を返します。
     * @return {@link LocalDateTime}型を返します。
     */
    public LocalDateTimeExpression returnLocalDateTime() {
        return new LocalDateTimeOperation(FunctionOp.CUSTOM, mixin, this);
    }

    /**
     * 関数の戻り値の型を返します。
     * @return {@link Date}型を返します。
     */
    public SqlDateExpression returnSqlDate() {
        return new SqlDateOperation(FunctionOp.CUSTOM, mixin, this);
    }

    /**
     * 関数の戻り値の型を返します。
     * @return {@link Time}型を返します。
     */
    public SqlTimeExpression returnSqlTime() {
        return new SqlTimeOperation(FunctionOp.CUSTOM, mixin, this);
    }

    /**
     * 関数の戻り値の型を返します。
     * @return {@link Timestamp}型を返します。
     */
    public SqlTimestampExpression returnSqlTimeStamp() {
        return new SqlTimestampOperation(FunctionOp.CUSTOM, mixin, this);
    }

    /**
     * 関数の戻り値の型を返します。
     * @return {@link java.util.Date}型を返します。
     */
    public UtilDateExpression returnUtilDate() {
        return new UtilDateOperation(FunctionOp.CUSTOM, mixin, this);
    }

//    /**
//     * 関数の戻り値の型を返します。
//     * @return 列挙型を返します。
//     */
//    public <R extends Enum<R>> EnumExpression<R> returnEnum(Class<R> type) {
//        return new EnumOperation<R>(type, FunctionOp.CUSTOM, mixin, this);
//    }

}
