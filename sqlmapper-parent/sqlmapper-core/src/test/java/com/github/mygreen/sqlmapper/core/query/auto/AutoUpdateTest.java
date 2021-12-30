package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MCustomer;
import com.github.mygreen.sqlmapper.core.test.entity.type.GenderType;


/**
 * {@link AutoUpdate}のテスタ
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoUpdateTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_customer.sql");
    }

    @Test
    void testUpdate() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setLastName("Yamamoto");

        int count = txNew().execute(action -> {
            return sqlMapper.update(entity)
                    .execute();
        });

        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("lastName", "Yamamoto")
            .hasFieldOrPropertyWithValue("version", 1L);

    }

    @Test
    void testIncludesVersion() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setLastName("Yamamoto");
        entity.setVersion(10L); // versionを任意の値に更新

        int count = txNew().execute(action -> {
            return sqlMapper.update(entity)
                    .includesVersion()
                    .execute();
        });
        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("lastName", "Yamamoto")
            .hasFieldOrPropertyWithValue("version", 10L);

    }

    @Test
    void testExcludesNull() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setLastName("Yamamoto");
        entity.setGenderType(null); // nullに更新

        int count = txNew().execute(action -> {
            return sqlMapper.update(entity)
                    .excludesNull()
                    .execute();
        });
        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("lastName", "Yamamoto")
            .hasFieldOrPropertyWithValue("genderType", GenderType.MALE) // 変わらないこと
            .hasFieldOrPropertyWithValue("version", 1L);

    }

    @Test
    void testSuppresOptimisticLockException_false() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setLastName("Yamamoto");
        entity.setVersion(10L); // バージョンを存在しない値に更新

        assertThatThrownBy(() -> sqlMapper.update(entity).execute())
                .isInstanceOf(OptimisticLockingFailureException.class);

    }

    @Test
    void testSuppresOptimisticLockException_true() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setLastName("Yamamoto");
        entity.setVersion(10L); // バージョンを存在しない値に更新

        int count = txNew().execute(action -> {
            return sqlMapper.update(entity)
                    .suppresOptimisticLockException()
                    .execute();
        });
        assertThat(count).isEqualTo(0L);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("lastName", "Yamada")    // 変わらないこと
            .hasFieldOrPropertyWithValue("version", 0L);    // 変わらないこと

    }

    @Test
    void testIncludes() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setFirstName("Jiro");
        entity.setLastName("Yamamoto");
        entity.setBirthday(LocalDate.of(1980, 12, 10));

        int count = txNew().execute(action -> {
            return sqlMapper.update(entity)
                    .includes(m_.firstName, m_.birthday)
                    .execute();
        });
        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("firstName", "Jiro")
            .hasFieldOrPropertyWithValue("lastName", "Yamada")  // 変わらないこと
            .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1980, 12, 10))
            .hasFieldOrPropertyWithValue("version", 1L);

    }

    @Test
    void testExcludes() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setFirstName("Jiro");
        entity.setLastName("Yamamoto");
        entity.setBirthday(LocalDate.of(1980, 12, 10));

        int count = txNew().execute(action -> {
            return sqlMapper.update(entity)
                    .excludes(m_.lastName)
                    .execute();
        });
        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("firstName", "Jiro")
            .hasFieldOrPropertyWithValue("lastName", "Yamada")  // 変わらないこと
            .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1980, 12, 10))
            .hasFieldOrPropertyWithValue("version", 1L);

    }

    @Test
    void testIncludesAndExcludes() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setFirstName("Jiro");
        entity.setLastName("Yamamoto");
        entity.setBirthday(LocalDate.of(1980, 12, 10));

        int count = txNew().execute(action -> {
            return sqlMapper.update(entity)
                    .includes(m_.firstName, m_.birthday)
                    .excludes(m_.birthday)
                    .execute();
        });
        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("firstName", "Jiro")
            .hasFieldOrPropertyWithValue("lastName", "Yamada")  // 変わらないこと
            .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1980, 12, 10))    // 更新されること
            .hasFieldOrPropertyWithValue("version", 1L);

    }

    @Test
    void testChangedFrom_entity() {

        MCustomer m_ = MCustomer.customer;

        Customer before = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setLastName("Yamamoto");

        int count = txNew().execute(action -> {
            return sqlMapper.update(entity)
                    .changedFrom(before)
                    .execute();
        });

        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("lastName", "Yamamoto")
            .hasFieldOrPropertyWithValue("version", 1L);

    }

    @Test
    void testChangedFrom_map() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        Map<String, Object> before = Map.of("id", entity.getId()
                , "firstName", entity.getFirstName()
                , "lastName", entity.getLastName()
                , "birthday", entity.getBirthday()
                , "genderType", entity.getGenderType()
                , "version", entity.getVersion());

        entity.setLastName("Yamamoto");

        int count = txNew().execute(action -> {
            return sqlMapper.update(entity)
                    .changedFrom(before)
                    .execute();
        });

        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("lastName", "Yamamoto")
            .hasFieldOrPropertyWithValue("version", 1L);

    }

}
