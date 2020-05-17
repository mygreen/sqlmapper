/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package com.github.mygreen.sqlmapper.sql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.PropertyAccessor;

import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.type.ValueType;
import com.github.mygreen.sqlmapper.type.ValueTypeRegistry;

import lombok.Getter;
import lombok.Setter;

/**
 * <code>SQL</code>を実行するときのコンテキストです。 コンテキストで<code>SQL</code>を実行するのに必要な情報を組み立てた後、
 * <code>getSql()</code>, <code>getBindVariables()</code>,
 * <code>getBindVariableTypes()</code>で、 情報を取り出して<code>SQL</code>を実行します。
 * <code>SQL</code>で<code>BEGIN</code>コメントと<code>END</code>コメントで囲まれている部分が、
 * 子供のコンテキストになります。 通常は、 <code>WHERE</code>句を<code>BEGIN</code>コメントと<code>END</code>コメントで囲み、
 * <code>WHERE</code>句の中の<code>IF</code>コメントが1つでも成立した場合、<code>enabled</code>になります。
 *
 * @author higa
 *
 */
public class SqlContext {

    private StringBuffer sqlBuf = new StringBuffer(255);

    /**
     * SQLテンプレート中の変数をバインドしたパラメータ
     */
    @Getter
    private List<Object> bindParams = new ArrayList<>();

    /**
     * <code>BEGIN</code>コメントと<code>END</code>コメントで、囲まれた子供のコンテキストが有効かどうか。
     */
    @Setter
    @Getter
    private boolean enabled = true;

    /**
     * パラメータに指定したオブジェクトに対するアクセッサーを取得します。
     */
    @Setter
    @Getter
    private PropertyAccessor propertyAccessor;

    /**
     * パラメータで指定した値を変換するために使用します。
     */
    @Setter
    @Getter
    private ValueTypeRegistry valueTypeRegistry;

    @Setter
    @Getter
    private Dialect dialect;

    /**
     * 親のノードの情報。
     */
    @Getter
    private SqlContext parent;

    public SqlContext() {
    }

    /**
     * Creates a <code>SqlContextImpl</code> with a specific <code>parent</code>
     *
     * @param parent the parent context.
     */
    public SqlContext(final SqlContext parent) {
        this.parent = parent;
        this.enabled = false;

        // 各種情報の引継ぎ
        this.propertyAccessor = parent.propertyAccessor;
        this.valueTypeRegistry = parent.valueTypeRegistry;

    }

    /**
     * <code>SQL</code>を取得します。
     *
     * @return SQL
     */
    public String getSql() {
        return sqlBuf.toString();
    }

    /**
     * <code>SQL</code>を追加します。
     *
     * @parm sql SQL
     */
    public void addSql(String sql) {
        sqlBuf.append(sql);
    }

    /**
     * <code>SQL</code>とバインド変数を追加します。
     * @param sql SQL
     * @param bindValue バインドする変数の値
     * @param bindName バインドする変数の名称
     * @param valueType バインドする変数のタイプに対応する {@value ValueType}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addSql(String sql, Object bindValue, ValueType valueType) {
        sqlBuf.append(sql);

        // Dialectで変更があるような場合は変換する。
        valueType = dialect.getValueType(valueType);

        if(valueType != null) {
            bindParams.add(valueType.getSqlParameterValue(bindValue));
        } else {
            bindParams.add(bindValue);
        }
    }

    /**
     * <code>SQL</code>とバインド変数を追加します。
     * @param sql SQL
     * @param bindParams バインドする変数情報
     */
    public void addSql(final String sql, final List<Object> bindParams) {
        this.sqlBuf.append(sql);
        this.bindParams.addAll(bindParams);

    }
}