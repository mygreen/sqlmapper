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
import com.github.mygreen.sqlmapper.core.test.config.TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.Employee;
import com.github.mygreen.sqlmapper.core.test.entity.MBusinessEstablishment;
import com.github.mygreen.sqlmapper.core.test.entity.MCustomer;
import com.github.mygreen.sqlmapper.core.test.entity.MEmployee;
import com.github.mygreen.sqlmapper.core.test.entity.MSection;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class AutoSelectTest {

    @Autowired
    SqlMapper sqlMapper;

    @Test
    void testSelectCount() {
        long count = sqlMapper.selectFrom(MCustomer.customer)
            .id("001")
            .getCount();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testSingleResult() {
        Customer entity = sqlMapper.selectFrom(MCustomer.customer)
            .id("001")
            .getSingleResult();

        assertThat(entity).hasFieldOrPropertyWithValue("id", "001");
    }

    @Test
    void testResultList() {
        List<Customer> entity = sqlMapper.selectFrom(MCustomer.customer)
            .id("001")
            .getResultList();

        assertThat(entity).hasSize(1);
        assertThat(entity.get(0)).hasFieldOrPropertyWithValue("id", "001");
    }

    @Test
    void testResultStream() {
        List<Customer> entity = sqlMapper.selectFrom(MCustomer.customer)
            .id("001")
            .getResultStream()
            .collect(Collectors.toList());

        assertThat(entity).hasSize(1);
        assertThat(entity.get(0)).hasFieldOrPropertyWithValue("id", "001");
    }

    @Test
    void testResultList_join() {

        final MEmployee employee = MEmployee.employee;
        final MSection section = MSection.section;
        final MBusinessEstablishment businessEstablishment = MBusinessEstablishment.businessEstablishment;

        List<Employee> entity = sqlMapper.selectFrom(employee)
                .innerJoin(section, (to) -> to.code.eq(employee.sectionCode))
                .innerJoin(businessEstablishment, (to) -> to.code.eq(section.businessEstablishmentCode))
                .associate(employee, section, (e1, e2) -> e1.setSection(e2))
                .associate(section, businessEstablishment, (e1, e2) -> e1.setBusinessEstablishment(e2))
                .where(employee.name.eq("山田太郎"))
                .orderBy(employee.name.asc(), employee.hireDate.desc())
                .getResultList();

        assertThat(entity).hasSize(1);

        assertThat(entity.get(0)).hasFieldOrPropertyWithValue("name", "山田太郎");
        assertThat(entity.get(0).getSection()).hasFieldOrPropertyWithValue("name", "人事本部");
        assertThat(entity.get(0).getSection().getBusinessEstablishment()).hasFieldOrPropertyWithValue("name", "東京本社");

    }

}
