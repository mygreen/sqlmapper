package com.github.mygreen.sqlmapper.core.type;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.TypeValueJsr310DateTimeTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MTypeValueJsr310DateTimeTestEntity;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class JSR310DateTimeTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_type_value.sql");
    }

    @Test
    void testAutoSelect_date() {

        MTypeValueJsr310DateTimeTestEntity m_ = MTypeValueJsr310DateTimeTestEntity.testJsr310DateTime;

        TypeValueJsr310DateTimeTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.dateData.eq(LocalDate.of(2021, 01, 11)))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 1l)
                .hasFieldOrPropertyWithValue("dateData", LocalDate.of(2021, 01, 11));

    }

    @Test
    void testAutoSelect_time() {

        MTypeValueJsr310DateTimeTestEntity m_ = MTypeValueJsr310DateTimeTestEntity.testJsr310DateTime;

        TypeValueJsr310DateTimeTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.timeData.eq(LocalTime.of(02, 04, 05)))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 2l)
                .hasFieldOrPropertyWithValue("timeData", LocalTime.of(02, 04, 05));

    }

    @Test
    void testAutoSelect_timestamp() {

        MTypeValueJsr310DateTimeTestEntity m_ = MTypeValueJsr310DateTimeTestEntity.testJsr310DateTime;

        TypeValueJsr310DateTimeTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.timestampData.eq(LocalDateTime.of(2021, 03, 13, 03, 04, 05)))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 3l)
                .hasFieldOrPropertyWithValue("timestampData", LocalDateTime.of(2021, 03, 13, 03, 04, 05));

    }

    @Test
    void testAutoInsert() {

        MTypeValueJsr310DateTimeTestEntity m_ = MTypeValueJsr310DateTimeTestEntity.testJsr310DateTime;

        TypeValueJsr310DateTimeTestEntity entity = new TypeValueJsr310DateTimeTestEntity();
        entity.setId(10);
        entity.setDateData(LocalDate.of(2010, 01, 01));
        entity.setTimeData(LocalTime.of(00, 12, 01));
        entity.setTimestampData(LocalDateTime.of(2010, 01, 01, 00, 12, 01));
        entity.setComment("insert");

        int count = txNew().execute(action -> sqlMapper.insert(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValueJsr310DateTimeTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).isEqualTo(entity);
    }

    @Test
    void testAutoUpdate() {


        MTypeValueJsr310DateTimeTestEntity m_ = MTypeValueJsr310DateTimeTestEntity.testJsr310DateTime;

        TypeValueJsr310DateTimeTestEntity entity = sqlMapper.selectFrom(m_)
                .id(1l)
                .getSingleResult();

        entity.setDateData(LocalDate.of(2010, 01, 01));
        entity.setTimeData(LocalTime.of(00, 12, 01));
        entity.setTimestampData(LocalDateTime.of(2010, 01, 01, 00, 12, 01));
        entity.setComment("update");

        int count = txNew().execute(action -> sqlMapper.update(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValueJsr310DateTimeTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).isEqualTo(entity);

    }



}
