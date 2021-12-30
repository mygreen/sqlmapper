package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

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


/**
 * {@link AutoBatchUpdate}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoBatchUpdateTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_customer.sql");
    }

    @Test
    void testBatchUpdate() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.asc())
                .getResultList();

        for(int i=0; i < 5; i++) {
            Customer entity = entities.get(i);
            entity.setLastName("Name@" + i);
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.updateBatch(entities)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            Customer result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("lastName", "Name@" + i)
                    .hasFieldOrPropertyWithValue("version", 1L);

        }

    }

    @Test
    void testIncludesVersion() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.asc())
                .getResultList();

        for(int i=0; i < 5; i++) {
            Customer entity = entities.get(i);
            entity.setLastName("Name@" + i);
            entity.setVersion((long)(10 + i)); // versionを任意の値に更新
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.updateBatch(entities)
                    .includesVersion()
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            Customer result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("lastName", "Name@" + i)
                    .hasFieldOrPropertyWithValue("version", (long)(10 + i));

        }

    }

    @Test
    void testSuppresOptimisticLockException_false() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.asc())
                .getResultList();

        for(int i=0; i < 5; i++) {
            Customer entity = entities.get(i);
            entity.setLastName("Name@" + i);
            entity.setVersion((long)(10 + i)); // バージョンを存在しない値に更新
        }

        assertThatThrownBy(() -> sqlMapper.updateBatch(entities).execute())
                .isInstanceOf(OptimisticLockingFailureException.class);

    }

    @Test
    void testSuppresOptimisticLockException_true() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.asc())
                .getResultList();

        String[] beforeLastNames = new String[5];
        for(int i=0; i < 5; i++) {
            Customer entity = entities.get(i);
            beforeLastNames[i] = entity.getLastName();  // 変更前を保存しておく

            entity.setLastName("Name@" + i);
            entity.setVersion((long)(10 + i)); // バージョンを存在しない値に更新
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.updateBatch(entities)
                    .suppresOptimisticLockException()
                    .execute();
        });

        assertThat(count).containsExactly(0, 0, 0, 0, 0);

        for(int i=0; i < 5; i++) {
            Customer result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("lastName", beforeLastNames[i])   // 変わらないこと
                    .hasFieldOrPropertyWithValue("version", 0L);    // 変わらないこと

        }

    }

    @Test
    void testIncludes() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.asc())
                .getResultList();

        String[] beforeLastNames = new String[5];
        for(int i=0; i < 5; i++) {
            Customer entity = entities.get(i);
            beforeLastNames[i] = entity.getLastName();  // 変更前を保存しておく

            entity.setFirstName("fName@" + i);
            entity.setLastName("lName@" + i);
            entity.setBirthday(LocalDate.of(2010, 10, i+1));
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.updateBatch(entities)
                    .includes(m_.firstName, m_.birthday)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            Customer result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("firstName", "fName@" + i)
                    .hasFieldOrPropertyWithValue("lastName", beforeLastNames[i])  // 変わらないこと
                    .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, i+1))
                    .hasFieldOrPropertyWithValue("version", 1L);

        }

    }

    @Test
    void testExcludes() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.asc())
                .getResultList();

        String[] beforeLastNames = new String[5];
        for(int i=0; i < 5; i++) {
            Customer entity = entities.get(i);
            beforeLastNames[i] = entity.getLastName();  // 変更前を保存しておく

            entity.setFirstName("fName@" + i);
            entity.setLastName("lName@" + i);
            entity.setBirthday(LocalDate.of(2010, 10, i+1));
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.updateBatch(entities)
                    .excludes(m_.lastName)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            Customer result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("firstName", "fName@" + i)
                    .hasFieldOrPropertyWithValue("lastName", beforeLastNames[i])  // 変わらないこと
                    .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, i+1))
                    .hasFieldOrPropertyWithValue("version", 1L);

        }

    }

    @Test
    void testIncludesAndExcludes() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.asc())
                .getResultList();

        String[] beforeLastNames = new String[5];
        for(int i=0; i < 5; i++) {
            Customer entity = entities.get(i);
            beforeLastNames[i] = entity.getLastName();  // 変更前を保存しておく

            entity.setFirstName("fName@" + i);
            entity.setLastName("lName@" + i);
            entity.setBirthday(LocalDate.of(2010, 10, i+1));
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.updateBatch(entities)
                    .includes(m_.firstName, m_.birthday)
                    .excludes(m_.birthday)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            Customer result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("firstName", "fName@" + i)
                    .hasFieldOrPropertyWithValue("lastName", beforeLastNames[i])  // 変わらないこと
                    .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2010, 10, i+1))    // 更新されること
                    .hasFieldOrPropertyWithValue("version", 1L);

        }

    }

}
