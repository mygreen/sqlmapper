package com.github.mygreen.sqlmapper.core.query.sql;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

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

/**
 * {@link SqlCount} のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class SqlCountTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_customer.sql", "insert_data_business.sql");
    }

    @Test
    void testBySqlFile() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("lastName", "Yamada"));

        long result = sqlMapper.getCountBySqlFile("/sqltemplate/customer_selectByAny.sql", templateContext);

        assertThat(result).isEqualTo(2l);
    }

    @Test
    void testBySql() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("lastName", "Yamada"));

        String sql = loadResource("/sqltemplate/customer_selectByAny.sql", StandardCharsets.UTF_8);

        long result = sqlMapper.getCountBySql(sql, templateContext);

        assertThat(result).isEqualTo(2l);
    }

}
