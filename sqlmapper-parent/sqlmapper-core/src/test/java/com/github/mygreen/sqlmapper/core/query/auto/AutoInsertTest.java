package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueIdentity2TestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueIdentityTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueSequenceFormatTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueSequenceTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueTableFormatTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueTableTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueUUIDTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MCustomer;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueIdentity2TestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueIdentityTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueSequenceFormatTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueSequenceTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueTableFormatTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueTableTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueUUIDTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.type.GenderType;


/**
 * {@link AutoInsert} のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoInsertTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
    }

    @Test
    void testInsert() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = new Customer();
        entity.setId("test@001");
        entity.setFirstName("Taro");
        entity.setLastName("Yamada");
        entity.setBirthday(LocalDate.of(2010, 10, 1));
        entity.setGenderType(GenderType.MALE);

        int count = sqlMapper.insert(entity)
            .execute();

        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", "test@001")
            .hasFieldOrPropertyWithValue("firstName", "Taro")
            .hasFieldOrPropertyWithValue("lastName", "Yamada")
            .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, 1))
            .hasFieldOrPropertyWithValue("genderType", GenderType.MALE)
            .hasFieldOrPropertyWithValue("version", 0l);

    }

    @Test
    void testInclude() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = new Customer();
        entity.setId("test@001");
        entity.setFirstName("Taro");
        entity.setLastName("Yamada");
        entity.setBirthday(LocalDate.of(2010, 10, 1));
        entity.setGenderType(GenderType.MALE);

        int count = sqlMapper.insert(entity)
                .includes(m_.firstName, m_.lastName, m_.birthday)
            .execute();

        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", "test@001")
            .hasFieldOrPropertyWithValue("firstName", "Taro")
            .hasFieldOrPropertyWithValue("lastName", "Yamada")
            .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, 1))
            .hasFieldOrPropertyWithValue("genderType", null)    // 挿入対象外
            .hasFieldOrPropertyWithValue("version", 0l);

    }

    @Test
    void testExclude() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = new Customer();
        entity.setId("test@001");
        entity.setFirstName("Taro");
        entity.setLastName("Yamada");
        entity.setBirthday(LocalDate.of(2010, 10, 1));
        entity.setGenderType(GenderType.MALE);

        int count = sqlMapper.insert(entity)
                .excludes(m_.genderType)
            .execute();

        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", "test@001")
            .hasFieldOrPropertyWithValue("firstName", "Taro")
            .hasFieldOrPropertyWithValue("lastName", "Yamada")
            .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, 1))
            .hasFieldOrPropertyWithValue("genderType", null)    // 挿入対象外
            .hasFieldOrPropertyWithValue("version", 0l);

    }

    @Test
    void testIncludeAndExclude() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = new Customer();
        entity.setId("test@001");
        entity.setFirstName("Taro");
        entity.setLastName("Yamada");
        entity.setBirthday(LocalDate.of(2010, 10, 1));
        entity.setGenderType(GenderType.MALE);

        int count = sqlMapper.insert(entity)
                .includes(m_.firstName, m_.lastName, m_.birthday)
                .excludes(m_.birthday)
            .execute();

        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", "test@001")
            .hasFieldOrPropertyWithValue("firstName", "Taro")
            .hasFieldOrPropertyWithValue("lastName", "Yamada")
            .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, 1))
            .hasFieldOrPropertyWithValue("genderType", null)    // 挿入対象外
            .hasFieldOrPropertyWithValue("version", 0l);

    }

    @Test
    void testGenerateValue_identity() {

        MGeneratedValueIdentityTestEntity m_ = MGeneratedValueIdentityTestEntity.testIdentity;

        for(int i=0; i < 5; i++) {
            GeneratedValueIdentityTestEntity entity = new GeneratedValueIdentityTestEntity();
            entity.setComment("test-identity" + i);

            int execCount = sqlMapper.insert(entity)
                .execute();

            assertThat(execCount).isEqualTo(1);

            GeneratedValueIdentityTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entity.getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", (long)(i+1))
                .hasFieldOrPropertyWithValue("comment", "test-identity" + i);
        }

    }

    @Test
    void testGenerateValue_identity2() {

        MGeneratedValueIdentity2TestEntity m_ = MGeneratedValueIdentity2TestEntity.testIdentity2;

        for(int i=0; i < 5; i++) {
            GeneratedValueIdentity2TestEntity entity = new GeneratedValueIdentity2TestEntity();
            entity.setComment("test-identity2" + i);

            int execCount = sqlMapper.insert(entity)
                .execute();

            assertThat(execCount).isEqualTo(1);

            GeneratedValueIdentity2TestEntity result = sqlMapper.selectFrom(m_)
                    .id(entity.getId1(), entity.getId2())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id1", (long)(i+1))
                .hasFieldOrPropertyWithValue("id2", 100+i)
                .hasFieldOrPropertyWithValue("comment", "test-identity2" + i);
        }

    }


    @Test
    void testGenerateValue_sequence() {

        MGeneratedValueSequenceTestEntity m_ = MGeneratedValueSequenceTestEntity.testSequence;

        for(int i=0; i < 5; i++) {
            GeneratedValueSequenceTestEntity entity = new GeneratedValueSequenceTestEntity();
            entity.setComment("test-sequence" + i);

            int execCount = sqlMapper.insert(entity)
                .execute();

            assertThat(execCount).isEqualTo(1);

            GeneratedValueSequenceTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entity.getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", (long)(i+1))
                .hasFieldOrPropertyWithValue("comment", "test-sequence" + i);
        }

    }

    @Test
    void testGenerateValue_sequence_format() {

        MGeneratedValueSequenceFormatTestEntity m_ = MGeneratedValueSequenceFormatTestEntity.testSequenceFormat;

        for(int i=0; i < 5; i++) {
            GeneratedValueSequenceFormatTestEntity entity = new GeneratedValueSequenceFormatTestEntity();
            entity.setComment("test-sequence-format" + i);

            int execCount = sqlMapper.insert(entity)
                .execute();

            assertThat(execCount).isEqualTo(1);

            GeneratedValueSequenceFormatTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entity.getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", "0000000" + (i+1))
                .hasFieldOrPropertyWithValue("comment", "test-sequence-format" + i);
        }

    }

    @Test
    void testGenerateValue_table() {

        MGeneratedValueTableTestEntity m_ = MGeneratedValueTableTestEntity.testTable;

        for(int i=0; i < 5; i++) {
            GeneratedValueTableTestEntity entity = new GeneratedValueTableTestEntity();
            entity.setComment("test-table" + i);

            int execCount = sqlMapper.insert(entity)
                .execute();

            assertThat(execCount).isEqualTo(1);

            GeneratedValueTableTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entity.getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", (long)(i+1))
                .hasFieldOrPropertyWithValue("comment", "test-table" + i);
        }

    }

    @Test
    void testGenerateValue_table_fomrat() {

        MGeneratedValueTableFormatTestEntity m_ = MGeneratedValueTableFormatTestEntity.testTableFormat;

        for(int i=0; i < 5; i++) {
            GeneratedValueTableFormatTestEntity entity = new GeneratedValueTableFormatTestEntity();
            entity.setComment("test-table-format" + i);

            int execCount = sqlMapper.insert(entity)
                .execute();

            assertThat(execCount).isEqualTo(1);

            GeneratedValueTableFormatTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entity.getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", "0000000" + (i+1))
                .hasFieldOrPropertyWithValue("comment", "test-table-format" + i);
        }

    }

    @Test
    void testGenerateValue_uuid() {

        MGeneratedValueUUIDTestEntity m_ = MGeneratedValueUUIDTestEntity.testUUID;

        for(int i=0; i < 5; i++) {
            GeneratedValueUUIDTestEntity entity = new GeneratedValueUUIDTestEntity();
            entity.setComment("test-UUID" + i);

            int execCount = sqlMapper.insert(entity)
                .execute();

            assertThat(execCount).isEqualTo(1);

            GeneratedValueUUIDTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entity.getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", entity.getId())
                .hasFieldOrPropertyWithValue("comment", "test-UUID" + i);
        }

    }
}
