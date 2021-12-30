package com.github.mygreen.sqlmapper.core.query.sql;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.splate.MapSqlTemplateContext;
import com.github.mygreen.splate.SqlTemplateContext;
import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MCustomer;


/**
 * {@link SqlUpdate} のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class SqlUpdateTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_customer.sql", "insert_data_business.sql");
    }

    @Test
    void testBySqlFile_dete() {

        SqlTemplateContext templateContext = new MapSqlTemplateContext(Map.of("customerId", "005"));

        int count = txNew().execute(action ->
                sqlMapper.updateBySqlFile("/sqltemplate/customer_deleteById.sql", templateContext)
                    .execute()
                );

        assertThat(count).isEqualTo(1);

        MCustomer m_ = MCustomer.customer;

        Optional<Customer> result = sqlMapper.selectFrom(m_)
                .id("005")
                .getOptionalResult();
        assertThat(result).isEmpty();

    }

    @Test
    void testBySql_delete() {

        SqlTemplateContext templateContext = new MapSqlTemplateContext(Map.of("customerId", "005"));

        String sql = loadResource("/sqltemplate/customer_deleteById.sql", StandardCharsets.UTF_8);

        int count = txNew().execute(action ->
                sqlMapper.updateBySql(sql, templateContext)
                    .execute()
                );

        assertThat(count).isEqualTo(1);

        MCustomer m_ = MCustomer.customer;

        Optional<Customer> result = sqlMapper.selectFrom(m_)
                .id("005")
                .getOptionalResult();
        assertThat(result).isEmpty();

    }


}
