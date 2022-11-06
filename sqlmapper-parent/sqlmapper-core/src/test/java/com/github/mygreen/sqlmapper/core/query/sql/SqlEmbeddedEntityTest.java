package com.github.mygreen.sqlmapper.core.query.sql;

import static org.assertj.core.api.Assertions.*;

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
import com.github.mygreen.sqlmapper.core.test.entity.EmbeddedTestEntity;

/**
 * SQLテンプレートによる埋め込み型のエンティティのテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class SqlEmbeddedEntityTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_embedded.sql");
    }

    @Test
    void testSqlSelectByFile() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("id", new EmbeddedTestEntity.PK("001", 2l)));

        EmbeddedTestEntity result = sqlMapper.selectBySqlFile(EmbeddedTestEntity.class, "/sqltemplate/embedded_selectById.sql", templateContext)
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", new EmbeddedTestEntity.PK("001", 2l))
            .hasFieldOrPropertyWithValue("name", "001-2@embedded");

    }

    @Test
    void testSqlCountBySql() {

        MapSqlTemplateContext templateContext = new MapSqlTemplateContext();
        templateContext.addVariable("id", new EmbeddedTestEntity.PK(null, 2l));
        templateContext.addVariable("name", "%@embedded%");

        long result = sqlMapper.getCountBySqlFile("/sqltemplate/embedded_selectByAny.sql", templateContext);

        assertThat(result).isEqualTo(3l);

    }

    @Test
    void testSqlUpdateBySql_delete() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("id", new EmbeddedTestEntity.PK("001", 2l)));

        int count = txNew().execute(action -> sqlMapper.updateBySqlFile("/sqltemplate/embedded_deleteById.sql", templateContext)
                .execute());
        assertThat(count).isEqualTo(1);

        Optional<EmbeddedTestEntity> result = sqlMapper.selectBySqlFile(EmbeddedTestEntity.class, "/sqltemplate/embedded_selectById.sql", templateContext)
                .getOptionalResult();
        assertThat(result).isEmpty();

    }


}
