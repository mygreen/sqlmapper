package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.query.IterationCallback;
import com.github.mygreen.sqlmapper.core.query.IterationContext;
import com.github.mygreen.sqlmapper.core.testdata.Customer;
import com.github.mygreen.sqlmapper.core.testdata.TestConfig;

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

    @Test
    void testResultStream() {
        List<Customer> entity = sqlMapper.selectFrom(Customer.class)
            .id("001")
            .getResultStream()
            .collect(Collectors.toList());

        assertThat(entity).hasSize(1);
        assertThat(entity.get(0)).hasFieldOrPropertyWithValue("id", "001");
    }

}
