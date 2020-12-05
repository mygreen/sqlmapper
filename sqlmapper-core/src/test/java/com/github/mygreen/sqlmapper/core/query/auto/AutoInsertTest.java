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
import com.github.mygreen.sqlmapper.core.testdata.Employee;
import com.github.mygreen.sqlmapper.core.testdata.MCustomer;
import com.github.mygreen.sqlmapper.core.testdata.MEmployee;
import com.github.mygreen.sqlmapper.core.testdata.Role;
import com.github.mygreen.sqlmapper.core.testdata.TestConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class AutoInsertTest {

    @Autowired
    SqlMapper sqlMapper;

    @Test
    void testInsert() {

        Customer entity = new Customer();
        entity.setId("00001");
        entity.setFirstName("Taro");
        entity.setLastName("Yamada");
        entity.setBirthday(LocalDate.of(2010, 10, 1));

        int count = sqlMapper.insert(entity)
            .execute();

        assertThat(count).isEqualTo(1);

        Customer result = sqlMapper.selectFrom(MCustomer.customer)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result.getId()).isEqualTo("00001");
        assertThat(result.getVersion()).isEqualTo(0L);

    }

    @Test
    void testInsertWithGenerateIdentity() {

        Employee entity = new Employee();
        entity.setName("Yamada Taro");
        entity.setAge(20);
        entity.setRole(Role.ENGINEER);

        int count = sqlMapper.insert(entity)
            .execute();

        assertThat(count).isEqualTo(1);

        Employee result = sqlMapper.selectFrom(MEmployee.employee)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getVersion()).isEqualTo(0L);

    }
}
