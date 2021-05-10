package com.github.mygreen.sqlmapper.core.query.sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.github.mygreen.splate.ProcessResult;
import com.github.mygreen.sqlmapper.core.SqlMapperContext;
import com.github.mygreen.sqlmapper.core.mapper.EntityMappingCallback;
import com.github.mygreen.sqlmapper.core.mapper.SqlEntityRowMapper;


/**
 * SQLテンプレートによる抽出を行うクエリを実行します。
 * {@link SqlSelectImpl}のクエリ実行処理の移譲先です。
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象のエンティティの型
 */
public class SqlSelectExecutor<T> {

    /**
     * クエリ情報
     */
    private final SqlSelectImpl<T> query;

    /**
     * 設定情報
     */
    private final SqlMapperContext context;

    /**
     * 実行するSQLです
     */
    private String executedSql;

    /**
     * クエリのパラメータ
     */
    private Object[] paramValues;

    /**
     * 組み立てたクエリ情報を指定するコンストラクタ。
     * @param query クエリ情報
     */
    public SqlSelectExecutor(SqlSelectImpl<T> query) {
        this.query = query;
        this.context = query.getContext();
    }

    /**
     * クエリ実行の準備を行います。
     */
    private void prepare() {
        prepareSql();
    }

    /**
     * 実行するSQLを準備します。
     */
    private void prepareSql() {

        final ProcessResult result = query.getTemplate().process(query.getParameter());
        this.executedSql = result.getSql();
        this.paramValues = result.getParameters().toArray();

    }

    /**
     * 1件だけヒットすることを前提として検索クエリを実行します。
     *
     * @param callback エンティティマッピング後のコールバック処理
     * @return エンティティのベースオブジェクト。
     * @throws IncorrectResultSizeDataAccessException 1件も見つからない場合、2件以上見つかった場合にスローされます。
     */
    public T getSingleResult(EntityMappingCallback<T> callback) {
        prepare();

        SqlEntityRowMapper<T> rowMapper = new SqlEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().queryForObject(executedSql, rowMapper, paramValues);
    }

    /**
     * 1件だけヒットすることを前提として検索クエリを実行します。
     *
     * @param callback エンティティマッピング後のコールバック処理。
     * @return エンティティのベースオブジェクト。1件も対象がないときは空を返します。
     */
    public Optional<T> getOptionalResult(EntityMappingCallback<T> callback) {
        prepare();

        SqlEntityRowMapper<T> rowMapper = new SqlEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        final List<T> ret = context.getJdbcTemplate().query(executedSql, rowMapper, paramValues);
        if(ret.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(ret.get(0));
        }
    }

    /**
     * 検索クエリを実行します。
     *
     * @param callback エンティティマッピング後のコールバック処理。
     * @return 検索してヒットした複数のベースオブジェクト。1件も対象がないときは空のリストを返します。
     */
    public List<T> getResultList(EntityMappingCallback<T> callback) {
        prepare();

        SqlEntityRowMapper<T> rowMapper = new SqlEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().query(executedSql, rowMapper, paramValues);
    }

    /**
     * 結果を {@link Stream} で返す検索クエリを実行します。
     * @param callback エンティティマッピング後のコールバック処理。
     * @return 問い合わせの結果
     */
    public Stream<T> getResultStream(EntityMappingCallback<T> callback) {
        prepare();

        SqlEntityRowMapper<T> rowMapper = new SqlEntityRowMapper<T>(query.getEntityMeta(), Optional.ofNullable(callback));
        return context.getJdbcTemplate().queryForStream(executedSql, rowMapper, paramValues);
    }

}
