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
import com.github.mygreen.sqlmapper.core.test.entity.TypeValueEnumTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.TypeValueEnumTestEntity.EnumTest1Type;
import com.github.mygreen.sqlmapper.core.test.entity.TypeValueEnumTestEntity.EnumTest2Type;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MTypeValueEnumTestEntity;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class EnumTypeTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_type_value.sql");
    }

    @Test
    void testAutoSelect() {

        MTypeValueEnumTestEntity m_ = MTypeValueEnumTestEntity.testEnum;

        TypeValueEnumTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.enumOrdinalData.eq(EnumTest1Type.ONE).and(m_.enumNameData.eq(EnumTest2Type.RED)))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("enumOrdinalData", EnumTest1Type.ONE)
            .hasFieldOrPropertyWithValue("enumNameData", EnumTest2Type.RED);

    }

    @Test
    void testAutoInsert() {

        MTypeValueEnumTestEntity m_ = MTypeValueEnumTestEntity.testEnum;

        TypeValueEnumTestEntity entity = new TypeValueEnumTestEntity();
        entity.setId(10);
        entity.setEnumOrdinalData(EnumTest1Type.THREE);
        entity.setEnumNameData(EnumTest2Type.BLUE);
        entity.setComment("insert");

        int count = txNew().execute(action -> sqlMapper.insert(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValueEnumTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("enumOrdinalData", entity.getEnumOrdinalData())
            .hasFieldOrPropertyWithValue("enumNameData", entity.getEnumNameData());

    }

    @Test
    void testAutoUpdate() {

        MTypeValueEnumTestEntity m_ = MTypeValueEnumTestEntity.testEnum;

        TypeValueEnumTestEntity entity = sqlMapper.selectFrom(m_)
                .id(1L)
                .getSingleResult();

        entity.setEnumOrdinalData(EnumTest1Type.THREE);
        entity.setEnumNameData(EnumTest2Type.BLUE);

        int count = txNew().execute(action -> sqlMapper.update(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValueEnumTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("enumOrdinalData", entity.getEnumOrdinalData())
                .hasFieldOrPropertyWithValue("enumNameData", entity.getEnumNameData());

    }
}
