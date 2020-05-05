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

import org.springframework.beans.PropertyAccessor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.type.ValueType;
import com.github.mygreen.sqlmapper.type.ValueTypeResolver;
import com.github.mygreen.sqlmapper.where.NamedParameterContext;

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
     * SQLテンプレート中の変数をバインドした情報
     */
    @Getter
    private NamedParameterContext bindParameter = new NamedParameterContext(new MapSqlParameterSource());

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
    private ValueTypeResolver valueTypeResolver;

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

        // パラメータ変数の現在まで払い出されたインデックス情報の引継ぎ
        this.bindParameter.setArgIndex(parent.bindParameter.getArgIndex());

        // 各種情報の引継ぎ
        this.propertyAccessor = parent.propertyAccessor;
        this.valueTypeResolver = parent.valueTypeResolver;

    }

    /**
     * 変数名を払い出す。
     * @return 変数名
     */
    public String createArgName() {
        return bindParameter.createArgName();
    }

    /**
     * 指定した件数の変数名を払い出す。
     * @param count 払い出す件数。
     * @return 変数名
     */
    public String[] createArgNames(int count) {
        return bindParameter.createArgNames(count);
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
    public void addSql(String sql, Object bindValue, String bindName, ValueType valueType) {
        sqlBuf.append(sql);

        if(valueType != null) {
            valueType.bindValue(bindValue, bindParameter.getParamSource(), bindName);
        } else {
            bindParameter.getParamSource().addValue(bindName, bindValue);
        }
    }

    /**
     * <code>SQL</code>とバインド変数を追加します。
     * @param sql SQL
     * @param bindParameter バインドする変数情報
     */
    public void addSql(final String sql, final NamedParameterContext bindParameter) {
        this.sqlBuf.append(sql);

        // 変数のバインド情報をマージする
        this.bindParameter.getParamSource().addValues(bindParameter.getParamSource().getValues());
        this.bindParameter.setArgIndex(bindParameter.getArgIndex());

    }
}