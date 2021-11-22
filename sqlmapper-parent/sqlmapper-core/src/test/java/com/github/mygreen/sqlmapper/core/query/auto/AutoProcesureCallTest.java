package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.annotation.In;
import com.github.mygreen.sqlmapper.core.annotation.ResultSet;
import com.github.mygreen.sqlmapper.core.test.config.TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;

import lombok.Data;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class AutoProcesureCallTest {

    @Autowired
    SqlMapper sqlMapper;

    @DisplayName("戻り値が1つのカラムの場合")
    @Test
    public void test_singleColumn() {

        SingleColumnParam param = new SingleColumnParam();
        param.setValue(3);

        sqlMapper.call("is_prime_number", param)
            .execute();

        assertThat(param.getResult()).isTrue();

    }

    @DisplayName("戻り値が複数カラムの場合")
    @Test
    public void test_multiColumn() {

        MultiColumnParam param = new MultiColumnParam();
        param.setName("Taro");

        sqlMapper.call("FIND_CUSTOMER_BY_NAME", param)
            .execute();

        assertThat(param.result).hasSize(1);
//        System.out.println(param.result);

    }

    @Data
    public static class SingleColumnParam {

        @In
        private Integer value;

        @ResultSet
        private Boolean result;

    }

    @Data
    public static class MultiColumnParam {

        @In
        private String name;

        @ResultSet
        private List<Customer> result;
    }
}
