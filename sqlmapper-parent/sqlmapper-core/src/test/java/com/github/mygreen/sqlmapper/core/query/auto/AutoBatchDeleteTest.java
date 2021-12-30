package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

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
 * {@link AutoBatchDelete}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoBatchDeleteTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_customer.sql");
    }

    @Test
    void testBatchDelete() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.asc())
                .getResultList();

        int count = txNew().execute(action -> {
            return sqlMapper.deleteBatch(entities)
                    .execute();
        });

        assertThat(count).isEqualTo(5);

        for(int i=0; i < 5; i++) {
            Optional<Customer> result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getOptionalResult();
            assertThat(result).isEmpty();

        }

    }

    @Test
    void testDelete_ignoreVersion() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.asc())
                .getResultList();

        for(int i=0; i < 5; i++) {
            Customer entity = entities.get(i);
            entity.setVersion((long)(10 + i)); // バージョンを存在しない値に更新
        }

        int count = txNew().execute(action -> {
            return sqlMapper.deleteBatch(entities)
                    .ignoreVersion()
                    .execute();
        });

        assertThat(count).isEqualTo(5);

        for(int i=0; i < 5; i++) {
            Optional<Customer> result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getOptionalResult();
            assertThat(result).isEmpty();    // 削除されていること

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
            entity.setVersion((long)(10 + i)); // バージョンを存在しない値に更新
        }

        assertThatThrownBy(() -> sqlMapper.deleteBatch(entities).execute())
                .isInstanceOf(OptimisticLockingFailureException.class);

    }

    @Test
    void testSuppresOptimisticLockException_true() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.asc())
                .getResultList();

        for(int i=0; i < 5; i++) {
            Customer entity = entities.get(i);
            entity.setVersion((long)(10 + i)); // バージョンを存在しない値に更新
        }

        int count = txNew().execute(action -> {
            return sqlMapper.deleteBatch(entities)
                    .suppresOptimisticLockException()
                    .execute();
        });

        assertThat(count).isEqualTo(0);

        for(int i=0; i < 5; i++) {
            Optional<Customer> result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getOptionalResult();
            assertThat(result).isNotEmpty();    // 削除されていないこと

        }

    }

}
