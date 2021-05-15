package com.github.mygreen.sqlmapper.metamodel;


/**
 * エンティティのパスを表現します。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> エンティティのタイプ
 */
public interface EntityPath<T> extends Path<T> {

    /**
     * 指定したプロパティ情報を取得する。
     * @param propertyName プロパティ名
     * @return プロパティ情報。指定したプロパティを持たない場合は{@literal null} を返します。
     */
    PropertyPath<?> getPropertyPath(String propertyName);

    /**
     * 指定したプロパティ情報を検索する。
     * もし、指定したプロパティが埋め込みエンティティ内に存在すれば、埋め込みエンティティ内のプロパティを返す。
     *
     * @param propertyName プロパティ名
     * @return プロパティ情報。指定したプロパティを持たない場合は{@literal null} を返します。
     */
    PropertyPath<?> findPropertyPath(String propertyName);

}
