package com.github.mygreen.sqlmapper.core.dialect;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.query.SelectForUpdateType;


public class OracleDialectTest extends OracleDialect {

    @Test
    void testGetName() {
        assertThat(getName()).isEqualTo("oracle");
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
    void testGetHintComment() {
        assertThat(getHintComment("TEST")).isEqualTo("/*+ TEST */ ");
    }

    @Test
    void testSupportsSelectForUpdate() {
        assertThat(supportsSelectForUpdate(SelectForUpdateType.NORMAL)).isTrue();
        assertThat(supportsSelectForUpdate(SelectForUpdateType.NOWAIT)).isTrue();
        assertThat(supportsSelectForUpdate(SelectForUpdateType.WAIT)).isTrue();
    }

    @Test
    void testGetForUpdateSql() {
        assertThat(getForUpdateSql(SelectForUpdateType.NORMAL, 10)).isEqualTo(" for update");
        assertThat(getForUpdateSql(SelectForUpdateType.NOWAIT, 10)).isEqualTo(" for update nowait");
        assertThat(getForUpdateSql(SelectForUpdateType.WAIT, 10)).isEqualTo(" for update wait 10");
    }

    @Test
    void testConvertLimitSql() {
        String select = "select * from sample";
        assertThatThrownBy(() -> convertLimitSql(select, -1, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(convertLimitSql(select, -1, 5)).isEqualTo("select * from sample fetch first 5 rows only");
        assertThat(convertLimitSql(select, 10, -1)).isEqualTo("select * from sample offset 10 rows");
        assertThat(convertLimitSql(select, 10, 5)).isEqualTo("select * from sample offset 10 rows fetch first 5 rows only");
    }

    @Test
    void testNeedsParameterForResultSet() {
        assertThat(needsParameterForResultSet()).isTrue();
    }

}
