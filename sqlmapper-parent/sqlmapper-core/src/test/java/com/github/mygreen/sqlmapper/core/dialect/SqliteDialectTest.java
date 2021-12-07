package com.github.mygreen.sqlmapper.core.dialect;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;


public class SqliteDialectTest extends SqliteDialect {

    @Test
    void testGetName() {
        assertThat(getName()).isEqualTo("sqlite");
    }

    @Test
    void testSupportsGenerationType() {
        assertThat(supportsGenerationType(GenerationType.IDENTITY)).isTrue();
        assertThat(supportsGenerationType(GenerationType.SEQUENCE)).isFalse();
        assertThat(supportsGenerationType(GenerationType.TABLE)).isTrue();
        assertThat(supportsGenerationType(GenerationType.UUID)).isTrue();
    }

    @Test
    void testGetSequenceIncrementer() {
        assertThat(getSequenceIncrementer(new SimpleDriverDataSource(), "test")).isNull();
    }
}
