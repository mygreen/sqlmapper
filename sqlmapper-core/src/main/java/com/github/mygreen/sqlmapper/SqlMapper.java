package com.github.mygreen.sqlmapper;

import java.util.List;

import com.github.mygreen.sqlmapper.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.query.auto.AutoAnyDelete;
import com.github.mygreen.sqlmapper.query.auto.AutoBatchInsert;
import com.github.mygreen.sqlmapper.query.auto.AutoBatchUpdate;
import com.github.mygreen.sqlmapper.query.auto.AutoDelete;
import com.github.mygreen.sqlmapper.query.auto.AutoInsert;
import com.github.mygreen.sqlmapper.query.auto.AutoSelect;
import com.github.mygreen.sqlmapper.query.auto.AutoUpdate;
import com.github.mygreen.sqlmapper.query.sql.SqlBatchUpdate;
import com.github.mygreen.sqlmapper.query.sql.SqlCount;
import com.github.mygreen.sqlmapper.query.sql.SqlSelect;
import com.github.mygreen.sqlmapper.query.sql.SqlUpdate;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * JDBCによるSQLの実行を管理するクラスです。
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class SqlMapper {

    @Getter
    private final SqlMapperContext context;

    /**
     * テーブルを参照します。
     * @param <T> エンティティタイプ
     * @param baseClass エンティティのクラス
     * @return 参照用のクエリ
     */
    public <T> AutoSelect<T> selectFrom(@NonNull Class<T> baseClass) {
        return new AutoSelect<T>(context, baseClass);
    }

    /**
     * エンティティを挿入します
     * @param <T> エンティティタイプ
     * @param entity エンティティのインスタンス
     * @return 挿入用のクエリ
     */
    public <T> AutoInsert<T> insert(@NonNull T entity) {
        return new AutoInsert<T>(context, entity);
    }

    /**
     * エンティティを削除します。
     * @param <T> エンティティタイプ
     * @param entity エンティティのインスタンス
     * @return 削除用のクエリ
     */
    public <T> AutoDelete<T> delete(@NonNull T entity){
        return new AutoDelete<T>(context, entity);
    }

    /**
     * エンティティを更新します。
     * @param <T> エンティティタイプ
     * @param entity エンティティのインスタンス
     * @return 更新用のクエリ
     */
    public <T> AutoUpdate<T> update(@NonNull T entity) {
        return new AutoUpdate<T>(context, entity);
    }

    /**
     * 任意の条件に対してテーブルのレコードを削除します。
     * @param <T> エンティティタイプ
     * @param baseClass エンティティのクラス
     * @return 削除用のクエリ
     */
    public <T> AutoAnyDelete<T> deleteFrom(@NonNull Class<T> baseClass) {
        return new AutoAnyDelete<T>(context, baseClass);
    }

    /**
     * 複数のエンティティを挿入します。
     * @param <T> エンティティタイプ
     * @param entities エンティティの並び
     * @return 挿入用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    @SuppressWarnings("unchecked")
    public <T> AutoBatchInsert<T> insertBatch(T... entities) {
        return new AutoBatchInsert<T>(context, entities);
    }

    /**
     * 複数のエンティティを挿入します。
     * @param <T> エンティティタイプ
     * @param entities エンティティの並び
     * @return 挿入用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    public <T> AutoBatchInsert<T> insertBatch(List<T> entities) {
        return new AutoBatchInsert<T>(context, entities);
    }

    /**
     * 複数のエンティティを更新します。
     * @param <T> エンティティタイプ
     * @param entities エンティティの並び
     * @return 更新用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    @SuppressWarnings("unchecked")
    public <T> AutoBatchUpdate<T> updateBatch(T... entities) {
        return new AutoBatchUpdate<T>(context, entities);
    }

    /**
     * 複数のエンティティを更新します。
     * @param <T> エンティティタイプ
     * @param entities エンティティの並び
     * @return 更新用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    public <T> AutoBatchUpdate<T> updateBatch(List<T> entities) {
        return new AutoBatchUpdate<T>(context, entities);
    }

    /**
     * 複数のエンティティを削除します。
     * @param <T> エンティティタイプ
     * @param entities エンティティの並び
     * @return 削除用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    @SuppressWarnings("unchecked")
    public <T> AutoBatchUpdate<T> deleteBatch(T... entities) {
        return new AutoBatchUpdate<T>(context, entities);
    }

    /**
     * 複数のエンティティを削除します。
     * @param <T> エンティティタイプ
     * @param entities エンティティの並び
     * @return 削除用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    public <T> AutoBatchUpdate<T> deleteBatch(List<T> entities) {
        return new AutoBatchUpdate<T>(context, entities);
    }

    /**
     * SQLファイルを元にテーブルを参照します。
     * @param <T> エンティティタイプ
     * @param baseClass エンティティのクラス
     * @param path SQLファイルのパス。
     * @return SQLファイル参照用のクエリ
     */
    public <T> SqlSelect<T> selectBySqlFile(@NonNull Class<T> baseClass, @NonNull String path) {
        return new SqlSelect<T>(context, baseClass, context.getSqlLoader().loadSqlFileAsNode(path));
    }

    /**
     * SQLファイルを元にテーブルを参照します。
     * @param <T> エンティティタイプ
     * @param baseClass エンティティのクラス
     * @param path SQLファイルのパス
     * @param parameter パラメータ
     * @return SQLファイル参照用のクエリ
     */
    public <T> SqlSelect<T> selectBySqlFile(@NonNull Class<T> baseClass, @NonNull String path, Object parameter) {
        return new SqlSelect<T>(context, baseClass, context.getSqlLoader().loadSqlFileAsNode(path), parameter);
    }

    /**
     * カウント用のSQLファイルを実行します。
     * @param path SQLファイルのパス
     * @return カウント結果
     */
    public long getCountBySqlFile(@NonNull String path) {
        return new SqlCount<>(context, context.getSqlLoader().loadSqlFileAsNode(path))
                .getCount();
    }

    /**
     * カウント用のSQLファイルを実行します。
     * @param path SQLファイルのパス
     * @param parameter パラメータ
     * @return カウント結果
     */
    public long getCountBySqlFile(@NonNull String path, Object parameter) {
        return new SqlCount<>(context, context.getSqlLoader().loadSqlFileAsNode(path), parameter)
                .getCount();
    }

    /**
     * SQLファイルを元にテーブルを更新（追加/更新/削除）をします。
     * @param path SQLファイルのパス
     * @return SQLファイル更新用のクエリ
     */
    public SqlUpdate<?> updateBySqlFile(@NonNull String path) {
        return new SqlUpdate<>(context, context.getSqlLoader().loadSqlFileAsNode(path));
    }

    /**
     * SQLファイルを元にテーブルを更新（追加/更新/削除）をします。
     * @param path SQLファイルのパス
     * @param parameter パラメータ
     * @return SQLファイル更新用のクエリ
     */
    public SqlUpdate<?> updateBySqlFile(@NonNull String path, Object parameter) {
        return new SqlUpdate<>(context, context.getSqlLoader().loadSqlFileAsNode(path), parameter);
    }

    /**
     * SQLファイルを元にテーブルをバッチ更新（追加/更新/削除）します。
     * @param <T> パラメータのタイプ
     * @param path SQLファイルのパス
     * @param parameters パラメータ
     * @return 更新用のクエリ
     */
    @SuppressWarnings("unchecked")
    public <T> SqlBatchUpdate<T> updateBatchBySqlFile(@NonNull String path, T... parameters) {
        return new SqlBatchUpdate<T>(context, context.getSqlLoader().loadSqlFileAsNode(path), parameters);
    }

    /**
     * SQLファイルを元にテーブルをバッチ更新（追加/更新/削除）します。
     * @param <T> パラメータのタイプ
     * @param path SQLファイルのパス
     * @param parameters パラメータ
     * @return 更新用のクエリ
     */
    public <T> SqlBatchUpdate<T> updateBatchBySqlFile(@NonNull String path, List<T> parameters) {
        return new SqlBatchUpdate<T>(context, context.getSqlLoader().loadSqlFileAsNode(path), parameters);
    }

    /**
     * SQLを元にテーブルを参照します。
     * @param <T> エンティティタイプ
     * @param baseClass エンティティのクラス
     * @param path SQL
     * @return SQL参照用のクエリ
     */
    public <T> SqlSelect<T> selectBySql(@NonNull Class<T> baseClass, @NonNull String sql) {
        return new SqlSelect<T>(context, baseClass, context.getSqlLoader().loadSqlTextAsNode(sql));
    }

    /**
     * SQLを元にテーブルを参照します。
     * @param <T> エンティティタイプ
     * @param baseClass エンティティのクラス
     * @param path SQL
     * @param parameter パラメータ
     * @return SQL参照用のクエリ
     */
    public <T> SqlSelect<T> selectBySql(@NonNull Class<T> baseClass, @NonNull String sql, Object parameter) {
        return new SqlSelect<T>(context, baseClass, context.getSqlLoader().loadSqlTextAsNode(sql), parameter);
    }

    /**
     * カウント用のSQLを実行します。
     * @param path SQL
     * @return カウント結果
     */
    public long getCountBySql(@NonNull String sql) {
        return new SqlCount<>(context, context.getSqlLoader().loadSqlTextAsNode(sql))
                .getCount();
    }

    /**
     * カウント用のSQLを実行します。
     * @param path SQL
     * @param parameter パラメータ
     * @return カウント結果
     */
    public long getCountBySql(@NonNull String sql, Object parameter) {
        return new SqlCount<>(context, context.getSqlLoader().loadSqlTextAsNode(sql), parameter)
                .getCount();
    }

    /**
     * SQLを元にテーブルを更新（追加/更新/削除）をします。
     * @param path SQL
     * @return SQL更新用のクエリ
     */
    public SqlUpdate<?> updateBySql(@NonNull String sql) {
        return new SqlUpdate<>(context, context.getSqlLoader().loadSqlTextAsNode(sql));
    }

    /**
     * SQLを元にテーブルを更新（追加/更新/削除）をします。
     * @param path SQL
     * @param parameter パラメータ
     * @return SQL更新用のクエリ
     */
    public SqlUpdate<?> updateBySql(@NonNull String sql, Object parameter) {
        return new SqlUpdate<>(context, context.getSqlLoader().loadSqlTextAsNode(sql), parameter);
    }

    /**
     * SQLを元にテーブルをバッチ更新（追加/更新/削除）します。
     * @param <T> パラメータのタイプ
     * @param path SQL
     * @param parameters パラメータ
     * @return 更新用のクエリ
     */
    @SuppressWarnings("unchecked")
    public <T> SqlBatchUpdate<T> updateBatchBySql(@NonNull String sql, T... parameters) {
        return new SqlBatchUpdate<T>(context, context.getSqlLoader().loadSqlTextAsNode(sql), parameters);
    }

    /**
     * SQLを元にテーブルをバッチ更新（追加/更新/削除）します。
     * @param <T> パラメータのタイプ
     * @param path SQL
     * @param parameters パラメータ
     * @return 更新用のクエリ
     */
    public <T> SqlBatchUpdate<T> updateBatchBySql(@NonNull String sql, List<T> parameters) {
        return new SqlBatchUpdate<T>(context, context.getSqlLoader().loadSqlTextAsNode(sql), parameters);
    }

}
