package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.StoredName;
import com.github.mygreen.sqlmapper.core.meta.StoredParamMeta;
import com.github.mygreen.sqlmapper.core.query.JdbcTemplateBuilder;

import lombok.Getter;

/**
 * ストアドプロシージャを呼び出すためのSQLを自動生成する処理の実装です。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class AutoProcedureCallImpl extends AutoStoredExecutorSupport implements AutoProcedureCall {

    /**
     * 呼び出すストアドプロシージャ名
     */
    @Getter
    private final StoredName procedureName;

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

    @Getter
    private Integer queryTimeout;

    /**
     * パラメータなしのコンストラクタ
     *
     * @param context
     * @param procedureName
     */
    public AutoProcedureCallImpl(SqlMapperContext context, final StoredName procedureName) {
        super(context);
        this.procedureName = procedureName;
        this.parameter = Optional.empty();
        this.paramMeta = null;
    }

    public AutoProcedureCallImpl(SqlMapperContext context, final StoredName procedureName, final Object parameter) {
        super(context);
        this.procedureName = procedureName;
        this.parameter = Optional.of(parameter);
        this.paramMeta = context.getStoredParamMetaFactory().create(parameter.getClass());
    }

    @Override
    public AutoProcedureCallImpl queryTimeout(int seconds) {
        this.queryTimeout = seconds;
        return this;
    }

    @Override
    public void execute() {
        final SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate())
                .withProcedureName(procedureName.getName());

        if(procedureName.getCatalog() != null) {
            jdbcCall.withCatalogName(procedureName.getCatalog());
        }

        if(procedureName.getSchema() != null) {
            jdbcCall.withSchemaName(procedureName.getSchema());
        }

        if(parameter.isEmpty()) {
            context.getSqlLogger().outCall(procedureName.toFullName(), null);
            jdbcCall.execute();

        } else {
            SqlParameter[] parameterTypes = createSqlParameterTypes(paramMeta);
            Object[] parameterValues = parameter.map(p -> createParameterValues(paramMeta, p))
                    .orElseGet(() -> new Object[0]);

            context.getSqlLogger().outCall(procedureName.toFullName(), parameterValues);

            Map<String, Object> out = jdbcCall.declareParameters(parameterTypes)
                    .execute(parameterValues);

            if(containsResultParam(paramMeta, parameter)) {
                // 戻り値を持つパラメータの処理を行う。
                doResultValue(paramMeta, parameter.get(), out);
            }
        }

    }

    /**
     * {@link JdbcTemplate}を取得します。
     * @return {@link JdbcTemplate}のインスタンス。
     */
    private JdbcTemplate getJdbcTemplate() {
        return JdbcTemplateBuilder.create(context.getDataSource(), context.getJdbcTemplateProperties())
                .queryTimeout(queryTimeout)
                .build();
    }
}
