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
import com.github.mygreen.sqlmapper.core.test.entity.TypeValueIntegerNumberTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MTypeValueIntegerNumberTestEntity;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class IntegerNumberTypeTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_type_value.sql");
    }

    @Test
    void testAutoSelect_null() {

        MTypeValueIntegerNumberTestEntity m_ = MTypeValueIntegerNumberTestEntity.testIntegerNumber;

        TypeValueIntegerNumberTestEntity result = sqlMapper.selectFrom(m_)
                .id(0L)
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("shortData", null)
                .hasFieldOrPropertyWithValue("intData", null)
                .hasFieldOrPropertyWithValue("longData", null);

    }

    @Test
    void testAutoSelect_short() {

        MTypeValueIntegerNumberTestEntity m_ = MTypeValueIntegerNumberTestEntity.testIntegerNumber;

        TypeValueIntegerNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.shortData.eq((short)1))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("shortData", (short)1);
    }

    @Test
    void testAutoSelect_int() {

        MTypeValueIntegerNumberTestEntity m_ = MTypeValueIntegerNumberTestEntity.testIntegerNumber;

        TypeValueIntegerNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.intData.eq(20))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("intData", 20);
    }

    @Test
    void testAutoSelect_long() {

        MTypeValueIntegerNumberTestEntity m_ = MTypeValueIntegerNumberTestEntity.testIntegerNumber;

        TypeValueIntegerNumberTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.longData.eq(300L))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("longData", 300L);
    }

    @Test
    void testAutoInsert() {

        MTypeValueIntegerNumberTestEntity m_ = MTypeValueIntegerNumberTestEntity.testIntegerNumber;

        TypeValueIntegerNumberTestEntity entity = new TypeValueIntegerNumberTestEntity();
        entity.setId(10L);
        entity.setShortData((short)5);
        entity.setIntData(15);
        entity.setLongData(151L);
        entity.setComment("insert");

        int count = txNew().execute(action -> sqlMapper.insert(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValueIntegerNumberTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();
        assertThat(result).isEqualTo(entity);

    }

    @Test
    void testAutoUpdate() {

        MTypeValueIntegerNumberTestEntity m_ = MTypeValueIntegerNumberTestEntity.testIntegerNumber;

        TypeValueIntegerNumberTestEntity entity = sqlMapper.selectFrom(m_)
                .id(1l)
                .getSingleResult();
        entity.setShortData((short)5);
        entity.setIntData(15);
        entity.setLongData(151L);
        entity.setComment("update");

        int count = txNew().execute(action -> sqlMapper.update(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValueIntegerNumberTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();
        assertThat(result).isEqualTo(entity);

    }


}
