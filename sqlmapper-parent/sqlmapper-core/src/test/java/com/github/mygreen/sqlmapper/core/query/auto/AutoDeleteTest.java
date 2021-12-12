package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

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
 * {@link AutoDelete} のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoDeleteTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_customer.sql");
    }

    @Test
    void testDelete() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        long count = sqlMapper.delete(entity)
                .execute();
        assertThat(count).isEqualTo(1L);

        Optional<Customer> result = sqlMapper.selectFrom(m_)
                .id("001")
                .getOptionalResult();
        assertThat(result).isEmpty();

    }

    @Test
    void testDelete_ignoreVersion() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setVersion(10L); // 存在しないバージョン

        long count = sqlMapper.delete(entity)
                .ignoreVersion()
                .execute();
        assertThat(count).isEqualTo(1L);

        Optional<Customer> result = sqlMapper.selectFrom(m_)
                .id("001")
                .getOptionalResult();
        assertThat(result).isEmpty();    // 削除されていること

    }

    @Test
    void testDelete_suppresOptimisticLockException_false() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setVersion(10L); // 存在しないバージョン

        assertThatThrownBy(() -> sqlMapper.delete(entity).execute())
            .isInstanceOf(OptimisticLockingFailureException.class);

    }

    @Test
    void testDelete_suppresOptimisticLockException_true() {

        MCustomer m_ = MCustomer.customer;

        Customer entity = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        entity.setVersion(10L); // 存在しないバージョン

        long count = sqlMapper.delete(entity)
                .suppresOptimisticLockException()
                .execute();
        assertThat(count).isEqualTo(0L);

        Optional<Customer> result = sqlMapper.selectFrom(m_)
                .id("001")
                .getOptionalResult();
        assertThat(result).isNotEmpty();    // 削除されていないこと。

    }
}
