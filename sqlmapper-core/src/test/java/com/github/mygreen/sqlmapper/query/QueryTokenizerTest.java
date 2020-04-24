package com.github.mygreen.sqlmapper.query;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mygreen.sqlmapper.query.QueryTokenizer;

/**
 * {@link QueryTokenizer}のテスタ
 *
 *
 * @author higa
 * @author T.TSUCHIE
 *
 */
public class QueryTokenizerTest {

    @Test
    public void testNextToken() {
        QueryTokenizer tokenizer = new QueryTokenizer("aaa=?");
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_PARAM, tokenizer.nextToken());
        assertEquals("?", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("先頭がパラメータ")
    @Test
    public void testNextToken_firstParam() {
        QueryTokenizer tokenizer = new QueryTokenizer("?=aaa");
        assertEquals(QueryTokenizer.TT_PARAM, tokenizer.nextToken());
        assertEquals("?", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("先頭が空白")
    @Test
    public void testNextToken_firstBlank() {
        QueryTokenizer tokenizer = new QueryTokenizer(" aaa=?");
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals(" ", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_PARAM, tokenizer.nextToken());
        assertEquals("?", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("シングルクオート")
    @Test
    public void testNextToken_singleQuote() {
        QueryTokenizer tokenizer = new QueryTokenizer("aaa='xxx' and bbb='yyy'");
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_QUOTE, tokenizer.nextToken());
        assertEquals("'xxx'", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals(" ", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("and", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals(" ", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("bbb", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_QUOTE, tokenizer.nextToken());
        assertEquals("'yyy'", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("先頭がシングルクオート")
    @Test
    public void testNextToken_singleQuote_first() {
        QueryTokenizer tokenizer = new QueryTokenizer("'xxx'='xxx'");
        assertEquals(QueryTokenizer.TT_QUOTE, tokenizer.nextToken());
        assertEquals("'xxx'", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_QUOTE, tokenizer.nextToken());
        assertEquals("'xxx'", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("文字列の中に演算子")
    @Test
    public void testNextToken_singleQuote_containsOperation() {
        QueryTokenizer tokenizer = new QueryTokenizer("aaa='x=x'");
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_QUOTE, tokenizer.nextToken());
        assertEquals("'x=x'", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("文字列のみ")
    @Test
    public void testNextToken_word() {
        QueryTokenizer tokenizer = new QueryTokenizer("aaa");
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("カンマ区切り")
    @Test
    public void testNextToken_comma() {
        QueryTokenizer tokenizer = new QueryTokenizer("aaa, bbb");
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals(", ", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("bbb", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("パラメータがカンマ区切り")
    @Test
    public void testNextToken_comma_param() {
        QueryTokenizer tokenizer = new QueryTokenizer("?, ?");
        assertEquals(QueryTokenizer.TT_PARAM, tokenizer.nextToken());
        assertEquals("?", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals(", ", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_PARAM, tokenizer.nextToken());
        assertEquals("?", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("名前付き変数")
    @Test
    public void testNextToken_namedParam() {
        QueryTokenizer tokenizer = new QueryTokenizer("aaa=:bbb");
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_NAMED_PARAM, tokenizer.nextToken());
        assertEquals(":bbb", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("名前付き変数が先頭")
    @Test
    public void testNextToken_firstNamedParam() {
        QueryTokenizer tokenizer = new QueryTokenizer(":bbb=aaa");
        assertEquals(QueryTokenizer.TT_NAMED_PARAM, tokenizer.nextToken());
        assertEquals(":bbb", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("名前付き変数が複数")
    @Test
    public void testNextToken_namedParam_multi() {
        QueryTokenizer tokenizer = new QueryTokenizer("aaa=:arg1 and bbb = :arg2");
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_NAMED_PARAM, tokenizer.nextToken());
        assertEquals(":arg1", tokenizer.getToken());

        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals(" ", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("and", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals(" ", tokenizer.getToken());

        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("bbb", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals(" = ", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_NAMED_PARAM, tokenizer.nextToken());
        assertEquals(":arg2", tokenizer.getToken());

        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

    @DisplayName("名前付き変数でない場合")
    @Test
    public void testNextToken_not_namedParam() {
        QueryTokenizer tokenizer = new QueryTokenizer("aaa=::int");
        assertEquals(QueryTokenizer.TT_WORD, tokenizer.nextToken());
        assertEquals("aaa", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_OTHER, tokenizer.nextToken());
        assertEquals("=:", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_NAMED_PARAM, tokenizer.nextToken());
        assertEquals(":int", tokenizer.getToken());
        assertEquals(QueryTokenizer.TT_EOF, tokenizer.nextToken());
    }

}
