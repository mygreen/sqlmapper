package com.github.mygreen.sqlmapper.core;

import java.util.List;

import com.github.mygreen.splate.EmptyValueSqlTemplateContext;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.core.query.auto.AutoAnyDelete;
import com.github.mygreen.sqlmapper.core.query.auto.AutoAnyDeleteImpl;
import com.github.mygreen.sqlmapper.core.query.auto.AutoBatchInsert;
import com.github.mygreen.sqlmapper.core.query.auto.AutoBatchInsertImpl;
import com.github.mygreen.sqlmapper.core.query.auto.AutoBatchUpdate;
import com.github.mygreen.sqlmapper.core.query.auto.AutoBatchUpdateImpl;
import com.github.mygreen.sqlmapper.core.query.auto.AutoDelete;
import com.github.mygreen.sqlmapper.core.query.auto.AutoDeleteImpl;
import com.github.mygreen.sqlmapper.core.query.auto.AutoInsert;
import com.github.mygreen.sqlmapper.core.query.auto.AutoInsertImpl;
import com.github.mygreen.sqlmapper.core.query.auto.AutoSelect;
import com.github.mygreen.sqlmapper.core.query.auto.AutoSelectImpl;
import com.github.mygreen.sqlmapper.core.query.auto.AutoUpdate;
import com.github.mygreen.sqlmapper.core.query.auto.AutoUpdateImpl;
import com.github.mygreen.sqlmapper.core.query.sql.SqlCountImpl;
import com.github.mygreen.sqlmapper.core.query.sql.SqlSelect;
import com.github.mygreen.sqlmapper.core.query.sql.SqlSelectImpl;
import com.github.mygreen.sqlmapper.core.query.sql.SqlUpdate;
import com.github.mygreen.sqlmapper.core.query.sql.SqlUpdateImpl;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * JDBCによるSQL実行を管理するクラスです。
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
     * @param <T> 処理対象となるエンティティの型
     * @param entityPath エンティティの情報
     * @return 参照用のクエリ
     */
    public <T> AutoSelect<T> selectFrom(@NonNull EntityPath<T> entityPath) {
        return new AutoSelectImpl<T>(context, entityPath);
    }

    /**
     * エンティティを挿入します
     * @param <T> 処理対象となるエンティティの型
     * @param entity エンティティのインスタンス
     * @return 挿入用のクエリ
     */
    public <T> AutoInsert<T> insert(@NonNull T entity) {
        return new AutoInsertImpl<T>(context, entity);
    }

    /**
     * エンティティを削除します。
     * @param <T> 処理対象となるエンティティの型
     * @param entity エンティティのインスタンス
     * @return 削除用のクエリ
     */
    public <T> AutoDelete<T> delete(@NonNull T entity){
        return new AutoDeleteImpl<T>(context, entity);
    }

    /**
     * エンティティを更新します。
     * @param <T> 処理対象となるエンティティの型
     * @param entity エンティティのインスタンス
     * @return 更新用のクエリ
     */
    public <T> AutoUpdate<T> update(@NonNull T entity) {
        return new AutoUpdateImpl<T>(context, entity);
    }

    /**
     * 任意の条件に対してテーブルのレコードを削除します。
     * @param <T> 処理対象となるエンティティの型
     * @param entityPath エンティティの情報
     * @return 削除用のクエリ
     */
    public <T> AutoAnyDelete<T> deleteFrom(@NonNull EntityPath<T> entityPath) {
        return new AutoAnyDeleteImpl<T>(context, entityPath);
    }

    /**
     * 複数のエンティティを挿入します。
     * <p>主キーが識別子（IDENTITY）による自動生成の場合は、バッチ実行ではなく1件ずつ処理されるので注意してください。</p>
     *
     * @param <T> 処理対象となるエンティティの型
     * @param entities エンティティの並び
     * @return 挿入用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    @SuppressWarnings("unchecked")
    public <T> AutoBatchInsert<T> insertBatch(T... entities) {
        return new AutoBatchInsertImpl<T>(context, entities);
    }

    /**
     * 複数のエンティティを挿入します。
     * <p>主キーが識別子（IDENTITY）による自動生成の場合は、バッチ実行ではなく1件ずつ処理されるので注意してください。</p>
     *
     * @param <T> 処理対象となるエンティティの型
     * @param entities エンティティの並び
     * @return 挿入用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    public <T> AutoBatchInsert<T> insertBatch(List<T> entities) {
        return new AutoBatchInsertImpl<T>(context, entities);
    }

    /**
     * 複数のエンティティを更新します。
     * @param <T> 処理対象となるエンティティの型
     * @param entities エンティティの並び
     * @return 更新用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    @SuppressWarnings("unchecked")
    public <T> AutoBatchUpdate<T> updateBatch(T... entities) {
        return new AutoBatchUpdateImpl<T>(context, entities);
    }

    /**
     * 複数のエンティティを更新します。
     * @param <T> 処理対象となるエンティティの型
     * @param entities エンティティの並び
     * @return 更新用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    public <T> AutoBatchUpdate<T> updateBatch(List<T> entities) {
        return new AutoBatchUpdateImpl<T>(context, entities);
    }

    /**
     * 複数のエンティティを削除します。
     * @param <T> 処理対象となるエンティティの型
     * @param entities エンティティの並び
     * @return 削除用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    @SuppressWarnings("unchecked")
    public <T> AutoBatchUpdate<T> deleteBatch(T... entities) {
        return new AutoBatchUpdateImpl<T>(context, entities);
    }

    /**
     * 複数のエンティティを削除します。
     * @param <T> 処理対象となるエンティティの型
     * @param entities エンティティの並び
     * @return 削除用のクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    public <T> AutoBatchUpdate<T> deleteBatch(List<T> entities) {
        return new AutoBatchUpdateImpl<T>(context, entities);
    }

    /**
     * SQLファイルを元にテーブルを参照します。
     * @param <T> 処理対象となるエンティティの型
     * @param baseClass エンティティのクラス
     * @param path SQLファイルのパス。
     * @return SQLファイル参照用のクエリ
     */
    public <T> SqlSelect<T> selectBySqlFile(@NonNull Class<T> baseClass, @NonNull String path) {
        return new SqlSelectImpl<T>(context, baseClass, context.getSqlTemplateEngine().getTemplate(path), new EmptyValueSqlTemplateContext());
    }

    /**
     * SQLファイルを元にテーブルを参照します。
     * @param <T> 処理対象となるエンティティの型
     * @param baseClass エンティティのクラス
     * @param path SQLファイルのパス
     * @param parameter SQLテンプレートのパラメータ
     * @return SQLファイル参照用のクエリ
     */
    public <T> SqlSelect<T> selectBySqlFile(@NonNull Class<T> baseClass, @NonNull String path, @NonNull SqlTemplateContext parameter) {
        return new SqlSelectImpl<T>(context, baseClass, context.getSqlTemplateEngine().getTemplate(path), parameter);
    }

    /**
     * カウント用のSQLファイルを実行します。
     * @param path SQLファイルのパス
     * @return カウント結果
     */
    public long getCountBySqlFile(@NonNull String path) {
        return new SqlCountImpl(context, context.getSqlTemplateEngine().getTemplate(path), new EmptyValueSqlTemplateContext())
                .getCount();
    }

    /**
     * カウント用のSQLファイルを実行します。
     * @param path SQLファイルのパス
     * @param parameter パラメータ
     * @return カウント結果
     */
    public long getCountBySqlFile(@NonNull String path, @NonNull SqlTemplateContext parameter) {
        return new SqlCountImpl(context, context.getSqlTemplateEngine().getTemplate(path), parameter)
                .getCount();
    }

    /**
     * SQLファイルを元にテーブルを更新（INSERT / UPDATE/ DELETE）をします。
     * @param path SQLファイルのパス
     * @return SQLファイル更新用のクエリ
     */
    public SqlUpdate updateBySqlFile(@NonNull String path) {
        return new SqlUpdateImpl(context, context.getSqlTemplateEngine().getTemplate(path), new EmptyValueSqlTemplateContext());
    }

    /**
     * SQLファイルを元にテーブルを更新（INSERT / UPDATE/ DELETE）をします。
     * @param path SQLファイルのパス
     * @param parameter パラメータ
     * @return SQLファイル更新用のクエリ
     */
    public SqlUpdate updateBySqlFile(@NonNull String path, @NonNull SqlTemplateContext parameter) {
        return new SqlUpdateImpl(context, context.getSqlTemplateEngine().getTemplate(path), parameter);
    }

    /**
     * SQLを元にテーブルを検索します。
     * @param <T> 処理対象となるエンティティの型
     * @param baseClass エンティティのクラス
     * @param sql SQL
     * @return SQL参照用のクエリ
     */
    public <T> SqlSelect<T> selectBySql(@NonNull Class<T> baseClass, @NonNull String sql) {
        return new SqlSelectImpl<T>(context, baseClass, context.getSqlTemplateEngine().getTemplateByText(sql), new EmptyValueSqlTemplateContext());
    }

    /**
     * SQLを元にテーブルを検索します。
     * @param <T> 処理対象となるエンティティの型
     * @param baseClass エンティティのクラス
     * @param sql SQL
     * @param parameter パラメータ
     * @return SQL参照用のクエリ
     */
    public <T> SqlSelect<T> selectBySql(@NonNull Class<T> baseClass, @NonNull String sql, @NonNull SqlTemplateContext parameter) {
        return new SqlSelectImpl<T>(context, baseClass, context.getSqlTemplateEngine().getTemplateByText(sql), parameter);
    }

    /**
     * カウント用のSQLを実行します。
     * @param sql SQL
     * @return カウント結果
     */
    public long getCountBySql(@NonNull String sql) {
        return new SqlCountImpl(context, context.getSqlTemplateEngine().getTemplateByText(sql), new EmptyValueSqlTemplateContext())
                .getCount();
    }

    /**
     * カウント用のSQLを実行します。
     * @param sql SQL
     * @param parameter パラメータ
     * @return カウント結果
     */
    public long getCountBySql(@NonNull String sql, @NonNull SqlTemplateContext parameter) {
        return new SqlCountImpl(context, context.getSqlTemplateEngine().getTemplateByText(sql), parameter)
                .getCount();
    }

    /**
     * SQLを元にテーブルを更新（INSERT / UPDATE/ DELETE）をします。
     * @param sql SQL
     * @return SQL更新用のクエリ
     */
    public SqlUpdate updateBySql(@NonNull String sql) {
        return new SqlUpdateImpl(context, context.getSqlTemplateEngine().getTemplateByText(sql), new EmptyValueSqlTemplateContext());
    }

    /**
     * SQLを元にテーブルを更新（INSERT / UPDATE/ DELETE）をします。
     * @param sql SQL
     * @param parameter パラメータ
     * @return SQL更新用のクエリ
     */
    public SqlUpdate updateBySql(@NonNull String sql, @NonNull SqlTemplateContext parameter) {
        return new SqlUpdateImpl(context, context.getSqlTemplateEngine().getTemplateByText(sql), parameter);
    }

}
