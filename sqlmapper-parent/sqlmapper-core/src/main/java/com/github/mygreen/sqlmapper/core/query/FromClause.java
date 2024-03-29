/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.github.mygreen.sqlmapper.core.query;

import org.springframework.util.StringUtils;

/**
 * from句を組み立てるクラスです。
 *
 * @author higa
 *
 */
public class FromClause {

    /**
     * SQLです。
     */
    private StringBuilder sql;

    /**
     * {@link FromClause}を作成します。
     *
     */
    public FromClause() {
        this(100);
    }

    /**
     * {@link FromClause}を作成します。
     *
     * @param capacity 初期容量
     */
    public FromClause(int capacity) {
        this.sql = new StringBuilder(capacity);
    }

    /**
     * SQLの長さを返します。
     *
     * @return SQLの長さ
     */
    public int getLength() {
        return sql.length();
    }

    /**
     * SQLに変換します。
     *
     * @return SQL
     */
    public String toSql() {
        return sql.toString();
    }

    /**
     * from句を追加します。
     *
     * @param tableName テーブル名
     * @param tableAlias テーブル別名。省略する場合は {@literal null} を指定。
     *
     */
    public void addSql(String tableName, String tableAlias) {
        sql.append(" from ").append(tableName);

        if(StringUtils.hasLength(tableAlias)) {
            sql.append(" ").append(tableAlias);
        }
    }

    /**
     * from句を追加します。
     *
     * @param tableName テーブル名
     * @param tableAlias テーブル別名。省略する場合は {@literal null} を指定。
     * @param lockHint ロック用のヒント
     */
    public void addSql(String tableName, String tableAlias, String lockHint) {
        sql.append(" from ")
            .append(tableName);

        if(StringUtils.hasLength(tableAlias)) {
            sql.append(" ")
                .append(tableAlias);
        }

        sql.append(" ").append(lockHint);
    }

    /**
     * 結合用のSQLを追加します。
     *
     * @param joinType 結合タイプ
     * @param tableName 結合先のテーブル名
     * @param tableAlias 結合先のテーブルの別名
     * @param condition 結合条件
     * @throws IllegalArgumentException 引数 {@literal joinType} で指定した結合タイプがサポート対象外のときにスローされます。
     */
    public void addSql(JoinType joinType, String tableName, String tableAlias, String condition) {

        switch(joinType) {
            case INNER:
                sql.append(" inner join ");
                break;
            case LEFT_OUTER:
                sql.append(" left outer join ");
                break;
            default:
                throw new IllegalArgumentException("Not support joinType : " + joinType.toString());
        }

        sql.append(tableName)
            .append(" ").append(tableAlias)
            .append(" on ")
            .append(condition);


    }

}
