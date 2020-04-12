package com.github.mygreen.sqlmapper.naming;


/**
 * DBのテーブルやカラムをJavaのエンティティ・クラスにマッピングする際の命名規則に沿った変換を行う。
 *
 *
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

    /**
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

}
