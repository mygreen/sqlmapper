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

/**
 * SQLをトークンに分解するクラスです。.
 *
 * @author higa
 */
public interface SqlTokenizer {

    /**
     * @return トークンを返します。
     */
    String getToken();

    /**
     * @return SQLを返します。
     */
    String getSql();

    /**
     * @return 現在解析しているポジションより前のSQLを返します。
     */
    String getBefore();

    /**
     * @return 現在解析しているポジションより後ろのSQLを返します。
     */
    String getAfter();

    /**
     * @return 現在解析しているポジションを返します。
     */
    int getPosition();

    /**
     * @return 現在のトークン種別を返します。
     */
    TokenType getTokenType();

    /**
     * @return 次のトークン種別を返します。
     */
    TokenType getNextTokenType();

    /**
     * @return 次のトークンに進みます。
     */
    TokenType next();

    /**
     * トークンをスキップします。
     *
     * @return スキップしたトークン
     */
    String skipToken();

    /**
     * ホワイトスペースをスキップします。
     *
     * @return スキップしたホワイストスペース
     */
    String skipWhitespace();

    enum TokenType {
        SQL,
        COMMENT,
        ELSE,
        BIND_VARIABLE,
        EOF
    }
}