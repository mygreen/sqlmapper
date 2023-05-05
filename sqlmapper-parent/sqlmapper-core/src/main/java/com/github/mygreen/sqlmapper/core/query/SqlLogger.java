package com.github.mygreen.sqlmapper.core.query;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.event.Level;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.util.CollectionUtils;

import com.github.mygreen.sqlmapper.core.config.ShowSqlProperties;
import com.github.mygreen.sqlmapper.core.util.QueryUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SQLをログ出力する処理。
 *
 * @since 0.4
 * @author T.TSUCHIE
 *
 */
@Slf4j
@RequiredArgsConstructor
public class SqlLogger {

    /**
     * SQLのログ出力設定
     * @return SQLのログ出力設定を取得します。
     */
    @Getter
    private final ShowSqlProperties prop;

    private static final SimpleDateFormat FORMAT_SQL_TIME = new SimpleDateFormat("HH:mm:ss");

    private static final SimpleDateFormat FORMAT_SQL_DATE = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat FORMAT_SQL_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final SimpleDateFormat FORMAT_UTIL_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * SQLをログ出力する。
     * @param sql 出力対象のSQL
     * @param params SQLのバインド変数
     */
    public void out(final String sql, final Object[] params) {
        outSql(sql);
        if(prop.getBindParam().isEnabled() && !QueryUtils.isEmpty(params)) {
            outParams(Arrays.asList(params));
        }

    }

    /**
     * SQLをログ出力する。
     * @param sql 出力対象のSQL
     * @param params SQLのバインド変数
     */
    public void out(final String sql, final Collection<Object> params) {
        outSql(sql);
        outParams(params);
    }

    /**
     * バッチ実行用のSQLをログ出力する。
     * @param sql 出力対象のSQL
     * @param batchParams バッチ変数用SQLのバインド変数
     */
    public void outBatch(final String sql, final List<Object[]> batchParams) {
        outSql(sql);
        outBatchParams(batchParams);

    }

    /**
     * ストアドプロシージャ／ストアドファンクションのログを出力する。
     * @param callName ストアドプロシージャ／ストアドファンクションのSQL
     * @param callParams ストアドプロシージャ／ストアドファンクションのバインド変数
     */
    public void outCall(final String callName, final Object[] callParams) {
        outCallName(callName, callParams);
        outCallParams(callParams);
    }

    /**
     * SQLをログ出力する。
     * @param sql 出力対象のSQL
     */
    private void outSql(final String sql) {
        if(!prop.isEnabled()) {
            return;
        }
        invokeLog(prop.getLogLevel(), "sql statement : {}", new Object[]{sql});
    }

    /**
     * SQLのバインド変数をログ出力する。
     * @param params SQLのバインド変数
     */
    private void outParams(final Collection<Object> params) {
        if(!prop.isEnabled() || !prop.getBindParam().isEnabled() || CollectionUtils.isEmpty(params)) {
            return;
        }

        int paramCount = 1;
        for(Object param : params) {
            String paramType = resolveBindParamType(param);
            String paramValue = resolveBindParamValue(param);

            invokeLog(prop.getBindParam().getLogLevel(), "sql binding parameter : [{}] as [{}] - [{}]",
                    new Object[]{paramCount, paramType, paramValue });

            paramCount++;
        }
    }

    /**
     * バッチ実行用のSQLのバインド変数をログ出力する。
     * @param batchParams SQLのバインド変数
     */
    private void outBatchParams(final Collection<Object[]> batchParams) {
        if(!prop.isEnabled() || !prop.getBindParam().isEnabled() || CollectionUtils.isEmpty(batchParams)) {
            return;
        }

        int recordCount = 1;
        for(Object[] recordParams : batchParams) {
            int paramCount = 1;
            for(Object param : recordParams) {
                String paramType = resolveBindParamType(param);
                String paramValue = resolveBindParamValue(param);

                invokeLog(prop.getBindParam().getLogLevel(), "sql batch binding parameter : [{}][{}] as [{}] - [{}]",
                        new Object[]{recordCount, paramCount, paramType, paramValue });

                paramCount++;
            }
            recordCount++;
        }
    }

    /**
     * ストアドプロシージャ/ストアドファンクションの名称をログ出力する。
     * @param callName 名称
     * @param callParams パラメータ
     */
    private void outCallName(final String callName, final Object[] callParams) {
        if(!prop.isEnabled()) {
            return;
        }

        int paramCount = (callParams == null) ? 0 : callParams.length;
        String args = QueryUtils.repeat("?", ", ", paramCount);
        invokeLog(prop.getBindParam().getLogLevel(), "sql call : {}({})", new Object[]{callName, args});
    }

    /**
     * ストアドプロシージャ/ストアドファンクションのパラメータをログ出力する。
     * @param callParams パラメータ
     */
    private void outCallParams(final Object[] callParams) {
        if(!prop.isEnabled() || !prop.getBindParam().isEnabled() || QueryUtils.isEmpty(callParams)) {
            return;
        }

        int paramCount = 1;
        for(Object param : callParams) {
            String paramType = resolveBindParamType(param);
            String paramValue = resolveBindParamValue(param);

            invokeLog(prop.getBindParam().getLogLevel(), "sql call binding parameter : [{}] as [{}] - [{}]",
                    new Object[]{paramCount, paramType, paramValue });

            paramCount++;
        }
    }



    /**
     * ログレベルを指定してログに出力する。
     * @param level ログレベル。
     * @param message ログメッセージ。
     * @param args ログメッセージ中の引数。
     */
    private void invokeLog(Level level, String message, Object[] args) {
        switch (level) {
            case TRACE:
                log.trace(message, args);
                break;
            case DEBUG:
                log.debug(message, args);
                break;
            case INFO:
                log.info(message, args);
                break;
            case WARN:
                log.warn(message, args);
                break;
            case ERROR:
                log.error(message, args);
                break;
        }
    }

    /**
     * SQLのバンド変数のログ出力用のタイプ名に変換する。
     * @param value バンド変数
     * @return タイプ情報。
     */
    private String resolveBindParamType(final Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof SqlParameterValue) {
            return ((SqlParameterValue)(value)).getTypeName();
        } else {
            int type = StatementCreatorUtils.javaTypeToSqlParameterType(value.getClass());
            switch (type) {
                case Types.BOOLEAN:
                    return "BOOLEAN";
                case Types.TINYINT:
                    return "TINYINT";
                case Types.SMALLINT:
                    return "SMALLINT";
                case Types.INTEGER:
                    return "INTEGER";
                case Types.BIGINT:
                    return "BIGINT";
                case Types.FLOAT:
                    return "FLOAT";
                case Types.DOUBLE:
                    return "DOUBLE";
                case Types.DECIMAL:
                    return "DECIMAL";
                case Types.NUMERIC:
                    return "NUMERIC";
                case Types.BLOB:
                    return "BLOB";
                case Types.CLOB:
                    return "CLOB";
                case Types.CHAR:
                    return "CHAR";
                case Types.VARCHAR:
                    return "VARCHAR";
                case Types.DATE:
                    return "DATE";
                case Types.TIMESTAMP:
                    return "TIMESTAMP";
                default:
                    return "UNKNOWN";
            }

        }
    }

    /**
     * SQLのバインド変数をログ出力用の値に変換する。
     * @param value バインド変数。
     * @return ログ出力用の値。
     */
    private String resolveBindParamValue(final Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof Blob) {
            return "<BLOB DATA...>";
        } else if(value instanceof Clob) {
            return "<CLOB DATA...>";
        } else if(value instanceof Time) {
            return FORMAT_SQL_TIME.format(value);
        } else if(value instanceof Date) {
            return FORMAT_SQL_DATE.format(value);
        } else if(value instanceof Timestamp) {
            return FORMAT_SQL_TIMESTAMP.format(value);
        } else if(value instanceof java.util.Date) {
            return FORMAT_UTIL_DATE.format(value);
        } else if (value instanceof SqlParameterValue) {
            return ((SqlParameterValue)(value)).getValue().toString();
        } else {
            return value.toString();
        }
    }


}
