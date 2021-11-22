package com.github.mygreen.sqlmapper.metamodel.support;

/**
 * SQL関数の字句解析処理。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class SqlFunctionTokenizer {

    /**
     * トークンの種類
     *
     */
    public enum TokenType {
        SQL,
        LEFT_VARIABLE,
        BIND_VARIABLE,
        EOF
    }

    /**
     * 解析対象のSQL
     */
    private String sql;

    /**
     * 現在解析しているポジション
     */
    private int position = 0;

    /**
     * トークン
     */
    private String token;

    /**
     * 現在のトークン種別
     */
    private TokenType tokenType = TokenType.SQL;

    /**
     * 次のトークン種別
     */
    private TokenType nextTokenType = TokenType.SQL;

    /**
     * 現在まで出現したバイド変数の個数
     */
    private int bindVariableNum = 0;

    public SqlFunctionTokenizer(String sql) {
        this.sql = sql;
    }

    /**
     * @return SQLを返します。
     */
    public String getSql() {
        return sql;
    }

    /**
     * @return 現在解析しているポジションを返します。
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return トークンを返します。
     */
    public String getToken() {
        return token;
    }

    /**
     * @return 現在解析しているポジションより前のSQLを返します。
     */
    public String getBefore() {
        return sql.substring(0, position);
    }

    /**
     * @return 現在解析しているポジションより後ろのSQLを返します。
     */
    public String getAfter() {
        return sql.substring(position);
    }

    /**
     * @return 現在のトークン種別を返します。
     */
    public TokenType getTokenType() {
        return tokenType;
    }

    /**
     * @return 次のトークン種別を返します。
     */
    public TokenType getNextTokenType() {
        return nextTokenType;
    }


    /**
     * @return 次のトークンに進みます。
     */
    public TokenType next() {
        if (position >= sql.length()) {
            token = null;
            tokenType = TokenType.EOF;
            nextTokenType = TokenType.EOF;
            return tokenType;
        }
        switch (nextTokenType) {
        case SQL:
            parseSql();
            break;
        case LEFT_VARIABLE:
            parseLeftVariable();
            break;
        case BIND_VARIABLE:
            parseBindVariable();
            break;
        default:
            parseEof();
            break;
        }
        return tokenType;
    }

    /**
     * Parse the SQL.
     */
    protected void parseSql() {

        int leftVariableStartPos = sql.indexOf("$left", position);
        int bindVariableStartPos = sql.indexOf("?", position);
        int nextStartPos = getNextStartPos(leftVariableStartPos, bindVariableStartPos);

        if (nextStartPos < 0) {
            // 特定のトークンでない場合は、すべてSQL区分する。
            token = sql.substring(position);
            nextTokenType = TokenType.EOF;
            position = sql.length();
            tokenType = TokenType.SQL;

        } else {
            token = sql.substring(position, nextStartPos);
            tokenType = TokenType.SQL;
            boolean needNext = nextStartPos == position;

            if (nextStartPos == leftVariableStartPos) {
                nextTokenType = TokenType.LEFT_VARIABLE;
                position = leftVariableStartPos + 4;

            } else if (nextStartPos == bindVariableStartPos) {
                nextTokenType = TokenType.BIND_VARIABLE;
                position = bindVariableStartPos;
            }

            if (needNext) {
                next();
            }
        }
    }

    /**
     * 次のトークンの開始位置を返します。
     *
     * @param leftVariableStartPos {@literal $left} 変数の開始位置
     * @param bindVariableStartPos {@literal ?} 変数の開始位置
     * @return 次のトークンの開始位置。特定のトークンでない場合は {@literal -1} を返します。
     */
    protected int getNextStartPos(int leftVariableStartPos, int bindVariableStartPos) {

        int nextStartPos = -1;
        if (leftVariableStartPos >= 0) {
            nextStartPos = leftVariableStartPos;
        }

        if (bindVariableStartPos >= 0
                && (nextStartPos < 0 || bindVariableStartPos < nextStartPos)) {
            nextStartPos = bindVariableStartPos;
        }
        return nextStartPos;
    }

    /**
     * {@literal $left} 変数をパースします。
     */
    protected void parseLeftVariable() {
        token = "$left";
        nextTokenType = TokenType.SQL;
        position += 1;
        tokenType = TokenType.LEFT_VARIABLE;
    }

    /**
     * Parse the bind variable.
     */
    protected void parseBindVariable() {
        token = "?";
        bindVariableNum++;
        nextTokenType = TokenType.SQL;
        position += 1;
        tokenType = TokenType.BIND_VARIABLE;
    }

    /**
     * Parse the end of the SQL.
     */
    protected void parseEof() {
        token = null;
        tokenType = TokenType.EOF;
        nextTokenType = TokenType.EOF;
    }

    /**
     * バインド変数の現在までの出現回数を取得します。
     * @return バインド変数の現在までの出現回
     */
    public int getBindBariableNum() {
        return bindVariableNum;
    }

    /**
     * トークンをスキップします。
     *
     * @return スキップしたトークン
     */
    public String skipToken() {
        int index = sql.length();
        char quote = position < sql.length() ? sql.charAt(position) : '\0';
        boolean quoting = quote == '\'' || quote == '(';
        if (quote == '(') {
            quote = ')';
        }
        for (int i = quoting ? position + 1 : position; i < sql.length(); ++i) {
            char c = sql.charAt(i);
            if ((Character.isWhitespace(c) || c == ',' || c == ')' || c == '(')
                    && !quoting) {
                index = i;
                break;
            } else if (c == '/' && i + 1 < sql.length()
                    && sql.charAt(i + 1) == '*') {
                index = i;
                break;
            } else if (c == '-' && i + 1 < sql.length()
                    && sql.charAt(i + 1) == '-') {
                index = i;
                break;
            } else if (quoting && quote == '\'' && c == '\''
                    && (i + 1 >= sql.length() || sql.charAt(i + 1) != '\'')) {
                index = i + 1;
                break;
            } else if (quoting && c == quote) {
                index = i + 1;
                break;
            }
        }
        token = sql.substring(position, index);
        tokenType = TokenType.SQL;
        nextTokenType = TokenType.SQL;
        position = index;
        return token;
    }

    /**
     * ホワイトスペースをスキップします。
     *
     * @return スキップしたホワイストスペース
     */
    public String skipWhitespace() {
        int index = skipWhitespace(position);
        token = sql.substring(position, index);
        position = index;
        return token;
    }

    private int skipWhitespace(int position) {
        int index = sql.length();
        for (int i = position; i < sql.length(); ++i) {
            char c = sql.charAt(i);
            if (!Character.isWhitespace(c)) {
                index = i;
                break;
            }
        }
        return index;
    }

}
