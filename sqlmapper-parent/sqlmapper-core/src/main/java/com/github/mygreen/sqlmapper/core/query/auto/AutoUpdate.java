package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.Map;

import org.springframework.dao.OptimisticLockingFailureException;

import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

/**
 * 更新を行うSQLを自動生成するクエリです。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public interface AutoUpdate<T> {

    /**
     * クエリタイムアウトの秒数を設定します。
     * <p>{@literal -1} を指定するとJDBC ドライバーのデフォルト値を使用します。
     *
     * @since 0.3
     * @param seconds クエリタイムアウトの秒数
     * @return 自身のインスタンス。
     */
    AutoUpdate<T> queryTimeout(int seconds);

    /**
     * バージョンプロパティを通常の更新対象に含め、バージョンチェックの対象外とします。
     * <p>
     * このメソッドが呼び出されると、<code>update</code>文の<code>where</code>句にはバージョンのチェックが含まれなくなり、
     * バージョンプロパティは通常のプロパティと同じように更新対象に含められます ({@link #excludesNull()}や{@link #changedFrom(Object)}等も同じように適用されます)。
     * </p>
     *
     * @return このインスタンス自身
     */
    AutoUpdate<T> includesVersion();

    /**
     * <code>null</code>値のプロパティを更新対象から除外します。
     * @return このインスタンス自身
     */
    AutoUpdate<T> excludesNull();

    /**
     * バージョンチェックを行った場合に、更新行数が0行でも {@link OptimisticLockingFailureException} をスローしないようにします。
     * @return このインスタンス自身
     */
    AutoUpdate<T> suppresOptimisticLockException();

    /**
     * 指定のプロパティのみを更新対象とします。
     * <p>ID(主キー)は自動的に更新対象外となります。</p>
     * <p>アノテーション {@literal @Column(updatable = false)} が設定されているプロパティは自動的に対象外となります。</p>
     *
     * @param properties 更新対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    AutoUpdate<T> includes(PropertyPath<?>... properties);

    /**
     * 指定のプロパティを更新対象から除外します。
     * <p>ID(主キー)は自動的に更新対象外となります。</p>
     * <p>アノテーション {@literal @Column(updatable = false)} が設定されているプロパティは自動的に対象外となります。</p>
     *
     * @param properties 除外対象のプロパティ名。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    AutoUpdate<T> excludes(PropertyPath<?>... properties);

    /**
     * beforeEntityから変更のあったプロパティだけを更新対象とします
     * @param beforeEntity 変更前の状態を持つエンティティ
     * @return このインスタンス自身
     */
    AutoUpdate<T> changedFrom(T beforeEntity);

    /**
     * beforeStatesから変更のあったプロパティだけを更新対象とします。
     * <p>引数 {@literal beforeStates} のサイズが {@literal 0} のときは何もしません。
     * @param beforeStates 変更前の状態を持つマップ。（key=プロパティ名、value=プロパティ値）
     * @return このインスタンス自身。
     */
    AutoUpdate<T> changedFrom(Map<String, Object> beforeStates);

    /**
     * 更新クエリを実行します。
     * @return 更新したレコード件数です。更新対象のプロパティ（カラム）がない場合は {@literal 0} を返します。
     * @throws OptimisticLockingFailureException 楽観的排他制御を行う場合に該当するレコードが存在しないときにスローされます。
     */
    int execute();

}
