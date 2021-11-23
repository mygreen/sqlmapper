package com.github.mygreen.sqlmapper.core.dialect;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;


public class H2DialectTest extends H2Dialect {

    @Test
    void testGetName() {
        assertThat(getName()).isEqualTo("h2");
    }

    @Test
    void testSupportsGenerationType() {
        assertThat(supportsGenerationType(GenerationType.IDENTITY)).isTrue();
        assertThat(supportsGenerationType(GenerationType.SEQUENCE)).isTrue();
        assertThat(supportsGenerationType(GenerationType.TABLE)).isTrue();
        assertThat(supportsGenerationType(GenerationType.UUID)).isTrue();
    }

    @Test
    void testGetSequenceIncrementer() {
        assertThat(getSequenceIncrementer(new SimpleDriverDataSource(), "test")).isNotNull();
    }

    @Test
    void testConvertLimitSql() {
        String select = "select * from sample";
        assertThatThrownBy(() -> convertLimitSql(select, -1, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(convertLimitSql(select, -1, 5)).isEqualTo("select * from sample limit 5");
        assertThat(convertLimitSql(select, 10, -1)).isEqualTo("select * from sample offset 10");
        assertThat(convertLimitSql(select, 10, 5)).isEqualTo("select * from sample limit 5 offset 10");
    }

    @Test
    void testNeedsParameterForResultSet() {
        assertThat(needsParameterForResultSet()).isFalse();
    }
}
