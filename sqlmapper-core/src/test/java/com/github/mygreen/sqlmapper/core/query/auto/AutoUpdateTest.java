package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.testdata.Customer;
import com.github.mygreen.sqlmapper.core.testdata.TestConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class AutoUpdateTest {

    @Autowired
    SqlMapper sqlMapper;

    @Test
    void testUpdate() {

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

        {
            // 対象のレコードの取得
            Customer entity = sqlMapper.selectFrom(Customer.class)
                    .id("00001")
                    .getSingleResult();

            entity.setFirstName("Yamamoto");

            long count = sqlMapper.update(entity)
                .execute();
            assertThat(count).isEqualTo(1);
        }

        {
            // 対象のレコードの取得
            Customer result = sqlMapper.selectFrom(Customer.class)
                    .id("00001")
                    .getSingleResult();

            assertThat(result.getFirstName()).isEqualTo("Yamamoto");

        }
    }
}
