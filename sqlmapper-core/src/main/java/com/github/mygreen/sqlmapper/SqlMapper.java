package com.github.mygreen.sqlmapper;

import com.github.mygreen.sqlmapper.query.auto.AutoDelete;
import com.github.mygreen.sqlmapper.query.auto.AutoInsert;
import com.github.mygreen.sqlmapper.query.auto.AutoSelect;
import com.github.mygreen.sqlmapper.query.auto.AutoUpdate;

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
}
