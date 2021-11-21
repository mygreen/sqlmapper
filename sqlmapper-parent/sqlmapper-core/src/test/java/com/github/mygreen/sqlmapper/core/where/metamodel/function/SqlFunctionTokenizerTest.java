package com.github.mygreen.sqlmapper.core.where.metamodel.function;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.mygreen.sqlmapper.core.where.metamodel.function.SqlFunctionTokenizer.TokenType;


public class SqlFunctionTokenizerTest {

    @Test
    void testParse() {
        SqlFunctionTokenizer tokenizer = new SqlFunctionTokenizer("custom_test($this, ?)");

        {
            TokenType type = tokenizer.next();
            assertThat(type).isEqualTo(TokenType.SQL);
            String sql = tokenizer.getToken();
            assertThat(sql).isEqualTo("custom_test(");
        }

        {
            TokenType type = tokenizer.next();
            assertThat(type).isEqualTo(TokenType.THIS_VARIABLE);
            String sql = tokenizer.getToken();
            assertThat(sql).isEqualTo("$this");
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
