package com.github.mygreen.sqlmapper.core.id;

import static org.assertj.core.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.config.TableIdGeneratorProperties;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;


/**
 * {@link TableIdIncrementer} のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class TableIdIncrementerTest extends QueryTestSupport {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TableIdGeneratorProperties tableIdGeneratorPropertie;

    @BeforeEach
    void beforeMethod() {
        resetData();
    }

    private TableIdContext createDefaultContext() {

        TableIdContext context = new TableIdContext();
        context.setCatalog(tableIdGeneratorPropertie.getCatalog());
        context.setSchema(tableIdGeneratorPropertie.getSchema());
        context.setTable(tableIdGeneratorPropertie.getTable());
        context.setPkColumn(tableIdGeneratorPropertie.getPkColumn());
        context.setValueColumn(tableIdGeneratorPropertie.getValueColumn());
        context.setInitialValue(tableIdGeneratorPropertie.getInitialValue());
        context.setAllocationSize(tableIdGeneratorPropertie.getAllocationSize());

        return context;
    }

    @Test
    public void testAllocationSize1() {

        String sequenceName = "TestSeq1";

        TableIdContext context = createDefaultContext();
        context.setAllocationSize(1l);
        TableIdIncrementer incremeter = new TableIdIncrementer(new JdbcTemplate(dataSource), context);

        // 初期値
        long initialValue = txNew().execute(action -> incremeter.getCurrentValue(sequenceName));
        assertThat(initialValue).isEqualTo(0l);

        for(int i=0; i < 10; i++) {

            // 次の値
            long nextValue = txNew().execute(action -> incremeter.nextValue(sequenceName));
            assertThat(nextValue).isEqualTo((long)(i));

            // 現在のDBのシーケンスの値
            long currentValue = txNew().execute(action -> incremeter.getCurrentValue(sequenceName));
            assertThat(currentValue).isEqualTo((long)(i/context.getAllocationSize()+1)*context.getAllocationSize());
            System.out.printf("i=%d, nextValue=%d, currentValue=%d\n", i, nextValue, currentValue);
        }

    }

    @Test
    public void testAllocationSize2() {

        String sequenceName = "TestSeq2";

        TableIdContext context = createDefaultContext();
        context.setAllocationSize(2l);
        TableIdIncrementer incremeter = new TableIdIncrementer(new JdbcTemplate(dataSource), context);

        // 初期値
        long initialValue = txNew().execute(action -> incremeter.getCurrentValue(sequenceName));
        assertThat(initialValue).isEqualTo(0l);

        for(int i=0; i < 10; i++) {

            // 次の値
            long nextValue = txNew().execute(action -> incremeter.nextValue(sequenceName));
            assertThat(nextValue).isEqualTo((long)(i));

            // 現在のDBのシーケンスの値
            long currentValue = txNew().execute(action -> incremeter.getCurrentValue(sequenceName));
            assertThat(currentValue).isEqualTo((long)(i/context.getAllocationSize()+1)*context.getAllocationSize());
            System.out.printf("i=%d, nextValue=%d, currentValue=%d\n", i, nextValue, currentValue);

        }

    }

    @Test
    public void testAllocationSize5() {

        String sequenceName = "TestSeq5";

        TableIdContext context = createDefaultContext();
        context.setAllocationSize(5l);
        TableIdIncrementer incremeter = new TableIdIncrementer(new JdbcTemplate(dataSource), context);

        // 初期値
        long initialValue = txNew().execute(action -> incremeter.getCurrentValue(sequenceName));
        assertThat(initialValue).isEqualTo(0l);

        for(int i=0; i < 20; i++) {

            // 次の値
            long nextValue = txNew().execute(action -> incremeter.nextValue(sequenceName));
            assertThat(nextValue).isEqualTo((long)(i));

            // 現在のDBのシーケンスの値
            long currentValue = txNew().execute(action -> incremeter.getCurrentValue(sequenceName));
            assertThat(currentValue).isEqualTo((long)(i/context.getAllocationSize()+1)*context.getAllocationSize());
            System.out.printf("i=%d, nextValue=%d, currentValue=%d\n", i, nextValue, currentValue);

        }

    }
}
