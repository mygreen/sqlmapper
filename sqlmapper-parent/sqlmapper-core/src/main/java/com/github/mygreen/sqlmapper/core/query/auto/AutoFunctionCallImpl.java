package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.StoredName;
import com.github.mygreen.sqlmapper.core.meta.StoredParamMeta;

import lombok.Getter;

/**
 * ストアドファンクションを呼び出すためのSQLを自動生成する処理の実装です。
 * <p>戻り値がRETURN文の場合のみ対応し、OUTパラメータ、RESULT_SETの場合は、{@link AutoProcedureCallImpl}を使用します。
 *
 * @author T.TSUCHIE
 *
 * @param <T> ストアドファンクションの戻り値のタイプ
 */
public class AutoFunctionCallImpl<T> extends AutoStoredExecutorSupport implements AutoFunctionCall<T> {

    /**
     * 戻り値のクラス
     */
    private final Class<T> resultClass;

    /**
     * 呼び出すストアドファンクション名
     */
    @Getter
    private final StoredName functionName;

    /**
     * パラメータ
     */
    @Getter
    private final Optional<Object> parameter;

    /**
     * パラメータのメタ情報。
     * パラメータが空の時は{@literal null}を設定。
     */
    private final StoredParamMeta paramMeta;

    public AutoFunctionCallImpl(SqlMapperContext context, final Class<T> resultClass, final StoredName functionName) {
        super(context);
        this.resultClass = resultClass;
        this.functionName = functionName;
        this.parameter = Optional.empty();
        this.paramMeta = null;
    }

    public AutoFunctionCallImpl(SqlMapperContext context,  final Class<T> resultClass, final StoredName functionName, final Object parameter) {
        super(context);
        this.resultClass = resultClass;
        this.functionName = functionName;
        this.parameter = Optional.of(parameter);
        this.paramMeta = context.getStoredParamMetaFactory().create(parameter.getClass());
    }

    @Override
    public T execute() {

        final SimpleJdbcCall jdbcCall = new SimpleJdbcCall(context.getJdbcTemplate())
                .withFunctionName(functionName.getName());

        if(functionName.getCatalog() != null) {
            jdbcCall.withCatalogName(functionName.getCatalog());
        }

        if(functionName.getSchema() != null) {
            jdbcCall.withSchemaName(functionName.getSchema());
        }


        if(parameter.isEmpty()) {
            return jdbcCall.executeFunction(resultClass);

        } else {
            SqlParameter[] parameterTypes = createSqlParameterTypes(paramMeta);
            Object[] parameterValues = parameter.map(p -> createParameterValues(paramMeta, p))
                    .orElseGet(() -> new Object[0]);

            if(containsResultParam(paramMeta, parameter)) {
                Map<String, Object> out = jdbcCall.declareParameters(parameterTypes)
                        .execute(parameterValues);
                // 戻り値を持つパラメータの処理を行う。
                doResultValue(paramMeta, parameter.get(), out);
                return null;


            } else {
                // SimpleJdbcCallは、OUTパラメータと戻り値（ResultSet）は併用できない。
                return jdbcCall.declareParameters(parameterTypes)
                        .executeFunction(resultClass, parameterValues);
            }
        }


    }


}
