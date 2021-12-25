package com.github.mygreen.sqlmapper.core.type;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.TypeValuePrimitiveNumberTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MTypeValuePrimitiveNumberTestEntity;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class PrimitiveNumberTypeTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_type_value.sql");
    }

    @Test
    void testAutoSelect_null() {

        MTypeValuePrimitiveNumberTestEntity m_ = MTypeValuePrimitiveNumberTestEntity.testPrimitiveNumber;

        TypeValuePrimitiveNumberTestEntity result = sqlMapper.selectFrom(m_)
                .id(0L)
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("shortData", (short)0)
                .hasFieldOrPropertyWithValue("intData", 0)
                .hasFieldOrPropertyWithValue("longData", 0l)
                .hasFieldOrPropertyWithValue("floatData", 0.0f)
                .hasFieldOrPropertyWithValue("doubleData", 0.0d);

    }

    @Test
    void testAutoSelect_short() {

        MTypeValuePrimitiveNumberTestEntity m_ = MTypeValuePrimitiveNumberTestEntity.testPrimitiveNumber;

        TypeValuePrimitiveNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.shortData.eq((short)1))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("shortData", (short)1);
    }

    @Test
    void testAutoSelect_int() {

        MTypeValuePrimitiveNumberTestEntity m_ = MTypeValuePrimitiveNumberTestEntity.testPrimitiveNumber;

        TypeValuePrimitiveNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.intData.eq(20))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("intData", 20);
    }

    @Test
    void testAutoSelect_long() {

        MTypeValuePrimitiveNumberTestEntity m_ = MTypeValuePrimitiveNumberTestEntity.testPrimitiveNumber;

        TypeValuePrimitiveNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.longData.eq(300L))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("longData", 300L);
    }

    @Test
    void testAutoSelect_float() {

        MTypeValuePrimitiveNumberTestEntity m_ = MTypeValuePrimitiveNumberTestEntity.testPrimitiveNumber;

        TypeValuePrimitiveNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.floatData.eq(4.4f))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 4L)
                .hasFieldOrPropertyWithValue("floatData", 4.4f);
    }

    @Test
    void testAutoSelect_double() {

        MTypeValuePrimitiveNumberTestEntity m_ = MTypeValuePrimitiveNumberTestEntity.testPrimitiveNumber;

        TypeValuePrimitiveNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.doubleData.eq(50.55d))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 5L)
                .hasFieldOrPropertyWithValue("doubleData", 50.55d);
    }

    @Test
    void testInsert() {

        MTypeValuePrimitiveNumberTestEntity m_ = MTypeValuePrimitiveNumberTestEntity.testPrimitiveNumber;

        TypeValuePrimitiveNumberTestEntity entity = new TypeValuePrimitiveNumberTestEntity();
        entity.setId(10L);
        entity.setShortData((short)5);
        entity.setIntData(15);
        entity.setLongData(151L);
        entity.setFloatData(5.5f);
        entity.setDoubleData(15.15d);
        entity.setComment("insert");

        int count = txNew().execute(action -> sqlMapper.insert(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValuePrimitiveNumberTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();
        assertThat(result).isEqualTo(entity);

    }

    @Test
    void testUpdate() {

        MTypeValuePrimitiveNumberTestEntity m_ = MTypeValuePrimitiveNumberTestEntity.testPrimitiveNumber;

        TypeValuePrimitiveNumberTestEntity entity = sqlMapper.selectFrom(m_)
                .id(1l)
                .getSingleResult();
        entity.setShortData((short)5);
        entity.setIntData(15);
        entity.setLongData(151L);
        entity.setFloatData(5.5f);
        entity.setDoubleData(15.15d);
        entity.setComment("update");

        int count = txNew().execute(action -> sqlMapper.update(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValuePrimitiveNumberTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();
        assertThat(result).isEqualTo(entity);

    }


}
