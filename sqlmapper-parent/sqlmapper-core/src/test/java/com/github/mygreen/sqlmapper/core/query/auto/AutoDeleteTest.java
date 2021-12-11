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
import com.github.mygreen.sqlmapper.core.test.entity.meta.MCustomer;

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

        {
            // データの作成
            Customer entity = new Customer();
            entity.setId("00001");
            entity.setFirstName("Taro");
            entity.setLastName("Yamada");
            entity.setBirthday(LocalDate.of(2010, 10, 1));

            int count = sqlMapper.insert(entity)
                .execute();

            assertThat(count).isEqualTo(1);

        }

        // 対象のレコードの取得
        Customer entity = sqlMapper.selectFrom(MCustomer.customer)
                .id("00001")
                .getSingleResult();

        long count = sqlMapper.delete(entity)
            .execute();
        assertThat(count).isEqualTo(1);
    }
}
