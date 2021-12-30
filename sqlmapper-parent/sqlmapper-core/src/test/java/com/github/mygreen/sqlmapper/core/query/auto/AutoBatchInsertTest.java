package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueSequenceTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueTableTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.GeneratedValueUUIDTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MCustomer;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueIdentity2TestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueIdentityTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueSequenceTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueTableTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MGeneratedValueUUIDTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.type.GenderType;


/**
 * {@link AutoBatchInsert}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoBatchInsertTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        sqlMapper.getContext().getEntityMetaFactory().refreshTableIdGenerator();
    }

    @Test
    void testBatchInsert() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            Customer entity = new Customer();
            entity.setId(String.format("test@%05d", i));
            entity.setFirstName("fName" + i);
            entity.setLastName("lName" + i);
            entity.setBirthday(LocalDate.of(2010, 10, i+1));
            entity.setGenderType(GenderType.MALE);

            entities.add(entity);
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            Customer result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", String.format("test@%05d", i))
                .hasFieldOrPropertyWithValue("firstName", "fName" + i)
                .hasFieldOrPropertyWithValue("lastName", "lName" + i)
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, i+1))
                .hasFieldOrPropertyWithValue("genderType", GenderType.MALE)
                .hasFieldOrPropertyWithValue("version", 0l);

        }

    }

    @Test
    void testIncludes() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            Customer entity = new Customer();
            entity.setId(String.format("test@%05d", i));
            entity.setFirstName("fName" + i);
            entity.setLastName("lName" + i);
            entity.setBirthday(LocalDate.of(2010, 10, i+1));
            entity.setGenderType(GenderType.MALE);

            entities.add(entity);
        }


        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .includes(m_.firstName, m_.lastName, m_.birthday)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            Customer result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", String.format("test@%05d", i))
                .hasFieldOrPropertyWithValue("firstName", "fName" + i)
                .hasFieldOrPropertyWithValue("lastName", "lName" + i)
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, i+1))
                .hasFieldOrPropertyWithValue("genderType", null)    // 挿入対象外
                .hasFieldOrPropertyWithValue("version", 0l);

        }

    }

    @Test
    void testExcludes() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            Customer entity = new Customer();
            entity.setId(String.format("test@%05d", i));
            entity.setFirstName("fName" + i);
            entity.setLastName("lName" + i);
            entity.setBirthday(LocalDate.of(2010, 10, i+1));
            entity.setGenderType(GenderType.MALE);

            entities.add(entity);
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .excludes(m_.genderType)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            Customer result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", String.format("test@%05d", i))
                .hasFieldOrPropertyWithValue("firstName", "fName" + i)
                .hasFieldOrPropertyWithValue("lastName", "lName" + i)
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, i+1))
                .hasFieldOrPropertyWithValue("genderType", null)    // 挿入対象外
                .hasFieldOrPropertyWithValue("version", 0l);

        }

    }

    @Test
    void testIncludesAndExcludes() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            Customer entity = new Customer();
            entity.setId(String.format("test@%05d", i));
            entity.setFirstName("fName" + i);
            entity.setLastName("lName" + i);
            entity.setBirthday(LocalDate.of(2010, 10, i+1));
            entity.setGenderType(GenderType.MALE);

            entities.add(entity);
        }


        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .includes(m_.firstName, m_.lastName, m_.birthday)
                    .excludes(m_.id, m_.birthday)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            Customer result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", String.format("test@%05d", i))
                .hasFieldOrPropertyWithValue("firstName", "fName" + i)
                .hasFieldOrPropertyWithValue("lastName", "lName" + i)
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, i+1))
                .hasFieldOrPropertyWithValue("genderType", null)    // 挿入対象外
                .hasFieldOrPropertyWithValue("version", 0l);

        }

    }

    @Test
    void testGenerateValue_identity() {

        MGeneratedValueIdentityTestEntity m_ = MGeneratedValueIdentityTestEntity.testIdentity;

        List<GeneratedValueIdentityTestEntity> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            GeneratedValueIdentityTestEntity entity = new GeneratedValueIdentityTestEntity();
            entity.setComment("test-identity" + i);

            entities.add(entity);
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            GeneratedValueIdentityTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", (long)(i+1))
                    .hasFieldOrPropertyWithValue("comment", "test-identity" + i);

        }

    }

    @Test
    void testGenerateValue_identity2() {

        MGeneratedValueIdentity2TestEntity m_ = MGeneratedValueIdentity2TestEntity.testIdentity2;

        List<GeneratedValueIdentity2TestEntity> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            GeneratedValueIdentity2TestEntity entity = new GeneratedValueIdentity2TestEntity();
            entity.setComment("test-identity2" + i);

            entities.add(entity);
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            GeneratedValueIdentity2TestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId1(), entities.get(i).getId2())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id1", (long)(i+1))
                    .hasFieldOrPropertyWithValue("id2", 100+i)
                    .hasFieldOrPropertyWithValue("comment", "test-identity2" + i);

        }

    }

    @Test
    void testGenerateValue_sequence() {

        MGeneratedValueSequenceTestEntity m_ = MGeneratedValueSequenceTestEntity.testSequence;

        List<GeneratedValueSequenceTestEntity> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            GeneratedValueSequenceTestEntity entity = new GeneratedValueSequenceTestEntity();
            entity.setComment("test-sequence" + i);

            entities.add(entity);
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            GeneratedValueSequenceTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", (long)(i+1))
                    .hasFieldOrPropertyWithValue("comment", "test-sequence" + i);

        }

    }

    @Test
    void testGenerateValue_table() {

        MGeneratedValueTableTestEntity m_ = MGeneratedValueTableTestEntity.testTable;

        List<GeneratedValueTableTestEntity> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            GeneratedValueTableTestEntity entity = new GeneratedValueTableTestEntity();
            entity.setComment("test-table" + i);

            entities.add(entity);
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            GeneratedValueTableTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", (long)(i+1))
                    .hasFieldOrPropertyWithValue("comment", "test-table" + i);

        }

    }

    @Test
    void testGenerateValue_uuid() {

        MGeneratedValueUUIDTestEntity m_ = MGeneratedValueUUIDTestEntity.testUUID;

        List<GeneratedValueUUIDTestEntity> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            GeneratedValueUUIDTestEntity entity = new GeneratedValueUUIDTestEntity();
            entity.setComment("test-uuid" + i);

            entities.add(entity);
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            GeneratedValueUUIDTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", entities.get(i).getId())
                    .hasFieldOrPropertyWithValue("comment", "test-uuid" + i);

        }

    }

}
