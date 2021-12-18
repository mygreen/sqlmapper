package com.github.mygreen.sqlmapper.core.query.auto;

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
import com.github.mygreen.sqlmapper.core.test.entity.meta.MCustomer;
import com.github.mygreen.sqlmapper.core.test.entity.type.GenderType;


/**
 * {@link AutoAnyDelete} のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoAnyDeleteTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_customer.sql");
    }

    @Test
    void testAnyDeleteAll() {

        MCustomer m_ = MCustomer.customer;

        long beforeCount = sqlMapper.selectFrom(m_)
                .getCount();

        int count = txNew().execute(action -> {
            return sqlMapper.deleteFrom(m_)
                .execute();
        });

        assertThat(count).isEqualTo(beforeCount);

        long afterCount = sqlMapper.selectFrom(m_)
                .getCount();

        assertThat(afterCount).isEqualTo(0);

    }

    @Test
    void testWhere() {

        MCustomer m_ = MCustomer.customer;


        long beforeCount = sqlMapper.selectFrom(m_)
                .where(m_.genderType.eq(GenderType.MALE))
                .getCount();

        int count = txNew().execute(action -> {
            return sqlMapper.deleteFrom(m_)
                .where(m_.genderType.eq(GenderType.MALE))
                .execute();
        });

        assertThat(count).isEqualTo(beforeCount);

        sqlMapper.selectFrom(m_).getResultList().forEach(entity -> {
            assertThat(entity).hasFieldOrPropertyWithValue("genderType", GenderType.FEMALE);
        });

    }

}
