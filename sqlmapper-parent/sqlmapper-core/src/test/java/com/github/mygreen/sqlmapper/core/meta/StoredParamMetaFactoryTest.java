package com.github.mygreen.sqlmapper.core.meta;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.annotation.Column;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.In;
import com.github.mygreen.sqlmapper.core.annotation.InOut;
import com.github.mygreen.sqlmapper.core.annotation.Out;
import com.github.mygreen.sqlmapper.core.annotation.ResultSet;
import com.github.mygreen.sqlmapper.core.testdata.NoDbTestConfig;

import lombok.Data;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=NoDbTestConfig.class)
public class StoredParamMetaFactoryTest {

    @Autowired
    private StoredParamMetaFactory storedParamMetaFactory;

    @Test
    void testStandard() {

        StoredParamMeta paramMeta = storedParamMetaFactory.create(StandardParam.class);

        assertThat(paramMeta.getParamType()).isEqualTo(StandardParam.class);
        assertThat(paramMeta.isAnonymouse()).isFalse();

        assertThat(paramMeta.getAllPropertyMeta()).hasSize(9);

        int assertCount = 0;
        for(StoredPropertyMeta propertyMeta : paramMeta.getAllPropertyMeta()) {

            if(propertyMeta.getName().equals("inParam1")) {
                assertThat(propertyMeta.getParamName()).isEqualTo("in_param1");
                assertThat(propertyMeta.isIn()).isTrue();
                assertCount++;

            } else if(propertyMeta.getName().equals("inParam2")) {
                assertThat(propertyMeta.getParamName()).isEqualTo("in_param2");
                assertThat(propertyMeta.isIn()).isTrue();
                assertCount++;

            } else if(propertyMeta.getName().equals("inParam3")) {
                assertThat(propertyMeta.getParamName()).isEqualTo("in_custom_param3");
                assertThat(propertyMeta.isIn()).isTrue();
                assertCount++;

            } else if(propertyMeta.getName().equals("outParam1")) {
                assertThat(propertyMeta.getParamName()).isEqualTo("out_param1");
                assertThat(propertyMeta.isOut()).isTrue();
                assertCount++;

            } else if(propertyMeta.getName().equals("outParam2")) {
                assertThat(propertyMeta.getParamName()).isEqualTo("out_custom_param2");
                assertThat(propertyMeta.isOut()).isTrue();
                assertCount++;

            } else if(propertyMeta.getName().equals("inOutParam1")) {
                assertThat(propertyMeta.getParamName()).isEqualTo("in_out_param1");
                assertThat(propertyMeta.isInOut()).isTrue();
                assertCount++;

            } else if(propertyMeta.getName().equals("inOutParam2")) {
                assertThat(propertyMeta.getParamName()).isEqualTo("in_out_custom_param2");
                assertThat(propertyMeta.isInOut()).isTrue();
                assertCount++;

            } else if(propertyMeta.getName().equals("resultSet1")) {
                assertThat(propertyMeta.getParamName()).isEqualTo("result_set1");
                assertThat(propertyMeta.isResultSet()).isTrue();
                assertCount++;

            } else if(propertyMeta.getName().equals("resultSet2")) {
                assertThat(propertyMeta.getParamName()).isEqualTo("result_set_custom2");
                assertThat(propertyMeta.isResultSet()).isTrue();
                assertCount++;

            }

        }

        assertThat(assertCount).isEqualTo(9);

    }

    @Test
    void testResultSet_Bean() {

        StoredParamMeta paramMeta = storedParamMetaFactory.create(BeanResultSetParam.class);

        assertThat(paramMeta.getParamType()).isEqualTo(BeanResultSetParam.class);
        assertThat(paramMeta.isAnonymouse()).isFalse();

        assertThat(paramMeta.getAllPropertyMeta()).hasSize(1);

        StoredPropertyMeta propertyMeta = paramMeta.getPropertyMeta("result").get();
        assertThat(propertyMeta.getAllNestedPopertyMetaList()).hasSize(3);
        int assertCount = 0;
        for(PropertyMeta nestPropertyMeta : propertyMeta.getAllNestedPopertyMetaList()) {

            if(nestPropertyMeta.getName().equals("id")) {
                assertThat(nestPropertyMeta.getColumnMeta().getName()).isEqualTo("ID");
                assertCount++;

            } else if(nestPropertyMeta.getName().equals("firstName")) {
                assertThat(nestPropertyMeta.getColumnMeta().getName()).isEqualTo("FIRST_NAME");
                assertCount++;

            } else if(nestPropertyMeta.getName().equals("birthday")) {
                assertThat(nestPropertyMeta.getColumnMeta().getName()).isEqualTo("date");
                assertCount++;

            }
        }

        assertThat(assertCount).isEqualTo(3);


    }

    @Test
    void testResultSet_ListBean() {

        StoredParamMeta paramMeta = storedParamMetaFactory.create(ListBeanResultSetParam.class);

        assertThat(paramMeta.getParamType()).isEqualTo(ListBeanResultSetParam.class);
        assertThat(paramMeta.isAnonymouse()).isFalse();

        assertThat(paramMeta.getAllPropertyMeta()).hasSize(1);

        StoredPropertyMeta propertyMeta = paramMeta.getPropertyMeta("result").get();
        assertThat(propertyMeta.getAllNestedPopertyMetaList()).hasSize(3);
        int assertCount = 0;
        for(PropertyMeta nestPropertyMeta : propertyMeta.getAllNestedPopertyMetaList()) {

            if(nestPropertyMeta.getName().equals("id")) {
                assertThat(nestPropertyMeta.getColumnMeta().getName()).isEqualTo("ID");
                assertCount++;

            } else if(nestPropertyMeta.getName().equals("firstName")) {
                assertThat(nestPropertyMeta.getColumnMeta().getName()).isEqualTo("FIRST_NAME");
                assertCount++;

            } else if(nestPropertyMeta.getName().equals("birthday")) {
                assertThat(nestPropertyMeta.getColumnMeta().getName()).isEqualTo("date");
                assertCount++;

            }
        }

        assertThat(assertCount).isEqualTo(3);


    }

    @Data
    public static class StandardParam {

        /**
         * アノテーションなし
         */
        private int inParam1;

        @In
        private String inParam2;

        @In(name = "in_custom_param3")
        private String inParam3;

        @Out
        private String outParam1;

        @Out(name = "out_custom_param2")
        private Boolean outParam2;

        @InOut
        private String inOutParam1;

        @InOut(name = "in_out_custom_param2")
        private Boolean inOutParam2;

        @ResultSet
        private Boolean resultSet1;

        @ResultSet(name = "result_set_custom2")
        private Float resultSet2;

    }

    @Data
    public static class BeanResultSetParam {

        @ResultSet
        private Customer result;

    }

    @Data
    public static class ListBeanResultSetParam {

        @ResultSet
        private List<Customer> result;

    }

    @Data
    public static class Customer {

        @Id
        private long id;

        private String firstName;

        @Column(name = "date")
        private LocalDate birthday;
    }


}
