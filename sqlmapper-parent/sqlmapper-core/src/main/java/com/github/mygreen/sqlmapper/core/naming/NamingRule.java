package com.github.mygreen.sqlmapper.core.naming;


/**
 * DBのテーブルやカラムをJavaのエンティティ・クラスにマッピングする際の命名規則に沿った変換を行う。
 *
 * @version 0.3.2
 * @author T.TSUCHIE
 *
 */
public interface NamingRule {

    /**
     * エンティティ名をテーブル名に変換する。
     * @param entityName エンティティ名
     * @return テーブル名
     */
    String entityToTable(String entityName);

//    /**
//     * テーブル名をエンティティ名に変換する。
//     * @param tableName テーブル名
//     * @return エンティティ名
//     */
//    String tableToEntity(String tableName);

    /**
     * プロパティ名をカラム名に変換する
     * @param propertyName プロパティ名
     * @return カラム名
     */
    String propertyToColumn(String propertyName);

    /**
     * カラム名をプロパティ名に変換する。
     * @param columnName カラム名
     * @return プロパティ名
     */
    String columnToProperty(String columnName);

    /**
     * プロパティ名をストアドプロシージャ／ファンクションの引数名に変換する
     * @param propertyName プロパティ名
     * @return ストアドプロシージャ／ファンクションの引数名
     */
    String propertyToStoredParam(String propertyName);

    /**
     * テーブルによる採番を行う際のシーケンス名を決定します。
     * <p>テーブル名とカラム名からシーケンス名に変換する。
     *
     * @since 0.3.2
     * @param tableName テーブル名
     * @param columnName カラム名
     * @return シーケンス名
     */
    String sequenceNameForTableGenerator(String talbeName, String columnName);

    /**
     * シーケンスによる採番を行う際のシーケンス名を決定します。
     * <p>テーブル名とカラム名からシーケンス名に変換する。
     *
     * @since 0.3.2
     * @param tableName テーブル名
     * @param columnName カラム名
     * @return シーケンス名
     */
    String sequenceNameForSequenceGenerator(String tableName, String columnName);

}
