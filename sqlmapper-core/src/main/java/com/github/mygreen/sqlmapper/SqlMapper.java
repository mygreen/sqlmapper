package com.github.mygreen.sqlmapper;

import java.net.MalformedURLException;
import java.util.List;

import com.github.mygreen.sqlmapper.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.query.auto.AutoAnyDelete;
import com.github.mygreen.sqlmapper.query.auto.AutoBatchInsert;
import com.github.mygreen.sqlmapper.query.auto.AutoBatchUpdate;
import com.github.mygreen.sqlmapper.query.auto.AutoDelete;
import com.github.mygreen.sqlmapper.query.auto.AutoInsert;
import com.github.mygreen.sqlmapper.query.auto.AutoSelect;
import com.github.mygreen.sqlmapper.query.auto.AutoUpdate;
import com.github.mygreen.sqlmapper.query.sql.SqSelect;

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
    public <T> SqSelect<T> selectBySqlFile(@NonNull Class<T> baseClass, @NonNull String path) {
        return new SqSelect<T>(context, context.getResourceLoader().getResource(path));
    }

    /**
     * SQLファイルを元にテーブルを参照します。
     * @param <T> エンティティタイプ
     * @param baseClass エンティティのクラス
     * @param path SQLファイルのパス
     * @param parameter パラメータ
     * @return SQLファイル参照用のクエリ
     */
    public <T> SqSelect<T> selectBySqlFile(@NonNull Class<T> baseClass, @NonNull String path, Object parameter) throws MalformedURLException {
        return new SqSelect<T>(context, context.getResourceLoader().getResource(path), parameter);
    }

}
