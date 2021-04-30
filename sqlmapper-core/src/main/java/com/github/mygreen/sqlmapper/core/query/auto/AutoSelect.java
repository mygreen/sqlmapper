package com.github.mygreen.sqlmapper.core.query.auto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.github.mygreen.sqlmapper.core.query.IllegalOperateException;
import com.github.mygreen.sqlmapper.core.query.JoinAssociation;
import com.github.mygreen.sqlmapper.core.query.JoinCondition;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.PropertyPath;

/**
 * 抽出を行うSQLを自動生成するクエリです。
 *
 * @author T.TSUCHIE
 *
 * @param <T> 処理対象となるエンティティの型
 */
public interface AutoSelect<T> {

    /**
     * ヒントを設定します。
     * @param hint ヒント
     * @return このインスタンス自身
     */
    AutoSelect<T> hint(String hint);

    /**
     * 抽出する行数を指定します。
     * @param limit 行数
     * @return このインスタンス自身
     */
    AutoSelect<T> limit(int limit);

    /**
     * 抽出するデータの開始位置を指定します。
     * @param offset 開始位置。
     * @return このインスタンス自身
     */
    AutoSelect<T> offset(int offset);

    /**
     * 指定のプロパティのみを挿入対象とします。
     * <p>アノテーション {@literal @Column(insertable = false)} が設定されているプロパティは対象外となります。</p>
     *
     * @param properties 挿入対象のプロパティ情報。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException エンティティに存在しないプロパティ名を指定した場合にスローされます。
     */
    AutoSelect<T> includes(PropertyPath<?>... properties);

    /**
     * 指定のプロパティを挿入対象から除外します。
     *
     * @param properties 除外対象のプロパティ情報。
     * @return 自身のインスタンス。
     */
    AutoSelect<T> excludes(PropertyPath<?>... properties);

    /**
     * FROM句で指定したテーブルと内部結合（{@literal INNERT JOIN}）する条件を指定します。
     *
     * @param <ENTITY> 結合先のテーブルのエンティティタイプ
     * @param toEntityPath 結合先テーブルのエンティティ情報
     * @param conditioner 結合条件の組み立て
     * @return 自身のインスタンス
     */
    <ENTITY extends EntityPath<?>> AutoSelect<T> innerJoin(ENTITY toEntityPath,
            JoinCondition.Conditioner<ENTITY> conditioner);

    /**
     * FROM句で指定したテーブルと左外部結合（{@literal LEFT OUTER JOIN}）する条件を指定します。
     *
     * @param <ENTITY> 結合先のテーブルのエンティティタイプ
     * @param toEntityPath 結合先テーブルのエンティティ情報
     * @param conditioner 結合条件の組み立て
     * @return 自身のインスタンス
     * @throws IllegalOperateException 既に同じ組み合わせのエンティティ（テーブル）を指定しているときにスローされます。
     */
    <ENTITY extends EntityPath<?>> AutoSelect<T> leftJoin(ENTITY toEntityPath,
            JoinCondition.Conditioner<ENTITY> conditioner);

    /**
     * テーブル結合の際に複数のテーブルのエンティティの構成定義を指定します。
     *
     * @param <E1> エンティティタイプ1
     * @param <E2> エンティティタイプ2
     * @param entityPath1 エンティティ情報1
     * @param entityPath2 エンティティ情報2
     * @param associator エンティティの構成定義
     * @return 自身のインスタンス
     * @throws IllegalOperateException 既に同じ組み合わせのエンティティの構成定義を指定しているときにスローされます。
     */
    <E1, E2> AutoSelect<T> associate(EntityPath<E1> entityPath1, EntityPath<E2> entityPath2,
            JoinAssociation.Associator<E1, E2> associator);


    /**
     * 検索条件を指定します。
     * @param where 検索条件。
     * @return 自身のインスタンス。
     */
    AutoSelect<T> where(Predicate where);

    /**
     * ソート順を指定します。
     * @param orderBy ソートするロパティの並び順情報
     * @return 自身のインスタンス。
     */
    AutoSelect<T> orderBy(OrderSpecifier... orders);

    /**
     * WHERE句の条件にIdプロパティ(主キー)を指定します。
     *
     * @param idPropertyValues IDプロパティの値。エンティティに定義している順で指定する必要があります。
     * @return 自身のインスタンス。
     * @throws IllegalOperateException 指定したIDの個数とエンティティの個数と一致しないときにスローされます。
     */
    AutoSelect<T> id(Object... idPropertyValues);

    /**
     * WHERE句の条件にバージョンプロパティを指定します。
     *
     * @param versionPropertyValue バージョンプロパティの値。
     * @return 自身のインスタンス
     * @throws IllegalOperateException エンティティにバージョンキーが定義されていないときにスローされます。
     */
    AutoSelect<T> version(Object versionPropertyValue);

    /**
     * {@literal FOR UPDATE} を追加します。
     * @return このインスタンス自身。
     * @throws IllegalOperateException DBMSがこの操作をサポートしていない場合にスローされます。
     */
    AutoSelect<T> forUpdate();

    /**
     * {@literal FOR UPDATE NOWAIT} を追加します。
     * @return このインスタンス自身。
     * @throws IllegalOperateException DBMSがこの操作をサポートしていない場合にスローされます。
     */
    AutoSelect<T> forUpdateNoWait();

    /**
     * {@literal FOR UPDATE WAIT} を追加します。
     * @param seconds  ロックを獲得できるまでの最大待機時間(秒単位)
     * @return このインスタンス自身。
     * @throws IllegalOperateException DBMSがこの操作をサポートしていない場合にスローされます。
     */
    AutoSelect<T> forUpdateWait(int seconds);

    /**
     * SQLが返す結果セットの行数を返します。
     * @return SQLが返す結果セットの行数
     */
    long getCount();

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。
     * @throws IncorrectResultSizeDataAccessException 1件も見つからない場合、2件以上見つかった場合にスローされます。
     */
    T getSingleResult();

    /**
     * 検索してベースオブジェクトを返します。
     *
     * @return ベースオブジェクト。1件も対象がないときは空を返します。
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
