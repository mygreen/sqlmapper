package com.github.mygreen.sqlmapper.core.query.sql;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.splate.MapSqlTemplateContext;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.type.GenderType;

/**
 * {@link SqlSelect} のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class SqlSelectTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_customer.sql", "insert_data_business.sql");
    }

    @Test
    void testBySqlFile() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("customerId", "005"));

        Customer result = sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectById.sql", templateContext)
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", "005")
                .hasFieldOrPropertyWithValue("firstName", "Chikako")
                .hasFieldOrPropertyWithValue("lastName", "Shimomura")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 12, 30))
                .hasFieldOrPropertyWithValue("genderType", GenderType.FEMALE)
                .hasFieldOrPropertyWithValue("version", 0l);
    }

    @Test
    void testBySql() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("customerId", "005"));

        String sql = loadResource("/sqltemplate/customer_selectById.sql", StandardCharsets.UTF_8);

        Customer result = sqlMapper.selectBySql(Customer.class, sql, templateContext)
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", "005")
                .hasFieldOrPropertyWithValue("firstName", "Chikako")
                .hasFieldOrPropertyWithValue("lastName", "Shimomura")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 12, 30))
                .hasFieldOrPropertyWithValue("genderType", GenderType.FEMALE)
                .hasFieldOrPropertyWithValue("version", 0l);

    }

    @Test
    void testBySqlFile_singleResult_found1() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("customerId", "005"));

        Customer result = sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectById.sql", templateContext)
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", "005");
    }

    @Test
    void testBySqlFile_singleResult_found0() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("customerId", "xx"));

        assertThatThrownBy(() ->
                sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectById.sql", templateContext)
                    .getSingleResult()
                ).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void testBySqlFile_singleResult_found2() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("lastName", "Yamada"));

        assertThatThrownBy(() ->
                sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectByAny.sql", templateContext)
                    .getSingleResult()
                ).isInstanceOf(IncorrectResultSizeDataAccessException.class);
    }

    @Test
    void testBySqlFile_optionalResult_found1() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("customerId", "005"));

        Optional<Customer> result = sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectById.sql", templateContext)
                .getOptionalResult();

        assertThat(result).isNotEmpty();
    }

    @Test
    void testBySqlFile_optionalResult_found0() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("customerId", "xx"));

        Optional<Customer> result = sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectById.sql", templateContext)
                .getOptionalResult();

        assertThat(result).isEmpty();

    }

    @Test
    void testBySqlFile_optionalResult_found2() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("lastName", "Yamada"));

        assertThatThrownBy(() ->
                sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectByAny.sql", templateContext)
                    .getOptionalResult()
                ).isInstanceOf(IncorrectResultSizeDataAccessException.class);
    }

    @Test
    void testBySqlFile_resultList() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("lastName", "Yamada"));

        List<Customer> result = sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectByAny.sql", templateContext)
                    .getResultList();

        assertThat(result).hasSize(2);
        result.forEach(r -> {
            assertThat(r).hasFieldOrPropertyWithValue("lastName", "Yamada");
        });
    }

    @Test
    void testBySqlFile_resultStream() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("lastName", "Yamada"));

        List<Customer> result = sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectByAny.sql", templateContext)
                    .getResultStream()
                    .collect(Collectors.toList());

        assertThat(result).hasSize(2);
        result.forEach(r -> {
            assertThat(r).hasFieldOrPropertyWithValue("lastName", "Yamada");
        });
    }

    @Test
    void testBySqlFile_limit() {

        Map<String, Object> params = new HashMap<>();
        params.put("lastName", null);
        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(params);

        List<Customer> result = sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectByAny.sql", templateContext)
                .limit(5)
                .getResultList();

        assertThat(result).hasSize(5);
        assertThat(result.get(0)).hasFieldOrPropertyWithValue("id", "001");
        assertThat(result.get(4)).hasFieldOrPropertyWithValue("id", "005");

    }

    @Test
    void testBySqlFile_offset() {

        Map<String, Object> params = new HashMap<>();
        params.put("lastName", null);
        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(params);

        List<Customer> result = sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectByAny.sql", templateContext)
                .offset(2)
                .getResultList();

        assertThat(result).hasSize(8);
        assertThat(result.get(0)).hasFieldOrPropertyWithValue("id", "003");
        assertThat(result.get(7)).hasFieldOrPropertyWithValue("id", "010");

    }

    @Test
    void testBySqlFile_offsetAndLimit() {

        Map<String, Object> params = new HashMap<>();
        params.put("lastName", null);
        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(params);

        List<Customer> result = sqlMapper.selectBySqlFile(Customer.class, "/sqltemplate/customer_selectByAny.sql", templateContext)
                .offset(2)
                .limit(5)
                .getResultList();

        assertThat(result).hasSize(5);
        assertThat(result.get(0)).hasFieldOrPropertyWithValue("id", "003");
        assertThat(result.get(4)).hasFieldOrPropertyWithValue("id", "007");

    }

}
