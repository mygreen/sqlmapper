package com.github.mygreen.sqlmapper.metamodel.support;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.mygreen.sqlmapper.metamodel.support.SqlFunctionTokenizer.TokenType;


public class SqlFunctionTokenizerTest {

    @Test
    void testParse() {
        SqlFunctionTokenizer tokenizer = new SqlFunctionTokenizer("custom_test($left, ?)");

        {
            TokenType type = tokenizer.next();
            assertThat(type).isEqualTo(TokenType.SQL);
            String sql = tokenizer.getToken();
            assertThat(sql).isEqualTo("custom_test(");
        }

        {
            TokenType type = tokenizer.next();
            assertThat(type).isEqualTo(TokenType.LEFT_VARIABLE);
            String sql = tokenizer.getToken();
            assertThat(sql).isEqualTo("$left");
        }

        {
            TokenType type = tokenizer.next();
            assertThat(type).isEqualTo(TokenType.SQL);
            String sql = tokenizer.getToken();
            assertThat(sql).isEqualTo(", ");
        }

        {
            TokenType type = tokenizer.next();
            assertThat(type).isEqualTo(TokenType.BIND_VARIABLE);
            String sql = tokenizer.getToken();
            assertThat(sql).isEqualTo("?");
            assertThat(tokenizer.getBindBariableNum()).isEqualTo(1);
        }

        {
            TokenType type = tokenizer.next();
            assertThat(type).isEqualTo(TokenType.SQL);
            String sql = tokenizer.getToken();
            assertThat(sql).isEqualTo(")");
        }

    }
}
