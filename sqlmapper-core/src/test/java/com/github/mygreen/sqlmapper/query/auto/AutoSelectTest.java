package com.github.mygreen.sqlmapper.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.SqlMapper;
import com.github.mygreen.sqlmapper.query.IterationCallback;
import com.github.mygreen.sqlmapper.query.IterationContext;
import com.github.mygreen.sqlmapper.testdata.Customer;
import com.github.mygreen.sqlmapper.testdata.TestConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class AutoSelectTest {

    @Autowired
    SqlMapper sqlMapper;

    @Test
    void testSelectCount() {
        long count = sqlMapper.selectFrom(Customer.class)
            .id("001")
            .getCount();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testSingleResult() {
        Customer entity = sqlMapper.selectFrom(Customer.class)
            .id("001")
            .getSingleResult();

        assertThat(entity).hasFieldOrPropertyWithValue("id", "001");
    }

    @Test
    void testResultList_1() {
        List<Customer> entity = sqlMapper.selectFrom(Customer.class)
            .id("001")
            .getResultList();

        assertThat(entity).hasSize(1);
        assertThat(entity.get(0)).hasFieldOrPropertyWithValue("id", "001");
    }

    @Test
    void testIterationResult() {

        sqlMapper.selectFrom(Customer.class)
            .id("001")
            .iterate(new IterationCallback<Customer, Void>() {

                @Override
                public Void iterate(Customer entity, IterationContext context) {

                    assertThat(entity).hasFieldOrPropertyWithValue("id", "001");

                    return null;
                }
            });

    }

}
