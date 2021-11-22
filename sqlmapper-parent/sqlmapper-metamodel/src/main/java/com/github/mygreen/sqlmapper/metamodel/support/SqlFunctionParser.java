package com.github.mygreen.sqlmapper.metamodel.support;

import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.sqlmapper.metamodel.support.SqlFunctionTokenizer.TokenType;

import lombok.AllArgsConstructor;

/**
 * SQL関数の書式のパーサー
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class SqlFunctionParser {

    /**
     * 字句解析してトークンに分解した結果
     *
     */
    @AllArgsConstructor
    public static class Token {

        /**
         * トークンのタイプ
         */
        public final TokenType type;

        /**
         * トークンの値。
         */
        public final String value;

        /**
         * バインド変数の場合のインデックス。
         * <p>バインド変数でないときは、値は{@literal -1}です。
         */
        public final int bindBariableIndex;

    }

    /**
     * SQL関数をパースしてトークンに分解します。
     * @param sql パース対象のSQL
     * @return トークンに分解した結果。
     * @throws IllegalArgumentException 不明なトークンタイプの場合にスローされます。
     */
    public List<Token> parse(final String sql) {

        SqlFunctionTokenizer tokenizer = new SqlFunctionTokenizer(sql);

        List<Token> tokens = new ArrayList<>();
        while(TokenType.EOF != tokenizer.next()) {
            TokenType currentToken = tokenizer.getTokenType();
            if(currentToken == TokenType.SQL) {
                tokens.add(new Token(currentToken, tokenizer.getToken(), -1));

            } else if(currentToken == TokenType.LEFT_VARIABLE) {
                tokens.add(new Token(currentToken, tokenizer.getToken(), -1));

            } else if(currentToken == TokenType.BIND_VARIABLE) {
                int varIndex = tokenizer.getBindBariableNum() - 1;
                tokens.add(new Token(currentToken, tokenizer.getToken(), varIndex));

            } else {
                throw new IllegalArgumentException("unknown token type : " + currentToken);
            }
        }

        return tokens;
    }
}
