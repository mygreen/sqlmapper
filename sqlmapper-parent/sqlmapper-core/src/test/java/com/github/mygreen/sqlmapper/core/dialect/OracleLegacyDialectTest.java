package com.github.mygreen.sqlmapper.core.dialect;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;


public class OracleLegacyDialectTest extends OracleLegacyDialect {

    @Test
    void testGetName() {
        assertThat(getName()).isEqualTo("oracle");
    }

    @Test
    void testSupportsGenerationType() {
        assertThat(supportsGenerationType(GenerationType.IDENTITY)).isFalse();
        assertThat(supportsGenerationType(GenerationType.SEQUENCE)).isTrue();
        assertThat(supportsGenerationType(GenerationType.TABLE)).isTrue();
        assertThat(supportsGenerationType(GenerationType.UUID)).isTrue();
    }

    @Test
    void testConvertLimitSql() {
        String select = "select * from sample";
        assertThatThrownBy(() -> convertLimitSql(select, -1, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(convertLimitSql(select, -1, 5)).isEqualTo("select * from ( select temp_.*, rownum rownumber_ from ( select * from sample ) temp_ ) where rownumber_ <= 5");
        assertThat(convertLimitSql(select, 10, -1)).isEqualTo("select * from ( select temp_.*, rownum rownumber_ from ( select * from sample ) temp_ ) where rownumber_ >= 11");
        assertThat(convertLimitSql(select, 10, 5)).isEqualTo("select * from ( select temp_.*, rownum rownumber_ from ( select * from sample ) temp_ ) where rownumber_ between 11 and 15");

    }
}
