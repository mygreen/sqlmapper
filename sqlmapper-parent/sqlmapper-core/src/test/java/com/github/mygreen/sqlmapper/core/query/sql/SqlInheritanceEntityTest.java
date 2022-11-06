package com.github.mygreen.sqlmapper.core.query.sql;

import static org.assertj.core.api.Assertions.*;

import java.sql.Timestamp;
import java.util.List;
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
import com.github.mygreen.sqlmapper.core.test.entity.InheritanceTestEntity;

/**
 * SQLテンプレートによる継承型のエンティティのテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class SqlInheritanceEntityTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_inheritance.sql");
    }

    @Test
    void testSqlSelectByFile() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("createAt", Timestamp.valueOf("2021-01-03 00:00:00")));

        List<InheritanceTestEntity> result = sqlMapper.selectBySqlFile(InheritanceTestEntity.class, "/sqltemplate/inheritance_selectByCreateAt.sql", templateContext)
                .getResultList();

        assertThat(result).hasSize(3);

        for(InheritanceTestEntity entity : result) {
            assertThat(entity.getCreateAt()).isAfterOrEqualTo(Timestamp.valueOf("2021-01-03 00:00:00"));
        }

    }

    @Test
    void testSqlCountBySql() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("createAt", Timestamp.valueOf("2021-01-03 00:00:00")));

        long result = sqlMapper.getCountBySqlFile("/sqltemplate/inheritance_selectByCreateAt.sql", templateContext);

        assertThat(result).isEqualTo(3l);

    }

    @Test
    void testSqlUpdateBySql_delete() {

        SqlTemplateContext<?> templateContext = new MapSqlTemplateContext(Map.of("createAt", Timestamp.valueOf("2021-01-03 00:00:00")));

        int count = txNew().execute(action -> sqlMapper.updateBySqlFile("/sqltemplate/inheritance_deleteByCreateAt.sql", templateContext)
                .execute());
        assertThat(count).isEqualTo(3);

        List<InheritanceTestEntity> result = sqlMapper.selectBySqlFile(InheritanceTestEntity.class, "/sqltemplate/inheritance_selectByCreateAt.sql", templateContext)
                .getResultList();
        assertThat(result).isEmpty();

    }
}
