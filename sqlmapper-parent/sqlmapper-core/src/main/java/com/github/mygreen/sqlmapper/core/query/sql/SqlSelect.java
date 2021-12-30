package com.github.mygreen.sqlmapper.core.query.sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

/**
 * SQLテンプレートによる抽出を行うクエリです。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象のエンティティの型
 */
public interface SqlSelect<T> {

    /**
     * クエリタイムアウトの秒数を設定します。
     * <p>{@literal -1} を指定するとJDBC ドライバーのデフォルト値を使用します。
     *
     * @since 0.3
     * @param seconds クエリタイムアウトの秒数
     * @return 自身のインスタンス。
     */
    SqlSelect<T> queryTimeout(int seconds);

    /**
     * フェッチ数を設定します。
     * <p>これをデフォルト値よりも高く設定すると、大きな結果セットを処理する際に、メモリ消費を犠牲にして処理速度が向上します。
     * <p>{@literal -1} を指定するとJDBC ドライバーのデフォルト値を使用します。
     *
     * @since 0.3
     * @param fetchSize フェッチ数
     * @return 自身のインスタンス。
     */
    SqlSelect<T> fetchSize(int fetchSize);

    /**
     * 最大行数を設定します。
     * <p>JDBCのStatementレベルで、結果セットのオブジェクトが含むことのできる最大行数を制限します。
     *  <br>制限値を超えた場合は通知なしの除外されます。
     * </p>
     * <p>RDMSでLIMIT句がサポートされていない場合に使用します。
     * <p>{@literal -1} を指定するとJDBC ドライバーのデフォルト値を使用します。
     *
     * @since 0.3
     * @param maxRows 最大行数
     * @return 自身のインスタンス。
     */
    SqlSelect<T> maxRows(int maxRows);

    /**
     * 抽出する行数を指定します。
     *
     * @since 0.3
     * @param limit 行数
     * @return このインスタンス自身
     */
    SqlSelect<T> limit(int limit);

    /**
     * 抽出するデータの開始位置を指定します。
     *
     * @since 0.3
     * @param offset 開始位置。0から始まります。
     * @return このインスタンス自身
     */
    SqlSelect<T> offset(int offset);

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。
     * @throws EmptyResultDataAccessException 1件も見つからなかった場合にスローされます。
     * @throws IncorrectResultSizeDataAccessException 2件以上見つかった場合にスローされます。
     */
    T getSingleResult();

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。1件も対象がないときは空を返します。
     * @throws IncorrectResultSizeDataAccessException 2件以上見つかった場合にスローされます。
     */
    Optional<T> getOptionalResult();

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return 1件も対象がないときは空のリストを返します。
     */
    List<T> getResultList();

    /**
     * 問い合わせ結果を{@link Stream} で取得します。
     * 問い合わせ結果全体のリストを作成しないため、問い合わせ結果が膨大になる場合でもメモリ消費量を抑えることが出来ます。
     *
     * @return 問い合わせの結果。
     */
    Stream<T> getResultStream();

}
