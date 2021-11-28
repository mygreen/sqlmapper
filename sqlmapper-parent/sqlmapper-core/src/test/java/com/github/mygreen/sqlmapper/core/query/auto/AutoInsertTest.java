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
import com.github.mygreen.sqlmapper.core.query.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.Employee;
import com.github.mygreen.sqlmapper.core.test.entity.MCustomer;
import com.github.mygreen.sqlmapper.core.test.entity.MEmployee;
import com.github.mygreen.sqlmapper.core.test.entity.Role;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoInsertTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
    }

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
        entity.setName("Suzuki Hanako");
        entity.setAge(20);
        entity.setRole(Role.MANAGER);
        entity.setHireDate(LocalDate.of(2021, 1, 1));
        entity.setSectionCode("021");
        entity.setBusinessEstablishmentCode(1);

        int execCount = sqlMapper.insert(entity)
            .execute();

        assertThat(execCount).isEqualTo(1);

        Employee result = sqlMapper.selectFrom(MEmployee.employee)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getVersion()).isEqualTo(0L);

    }
}
