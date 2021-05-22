package com.github.mygreen.sqlmapper.core;

import java.util.Collection;
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

    /**
     * SqlMapperの設定情報。
     */
    @Getter
    private final SqlMapperContext context;

    /**
     * SQLを自動生成して抽出します。
     * @param <T> 処理対象となるエンティティの型
     * @param entityPath 抽出対象のテーブルのエンティティ情報
     * @return 抽出を行うSQLを自動生成するクエリ
     */
    public <T> AutoSelect<T> selectFrom(@NonNull EntityPath<T> entityPath) {
        return new AutoSelectImpl<T>(context, entityPath);
    }

    /**
     * SQLを自動生成して挿入します。
     * @param <T> 処理対象となるエンティティの型
     * @param entity 挿入対象のテーブルのエンティティのインスタンス
     * @return 挿入を行うSQLを自動生成するクエリ
     */
    public <T> AutoInsert<T> insert(@NonNull T entity) {
        return new AutoInsertImpl<T>(context, entity);
    }

    /**
     * SQLを自動生成して削除します。
     * @param <T> 処理対象となるエンティティの型
     * @param entity 削除対象のテーブルのエンティティのインスタンス
     * @return 削除を行うSQLを自動生成するクエリ
     */
    public <T> AutoDelete<T> delete(@NonNull T entity){
        return new AutoDeleteImpl<T>(context, entity);
    }

    /**
     * SQLを自動生成して更新します。
     * @param <T> 処理対象となるエンティティの型
     * @param entity エンティティのインスタンス
     * @return 更新を行うSQLを自動生成するクエリ
     */
    public <T> AutoUpdate<T> update(@NonNull T entity) {
        return new AutoUpdateImpl<T>(context, entity);
    }

    /**
     * 任意の条件を指定して、SQLを自動生成して削除します。
     * @param <T> 処理対象となるエンティティの型
     * @param entityPath 削除対象のテーブルのエンティティ情報
     * @return 任意の条件をして削除を行うSQLを自動生成するクエリ
     */
    public <T> AutoAnyDelete<T> deleteFrom(@NonNull EntityPath<T> entityPath) {
        return new AutoAnyDeleteImpl<T>(context, entityPath);
    }

    /**
     * SQLを自動生成してバッチ挿入します。
     * <p>主キーが識別子（IDENTITY）による自動生成の場合は、バッチ実行ではなく1件ずつ処理されるので注意してください。</p>
     *
     * @param <T> 処理対象となるエンティティの型
     * @param entities バッチ挿入対象のテーブルのエンティティのインスタンス
     * @return バッチ挿入を行うSQLを自動生成するクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    @SuppressWarnings("unchecked")
    public <T> AutoBatchInsert<T> insertBatch(T... entities) {
        return new AutoBatchInsertImpl<T>(context, entities);
    }

    /**
     * SQLを自動生成してバッチ挿入します。
     * <p>主キーが識別子（IDENTITY）による自動生成の場合は、バッチ実行ではなく1件ずつ処理されるので注意してください。</p>
     *
     * @param <T> 処理対象となるエンティティの型
     * @param entities バッチ挿入対象のテーブルのエンティティのインスタンス
     * @return バッチ挿入を行うSQLを自動生成するクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    public <T> AutoBatchInsert<T> insertBatch(List<T> entities) {
        return new AutoBatchInsertImpl<T>(context, entities);
    }

    /**
     * SQLを自動生成してバッチ更新します。
     * @param <T> 処理対象となるエンティティの型
     * @param entities バッチ更新対象のテーブルのエンティティのインスタンス
     * @return バッチ更新を行うSQLを自動生成するクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    @SuppressWarnings("unchecked")
    public <T> AutoBatchUpdate<T> updateBatch(T... entities) {
        return new AutoBatchUpdateImpl<T>(context, entities);
    }

    /**
     * SQLを自動生成してバッチ更新します。
     * @param <T> 処理対象となるエンティティの型
     * @param entities バッチ更新対象のテーブルのエンティティのインスタンス
     * @return バッチ更新を行うSQLを自動生成するクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    public <T> AutoBatchUpdate<T> updateBatch(List<T> entities) {
        return new AutoBatchUpdateImpl<T>(context, entities);
    }

    /**
     * SQLを自動生成してバッチ削除します。
     * @param <T> 処理対象となるエンティティの型
     * @param entities バッチ削除対象のテーブルのエンティティのインスタンス
     * @return バッチ削除を行うSQLを自動生成するクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    @SuppressWarnings("unchecked")
    public <T> AutoBatchUpdate<T> deleteBatch(T... entities) {
        return new AutoBatchUpdateImpl<T>(context, entities);
    }

    /**
     * SQLを自動生成してバッチ削除します。
     * @param <T> 処理対象となるエンティティの型
     * @param entities バッチ削除対象のテーブルのエンティティのインスタンス
     * @return バッチ削除を行うSQLを自動生成するクエリ
     * @throws IllegalOperateException 引数で指定したエンティティの並びが空のときにスローされます。
     */
    public <T> AutoBatchUpdate<T> deleteBatch(Collection<T> entities) {
        return new AutoBatchUpdateImpl<T>(context, entities);
    }

    /**
     * SQLテンプレートファイルを指定して抽出します。
     * @param <T> 処理対象となるエンティティの型
     * @param baseClass エンティティのクラス
     * @param path SQLテンプレートのファイルパス。
     * @return SQLテンプレートによる抽出を行うクエリ
     */
    public <T> SqlSelect<T> selectBySqlFile(@NonNull Class<T> baseClass, @NonNull String path) {
        return new SqlSelectImpl<T>(context, baseClass, context.getSqlTemplateEngine().getTemplate(path), new EmptyValueSqlTemplateContext());
    }

    /**
     * SQLテンプレートファイルを指定して抽出します。
     * @param <T> 処理対象となるエンティティの型
     * @param baseClass エンティティのクラス
     * @param path SQLテンプレートのファイルパス。
     * @param parameter SQLテンプレートのパラメータ
     * @return SQLテンプレートによる抽出を行うクエリ
     */
    public <T> SqlSelect<T> selectBySqlFile(@NonNull Class<T> baseClass, @NonNull String path, @NonNull SqlTemplateContext parameter) {
        return new SqlSelectImpl<T>(context, baseClass, context.getSqlTemplateEngine().getTemplate(path), parameter);
    }

    /**
     * SQLテンプレートファイルを指定して件数のカウントを取得します。
     * @param path SQLテンプレートのファイルパス。
     * @return カウント結果
     */
    public long getCountBySqlFile(@NonNull String path) {
        return new SqlCountImpl(context, context.getSqlTemplateEngine().getTemplate(path), new EmptyValueSqlTemplateContext())
                .getCount();
    }

    /**
     * SQLテンプレートファイルを指定して件数のカウントを取得します。
     * @param path SQLテンプレートのファイルパス。
     * @param parameter SQLテンプレートのパラメータ
     * @return カウント結果
     */
    public long getCountBySqlFile(@NonNull String path, @NonNull SqlTemplateContext parameter) {
        return new SqlCountImpl(context, context.getSqlTemplateEngine().getTemplate(path), parameter)
                .getCount();
    }

    /**
     * SQLテンプレートファイルを指定して更新（INSERT / UPDATE/ DELETE）を行います。
     * @param path SQLテンプレートのファイルパス。
     * @return SQLテンプレートによる更新を行うクエリ
     */
    public SqlUpdate updateBySqlFile(@NonNull String path) {
        return new SqlUpdateImpl(context, context.getSqlTemplateEngine().getTemplate(path), new EmptyValueSqlTemplateContext());
    }

    /**
     * SQLテンプレートファイルを指定して更新（INSERT / UPDATE/ DELETE）を行います。
     * @param path SQLテンプレートのファイルパス。
     * @param parameter SQLテンプレートのパラメータ
     * @return SQLテンプレートによる更新を行うクエリ
     */
    public SqlUpdate updateBySqlFile(@NonNull String path, @NonNull SqlTemplateContext parameter) {
        return new SqlUpdateImpl(context, context.getSqlTemplateEngine().getTemplate(path), parameter);
    }

    /**
     * SQLテンプレートを指定して抽出します。
     * @param <T> 処理対象となるエンティティの型
     * @param baseClass エンティティのクラス
     * @param sql SQLテンプレート
     * @return SQLテンプレートによる抽出を行うクエリ
     */
    public <T> SqlSelect<T> selectBySql(@NonNull Class<T> baseClass, @NonNull String sql) {
        return new SqlSelectImpl<T>(context, baseClass, context.getSqlTemplateEngine().getTemplateByText(sql), new EmptyValueSqlTemplateContext());
    }

    /**
     * SQLテンプレートを指定して抽出します。
     * @param <T> 処理対象となるエンティティの型
     * @param baseClass エンティティのクラス
     * @param sql SQLテンプレート
     * @param parameter SQLテンプレートのパラメータ
     * @return SQLテンプレートによる抽出を行うクエリ
     */
    public <T> SqlSelect<T> selectBySql(@NonNull Class<T> baseClass, @NonNull String sql, @NonNull SqlTemplateContext parameter) {
        return new SqlSelectImpl<T>(context, baseClass, context.getSqlTemplateEngine().getTemplateByText(sql), parameter);
    }

    /**
     * SQLテンプレートを指定して件数のカウントを取得します。
     * @param sql SQLテンプレート
     * @return カウント結果
     */
    public long getCountBySql(@NonNull String sql) {
        return new SqlCountImpl(context, context.getSqlTemplateEngine().getTemplateByText(sql), new EmptyValueSqlTemplateContext())
                .getCount();
    }

    /**
     * SQLテンプレートを指定して件数のカウントを取得します。
     * @param sql SQLテンプレート
     * @param parameter SQLテンプレートのパラメータ
     * @return カウント結果
     */
    public long getCountBySql(@NonNull String sql, @NonNull SqlTemplateContext parameter) {
        return new SqlCountImpl(context, context.getSqlTemplateEngine().getTemplateByText(sql), parameter)
                .getCount();
    }

    /**
     * SQLテンプレートを指定して更新（INSERT / UPDATE/ DELETE）を行います。
     * @param sql SQLテンプレート
     * @return SQLテンプレートによる更新を行うクエリ
     */
    public SqlUpdate updateBySql(@NonNull String sql) {
        return new SqlUpdateImpl(context, context.getSqlTemplateEngine().getTemplateByText(sql), new EmptyValueSqlTemplateContext());
    }

    /**
     * SQLテンプレートを指定して更新（INSERT / UPDATE/ DELETE）を行います。
     * @param sql SQLテンプレート
     * @param parameter SQLテンプレートのパラメータ
     * @return SQLテンプレートによる更新を行うクエリ
     */
    public SqlUpdate updateBySql(@NonNull String sql, @NonNull SqlTemplateContext parameter) {
        return new SqlUpdateImpl(context, context.getSqlTemplateEngine().getTemplateByText(sql), parameter);
    }

}
