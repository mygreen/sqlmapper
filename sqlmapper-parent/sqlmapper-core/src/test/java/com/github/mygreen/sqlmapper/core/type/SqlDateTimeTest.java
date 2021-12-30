package com.github.mygreen.sqlmapper.core.type;

import static org.assertj.core.api.Assertions.*;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.TypeValueSqlDateTimeTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MTypeValueSqlDateTimeTestEntity;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class SqlDateTimeTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_type_value.sql");
    }

    @Test
    void testAutoSelect_date() {

        MTypeValueSqlDateTimeTestEntity m_ = MTypeValueSqlDateTimeTestEntity.testSqlDateTime;

        TypeValueSqlDateTimeTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.dateData.eq(Date.valueOf("2021-01-11")))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 1l)
                .hasFieldOrPropertyWithValue("dateData", Date.valueOf("2021-01-11"));

    }

    @Test
    void testAutoSelect_time() {

        MTypeValueSqlDateTimeTestEntity m_ = MTypeValueSqlDateTimeTestEntity.testSqlDateTime;

        TypeValueSqlDateTimeTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.timeData.eq(Time.valueOf("02:04:05")))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 2l)
                .hasFieldOrPropertyWithValue("timeData", Time.valueOf("02:04:05"));

    }

    @Test
    void testAutoSelect_timestamp() {

        MTypeValueSqlDateTimeTestEntity m_ = MTypeValueSqlDateTimeTestEntity.testSqlDateTime;

        TypeValueSqlDateTimeTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.timestampData.eq(Timestamp.valueOf("2021-03-13 03:04:05")))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 3l)
                .hasFieldOrPropertyWithValue("timestampData", Timestamp.valueOf("2021-03-13 03:04:05"));

    }

    @Test
    void testAutoInsert() {

        MTypeValueSqlDateTimeTestEntity m_ = MTypeValueSqlDateTimeTestEntity.testSqlDateTime;

        TypeValueSqlDateTimeTestEntity entity = new TypeValueSqlDateTimeTestEntity();
        entity.setId(10);
        entity.setDateData(Date.valueOf("2010-01-01"));
        entity.setTimeData(Time.valueOf("00:12:01"));
        entity.setTimestampData(Timestamp.valueOf("2010-01-01 00:12:01"));
        entity.setComment("insert");

        int count = txNew().execute(action -> sqlMapper.insert(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValueSqlDateTimeTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).isEqualTo(entity);
    }

    @Test
    void testAutoUpdate() {


        MTypeValueSqlDateTimeTestEntity m_ = MTypeValueSqlDateTimeTestEntity.testSqlDateTime;

        TypeValueSqlDateTimeTestEntity entity = sqlMapper.selectFrom(m_)
                .id(1l)
                .getSingleResult();

        entity.setDateData(Date.valueOf("2010-01-01"));
        entity.setTimeData(Time.valueOf("00:12:01"));
        entity.setTimestampData(Timestamp.valueOf("2010-01-01 00:12:01"));
        entity.setComment("update");

        int count = txNew().execute(action -> sqlMapper.update(entity).execute());
        assertThat(count).isEqualTo(1);

        TypeValueSqlDateTimeTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).isEqualTo(entity);

    }

}
