package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.query.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.CustomerAddress;
import com.github.mygreen.sqlmapper.core.test.entity.Employee;
import com.github.mygreen.sqlmapper.core.test.entity.MBusinessEstablishment;
import com.github.mygreen.sqlmapper.core.test.entity.MCustomer;
import com.github.mygreen.sqlmapper.core.test.entity.MCustomerAddress;
import com.github.mygreen.sqlmapper.core.test.entity.MEmployee;
import com.github.mygreen.sqlmapper.core.test.entity.MSection;
import com.github.mygreen.sqlmapper.core.test.entity.type.GenderType;


/**
 * {@link AutoSelect}のテスタ
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoSelectTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_customer.sql", "insert_data_business.sql");
    }

    @Test
    void testSelectById() {

        MCustomer m_ = MCustomer.customer;

        Customer resulst = sqlMapper.selectFrom(m_)
                .id("001")
                .getSingleResult();

        assertThat(resulst).hasFieldOrPropertyWithValue("id", "001");

    }

    @Test
    void testSelectByVersion() {

        MCustomer m_ = MCustomer.customer;

        // 存在するバージョンの場合
        {
            Customer resulst = sqlMapper.selectFrom(m_)
                    .id("001")
                    .version(0L)
                    .getSingleResult();

            assertThat(resulst).hasFieldOrPropertyWithValue("id", "001");
        }

        // 存在しないバージョンの場合
        {
            assertThatThrownBy(() -> sqlMapper.selectFrom(m_)
                    .id("001")
                    .version(1L)
                    .getSingleResult())
                .isInstanceOf(IncorrectResultSizeDataAccessException.class);
        }

    }

    @Test
    void testSelectWhere() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> result = sqlMapper.selectFrom(m_)
            .where(m_.lastName.lower().contains("yama"))
            .getResultList();

        assertThat(result).hasSize(3);

    }

    @Test
    void testOrderBy() {

        MCustomer m_ = MCustomer.customer;

        List<String> result = sqlMapper.selectFrom(m_)
                .where(m_.birthday.before(LocalDate.of(2000, 1, 1)))
                .orderBy(m_.birthday.desc())
                .getResultStream()
                .map(c -> c.getId())
                .collect(Collectors.toList());

        assertThat(result).hasSize(6)
            .containsExactly("009", "002", "006", "007", "001", "008");

    }

    @Test
    void testLimit() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> result = sqlMapper.selectFrom(m_)
                .orderBy(m_.id.asc())
                .limit(5)
                .getResultList();

        assertThat(result).hasSize(5);
        assertThat(result.get(0)).hasFieldOrPropertyWithValue("id", "001");
        assertThat(result.get(4)).hasFieldOrPropertyWithValue("id", "005");

    }

    @Test
    void testOffset() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> result = sqlMapper.selectFrom(m_)
                .orderBy(m_.id.asc())
                .offset(2)
                .getResultList();

        assertThat(result).hasSize(8);
        assertThat(result.get(0)).hasFieldOrPropertyWithValue("id", "003");
        assertThat(result.get(7)).hasFieldOrPropertyWithValue("id", "010");
    }

    @Test
    void testOffsetAndLimit() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> result = sqlMapper.selectFrom(m_)
                .orderBy(m_.id.asc())
                .offset(2)
                .limit(5)
                .getResultList();

        assertThat(result).hasSize(5);
        assertThat(result.get(0)).hasFieldOrPropertyWithValue("id", "003");
        assertThat(result.get(4)).hasFieldOrPropertyWithValue("id", "007");

    }

    @Test
    void testIncludes() {

        MCustomer m_ = MCustomer.customer;

        Customer result = sqlMapper.selectFrom(m_)
                .id("001")
                .includes(m_.firstName, m_.lastName)
                .getSingleResult();

        assertThat(result.getId()).isEqualTo("001");
        assertThat(result.getFirstName()).isEqualTo("Taro");
        assertThat(result.getLastName()).isEqualTo("Yamada");
        assertThat(result.getBirthday()).isNull();
        assertThat(result.getGenderType()).isNull();
        assertThat(result.getVersion()).isNull();

    }

    @Test
    void testExcludes() {

        MCustomer m_ = MCustomer.customer;

        Customer result = sqlMapper.selectFrom(m_)
                .id("001")
                .excludes(m_.firstName, m_.lastName)
                .getSingleResult();

        assertThat(result.getId()).isEqualTo("001");
        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).isNull();
        assertThat(result.getBirthday()).isEqualTo(LocalDate.of(1980, 1, 1));
        assertThat(result.getGenderType()).isEqualTo(GenderType.MALE);
        assertThat(result.getVersion()).isEqualTo(0L);

    }

    @Test
    void testIncluesAndExcludes() {
        MCustomer m_ = MCustomer.customer;

        Customer result = sqlMapper.selectFrom(m_)
                .id("001")
                .includes(m_.firstName, m_.lastName)
                .excludes(m_.id, m_.lastName, m_.birthday)
                .getSingleResult();

        assertThat(result.getId()).isEqualTo("001");
        assertThat(result.getFirstName()).isEqualTo("Taro");
        assertThat(result.getLastName()).isEqualTo("Yamada");
        assertThat(result.getBirthday()).isNull();
        assertThat(result.getGenderType()).isNull();
        assertThat(result.getVersion()).isNull();
    }


    @Test
    void testSelectCount() {

        MCustomer m_ = MCustomer.customer;

        long result = sqlMapper.selectFrom(m_)
            .where(m_.lastName.eq("Yamada"))
            .getCount();

        assertThat(result).isEqualTo(2);
    }

    @Test
    void testSingleResult_found1() {

        MCustomer m_ = MCustomer.customer;

        Customer result = sqlMapper.selectFrom(m_)
            .id("001")
            .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", "001");
    }

    @Test
    void testSingleResult_found0() {

        MCustomer m_ = MCustomer.customer;

        assertThatThrownBy(() -> sqlMapper.selectFrom(m_)
                .id("xxx")
                .getSingleResult())
            .isInstanceOf(IncorrectResultSizeDataAccessException.class);
    }

    @Test
    void testSingleResult_found2() {

        MCustomer m_ = MCustomer.customer;

        assertThatThrownBy(() -> sqlMapper.selectFrom(m_)
                .where(m_.lastName.eq("Yamada"))
                .getSingleResult())
            .isInstanceOf(IncorrectResultSizeDataAccessException.class);
    }

    @Test
    void testResultList() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> result = sqlMapper.selectFrom(m_)
            .where(m_.lastName.eq("Yamada"))
            .getResultList();

        assertThat(result).hasSize(2);
        result.forEach(r -> {
            assertThat(r).hasFieldOrPropertyWithValue("lastName", "Yamada");
        });
    }

    @Test
    void testResultStream() {

        MCustomer m_ = MCustomer.customer;

        List<Customer> result = sqlMapper.selectFrom(m_)
                .where(m_.lastName.eq("Yamada"))
                .getResultStream()
                .collect(Collectors.toList());


        assertThat(result).hasSize(2);
        result.forEach(r -> {
            assertThat(r).hasFieldOrPropertyWithValue("lastName", "Yamada");
        });
    }

    @Test
    void testOuterJoin() {

        MCustomer c_ = MCustomer.customer;
        MCustomerAddress a_ = MCustomerAddress.customerAddress;

        List<Customer> result = sqlMapper.selectFrom(c_)
                .leftJoin(a_, (to) -> to.customerId.eq(c_.id))
                .associate(c_, a_, (e1, e2) -> Optional.ofNullable(e2.getCustomerId()).ifPresent(c -> e1.setAddress(e2)))
                .orderBy(c_.id.asc())
                .limit(2)
                .getResultList();

        assertThat(result).hasSize(2);
        {
            Customer cust = result.get(0);
            assertThat(cust).hasFieldOrPropertyWithValue("id", "001");

            CustomerAddress addr = cust.getAddress();
            assertThat(addr).hasFieldOrPropertyWithValue("customerId", "001")
                .hasFieldOrPropertyWithValue("address", "神奈川県藤沢市亀井野1-4-17");

        }

        {
            // outer join のため、関連が存在しないときも含む
            Customer cust = result.get(1);
            assertThat(cust).hasFieldOrPropertyWithValue("id", "002");

            CustomerAddress addr = cust.getAddress();
            assertThat(addr).isNull();

        }

    }

    @Test
    void testInnerJoin() {

        MCustomer c_ = MCustomer.customer;
        MCustomerAddress a_ = MCustomerAddress.customerAddress;

        List<Customer> result = sqlMapper.selectFrom(c_)
                .innerJoin(a_, (to) -> to.customerId.eq(c_.id))
                .associate(c_, a_, (e1, e2) -> e1.setAddress(e2))
                .orderBy(c_.id.asc())
                .limit(2)
                .getResultList();

        assertThat(result).hasSize(2);
        {
            Customer cust = result.get(0);
            assertThat(cust).hasFieldOrPropertyWithValue("id", "001");

            CustomerAddress addr = cust.getAddress();
            assertThat(addr).hasFieldOrPropertyWithValue("customerId", "001")
                .hasFieldOrPropertyWithValue("address", "神奈川県藤沢市亀井野1-4-17");

        }

        {
            // inner join のため、関連が存在しない場合はスキップ
            Customer cust = result.get(1);
            assertThat(cust).hasFieldOrPropertyWithValue("id", "003");

            CustomerAddress addr = cust.getAddress();
            assertThat(addr).hasFieldOrPropertyWithValue("customerId", "003")
                .hasFieldOrPropertyWithValue("address", "東京都目黒区上目黒3-1-1106");

        }

    }

    @Test
    void testManyJoin() {

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
