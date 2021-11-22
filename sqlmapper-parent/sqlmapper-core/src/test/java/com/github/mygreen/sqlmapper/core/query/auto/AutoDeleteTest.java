package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.config.TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.MCustomer;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class AutoDeleteTest {

    @Autowired
    SqlMapper sqlMapper;

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
