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
package com.github.mygreen.sqlmapper.query;

/**
 * トークンを認識するクラスです。
 *
 * @author higa
 *
 */
public class QueryTokenizer {

    /**
     * EOFをあらわします。
     */
    public static final int TT_EOF = -1;

    /**
     * 単語をあらわします。
     */
    public static final int TT_WORD = -2;

    /**
     * 引用文をあらわします。
     */
    public static final int TT_QUOTE = -3;

    /**
     * パラメータ {@code ?} を表します。
     */
    public static final int TT_PARAM = -4;

    /**
     * 名前付きパラメータ {@code :xxx} を表します。
     */
    public static final int TT_NAMED_PARAM = -5;

    /**
     * 単語、引用文以外をあらわします。
     */
    public static final int TT_OTHER = -6;

    /**
     * 解析対象の文字列
     */
    private final String str;

    /**
     * 解析対象の文字列の奈川
     */
    private final int length;

    /**
     * 解析している現在の位置（インデックス）
     */
    private int pos;

    /**
     * 解析している次の位置（インデックス）
     */
    private int nextPos;

    /**
     * 現在のトークン
     */
    private String token;

    /**
     * 次のオークンのタイプ
     */
    private int type = TT_EOF;

    /**
     * {@link QueryTokenizer}を作成します。
     *
     * @param str ソースの文字列
     */
    public QueryTokenizer(final String str) {
        this.str = str;
        length = str.length();
        peek(0);
    }

    /**
     * 次のトークンを前もって調べます。
     *
     * @param index インデックス
     */
    protected void peek(int index) {
        if (index < length) {
            char c = str.charAt(index);
            if (Character.isWhitespace(c) || isOrdinary(c)) {
                type = TT_OTHER;
            } else if (c == '?') {
                //TODO:
                type = TT_PARAM;
            } else if (c == '\'') {
                type = TT_QUOTE;
            } else if (c == ':') {
                type = TT_NAMED_PARAM;
            } else {
                type = TT_WORD;
            }
            pos = index;
            nextPos = index + 1;
        } else {
            type = TT_EOF;
        }
    }

    /**
     * トークンのタイプを返します。
     *
     * @return トークンのタイプ
     */
    public int nextToken() {
        if (type == TT_EOF) {
            return TT_EOF;
        }
        if (type == TT_QUOTE) {
            // シングルルクオートの場合、次のクオートが現れるところまで進め切り出す。
            for (int i = nextPos; i < length; i++) {
                char c = str.charAt(i);
                if (c == '\'') {
                    i++;
                    if (i >= length) {
                        token = str.substring(pos, i);
                        type = TT_EOF;
                        return TT_QUOTE;
                    } else if (str.charAt(i) != '\'') {
                        token = str.substring(pos, i);
                        peek(i);
                        return TT_QUOTE;
                    }

                }
            }
            // シングルクオートが閉じられていないとき
            throw new IllegalQueryException(String.format("Not close single quote of (%s)", str));
        }

        if(type == TT_NAMED_PARAM) {
            for(int i=nextPos; i < length; i++) {
                char c = str.charAt(i);
                if(!isNamedParamChar(c)) {
                    // 名前付き変数の終わり
                    token = str.substring(pos, i);
                    peek(i);
                    return TT_NAMED_PARAM;

                }
            }

            // 文字列の最後のとき
            token = str.substring(pos);
            type = TT_EOF;
            return TT_NAMED_PARAM;
        }

        for (int i = nextPos; i < length; i++) {
            char c = str.charAt(i);
            if (isOther(c)) {
                if (type == TT_WORD) {
                    token = str.substring(pos, i);
                    pos = i;
                    nextPos = pos + 1;
                    type = TT_OTHER;
                    return TT_WORD;

                }

                if(type == TT_PARAM) {
                    token = str.substring(pos, i);
                    pos = i;
                    nextPos = pos + 1;
                    type = TT_OTHER;
                    return TT_PARAM;
                }

                if(type == TT_NAMED_PARAM) {
                    token = str.substring(pos, i);
                    pos = i;
                    nextPos = pos + 1;
                    type = TT_OTHER;
                    return TT_NAMED_PARAM;
                }

            } else if(c == '?') {
                //TODO:
                if(type == TT_OTHER) {
                    token = str.substring(pos, i);
                    pos = i;
                    nextPos = pos + 1;
                    type = TT_PARAM;
                    return TT_OTHER;
                }

            } else if (c == '\'') {
                if (type == TT_WORD) {
                    token = str.substring(pos, i);
                    pos = i;
                    nextPos = pos + 1;
                    type = TT_QUOTE;
                    return TT_WORD;
                }
                if( type == TT_PARAM) {
                    // TODO
                    token = str.substring(pos, i);
                    pos = i;
                    nextPos = pos + 1;
                    type = TT_QUOTE;
                    return TT_PARAM;
                }
                if (type == TT_OTHER) {
                    token = str.substring(pos, i);
                    pos = i;
                    nextPos = pos + 1;
                    type = TT_QUOTE;
                    return TT_OTHER;
                }
            } else if (c == ':') {
                // 名前付きパラメータの開始
                // 最後が「:」で終わるとき、単語として判断
                if(pos == length) {
                    token = str.substring(pos, i);
                    pos = i;
                    nextPos = pos + 1;
                    type = TT_WORD;
                    return TT_OTHER;
                }

                if(!isNamedParamChar(str.charAt(i+1))) {
                    // 次の単語が名前付きパラメータとして使用できないとき、単語として判断
                    continue;
                }

                int beforeToken = type;
                token = str.substring(pos, i);
                pos = i;
                nextPos = pos + 1;
                type = TT_NAMED_PARAM;
                return beforeToken;

            } else {
                //TODO
                if(type == TT_PARAM) {
                    token = str.substring(pos, i);
                    pos = i;
                    nextPos = pos + 1;
                    type = TT_WORD;
                    return TT_PARAM;
                }

                if (type == TT_OTHER) {
                    token = str.substring(pos, i);
                    pos = i;
                    nextPos = pos + 1;
                    type = TT_WORD;
                    return TT_OTHER;
                }
            }
        }
        token = str.substring(pos, length);
        if (type == TT_WORD) {
            type = TT_EOF;
            return TT_WORD;
        }
        if(type == TT_PARAM) {
            type = TT_EOF;
            return TT_PARAM;
        }

        type = TT_EOF;
        return TT_OTHER;
    }

    /**
     * 単語以外かどうかを返します。
     *
     * @param c 文字
     * @return 単語以外かどうか
     */
    protected boolean isOther(char c) {
        return Character.isWhitespace(c) || isOrdinary(c);
    }

    /**
     * 単独で文字として認識するかどうかを返します。
     *
     * @param c 文字
     * @return 単独で文字として認識するかどうか
     */
    protected boolean isOrdinary(char c) {
        return c == '=' || /*c == '?' ||*/ c == '<' || c == '>' || c == '('
                || c == ')' || c == '!' || c == '*' || c == '-' || c == ',';
    }

    /**
     * 名前付きパラメータの変数として認識するかどうか。
     * @param c
     * @return
     */
    protected boolean isNamedParamChar(char c) {
        return ('0' <= c && c <= '9')
                || ('a' <= c && c <= 'z')
                || ('A' <= c && c <= 'Z');

    }

    protected int doNamedParam(int index) {

        // 最後が「:」で終わるとき、タイプは以前のまま
        if(index == length) {
            token = str.substring(pos, index);
            pos = index;
            nextPos = pos+1;
            type = TT_OTHER;
            return TT_OTHER;
        }

        int lastIndex = index;
        for(int i=index+1; i < length; i++) {
            char c = str.charAt(i);
            if(('0' <= c && c <= '9')
                    || ('a' <= c && c <= 'z')
                    || ('A' <= c && c <= 'Z')
                    ) {
                lastIndex = i;
                continue;
            }
            break;
        }

        if (lastIndex > index) {
            // 名前付き変数として成り立つとき
            token = str.substring(pos, lastIndex);
            pos = lastIndex;
            nextPos = pos + 1;
            type = TT_OTHER;
            return TT_NAMED_PARAM;
        }

        return TT_OTHER;
    }

    /**
     * トークンを返します。
     *
     * @return トークン
     */
    public String getToken() {
        return token;
    }
}
