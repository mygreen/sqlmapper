package com.github.mygreen.sqlmapper.core.query.auto;

import org.springframework.dao.DuplicateKeyException;

import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

/**
 * 挿入を行うSQLを自動生成するクエリです。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public interface AutoInsert<T> {

    /**
     * 指定のプロパティのみを挿入対象とします。
     * <p>アノテーション {@literal @Column(insertable = false)} が設定されているプロパティは対象外となります。</p>
     *
     * @param properties 挿入対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    AutoInsert<T> includes(PropertyPath<?>... properties);

    /**
     * 指定のプロパティを挿入対象から除外します。
     *
     * @param properties 除外対象のプロパティ情報。
     * @return 自身のインスタンス。
     */
    AutoInsert<T> excludes(PropertyPath<?>... properties);

    /**
     * クエリを実行します。
     * @return 挿入した行数。
     * @throws DuplicateKeyException 主キーなどのが一意制約に違反したとき。
     */
    int execute();

}
