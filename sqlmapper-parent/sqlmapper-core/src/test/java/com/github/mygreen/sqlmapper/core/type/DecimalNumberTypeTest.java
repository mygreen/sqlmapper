package com.github.mygreen.sqlmapper.core.type;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.TypeValueDecimalNumberTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MTypeValueDecimalNumberTestEntity;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class DecimalNumberTypeTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_type_value.sql");
    }

    @Test
    void testAutoSelect_null() {

        MTypeValueDecimalNumberTestEntity m_ = MTypeValueDecimalNumberTestEntity.testDecimalNumber;

        TypeValueDecimalNumberTestEntity result = sqlMapper.selectFrom(m_)
                .id(0L)
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("floatData", null)
                .hasFieldOrPropertyWithValue("doubleData", null)
                .hasFieldOrPropertyWithValue("bigdecimalData", null);

    }

    @Test
    void testAutoSelect_short() {

        MTypeValueDecimalNumberTestEntity m_ = MTypeValueDecimalNumberTestEntity.testDecimalNumber;

        TypeValueDecimalNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.floatData.eq(1.1f))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("floatData", 1.1f);
    }

    @Test
    void testAutoSelect_int() {

        MTypeValueDecimalNumberTestEntity m_ = MTypeValueDecimalNumberTestEntity.testDecimalNumber;

        TypeValueDecimalNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.doubleData.eq(20.22d))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("doubleData", 20.22d);
    }

    @Test
    void testAutoSelect_long() {

        MTypeValueDecimalNumberTestEntity m_ = MTypeValueDecimalNumberTestEntity.testDecimalNumber;

        TypeValueDecimalNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.bigdecimalData.eq(new BigDecimal("300.333")))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("bigdecimalData", new BigDecimal("300.333"));
    }

    @Test
    void testAutoInsert() {

        MTypeValueDecimalNumberTestEntity m_ = MTypeValueDecimalNumberTestEntity.testDecimalNumber;

        TypeValueDecimalNumberTestEntity entity = new TypeValueDecimalNumberTestEntity();
        entity.setId(10L);
        entity.setFloatData(5.5f);
        entity.setDoubleData(15.15d);
        entity.setBigdecimalData(new BigDecimal("151.151"));
        entity.setComment("insert");

        int count = txNew().execute(action -> sqlMapper.insert(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValueDecimalNumberTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();
        assertThat(result).isEqualTo(entity);

    }

    @Test
    void testAutoUpdate() {

        MTypeValueDecimalNumberTestEntity m_ = MTypeValueDecimalNumberTestEntity.testDecimalNumber;

        TypeValueDecimalNumberTestEntity entity = sqlMapper.selectFrom(m_)
                .id(1l)
                .getSingleResult();
        entity.setFloatData(5.5f);
        entity.setDoubleData(15.15d);
        entity.setBigdecimalData(new BigDecimal("151.151"));
        entity.setComment("update");

        int count = txNew().execute(action -> sqlMapper.update(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValueDecimalNumberTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();
        assertThat(result).isEqualTo(entity);

    }


}
