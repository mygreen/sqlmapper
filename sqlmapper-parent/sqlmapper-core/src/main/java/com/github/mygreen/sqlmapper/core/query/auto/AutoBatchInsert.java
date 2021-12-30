package com.github.mygreen.sqlmapper.core.query.auto;

import org.springframework.dao.DuplicateKeyException;

import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

/**
 * バッチ挿入を行うSQLを自動生成するクエリです。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public interface AutoBatchInsert<T> {

    /**
     * クエリタイムアウトの秒数を設定します。
     * <p>{@literal -1} を指定するとJDBC ドライバーのデフォルト値を使用します。
     *
     * @param seconds クエリタイムアウトの秒数
     * @return 自身のインスタンス。
     */
    AutoBatchInsert<T> queryTimeout(int seconds);

    /**
     * 指定のプロパティのみを挿入対象とします。
     * <p>ID(主キー)、バージョンキー（排他キー）の場合は、必ず挿入対象となります。</p>
     * <p>{@link #excludes(PropertyPath...)} と同時に指定した場合、{@link #includes(PropertyPath...)}が優先されます。</p>
     *
     * @param properties 挿入対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    AutoBatchInsert<T> includes(PropertyPath<?>... properties);

    /**
     * 指定のプロパティを挿入対象から除外します。
     * <p>ID(主キー)、バージョンキー（排他キー）の場合は、必ず挿入対象となります。</p>
     * <p>{@link #includes(PropertyPath...)} と同時に指定した場合、{@link #includes(PropertyPath...)}が優先されます。</p>
     *
     * @param properties 除外対象のプロパティ情報。
     * @return 自身のインスタンス。
     */
    AutoBatchInsert<T> excludes(PropertyPath<?>... properties);

    /**
     * クエリを実行します。
     * @return 更新した行数。
     * @throws DuplicateKeyException 主キーなどのが一意制約に違反したとき。
     */
    int[] execute();

}
