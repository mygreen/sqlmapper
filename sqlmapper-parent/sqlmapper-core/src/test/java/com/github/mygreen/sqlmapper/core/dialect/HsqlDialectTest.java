package com.github.mygreen.sqlmapper.core.dialect;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;


public class HsqlDialectTest extends HsqlDialect {

    @Test
    void testGetName() {
        assertThat(getName()).isEqualTo("hsql");
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
}
