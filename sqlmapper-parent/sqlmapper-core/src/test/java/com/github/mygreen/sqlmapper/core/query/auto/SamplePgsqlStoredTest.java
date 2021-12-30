package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.annotation.In;
import com.github.mygreen.sqlmapper.core.annotation.Out;
import com.github.mygreen.sqlmapper.core.test.config.PgsqlTestConfig;

import lombok.Data;

@Disabled("サンプルのため対象外")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=PgsqlTestConfig.class)
public class SamplePgsqlStoredTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SqlMapper sqlMapper;

    /**
     * ストアドファンクションを
     */
    @Test
    void testProc_increment() {

        // ResultSetのマップのキーを大文字・小文字区別のないのLinkedCaseInsensitiveMap にする。
        jdbcTemplate.setResultsMapCaseInsensitive(true);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("test_func_increment");

        jdbcCall.addDeclaredParameter(new SqlParameter("i", Types.INTEGER));
        jdbcCall.addDeclaredParameter(new SqlReturnResultSet("return", new RowMapper<Integer>() {

            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }

        }));

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("i", 3);

        Map<String, Object> ret = jdbcCall.execute(param);
        System.out.println(ret.get("return"));
    }

    @DisplayName("ストアドファンクション(名前付き)")
    @Test
    void testFunction_increment() {

        jdbcTemplate.setResultsMapCaseInsensitive(true);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("test_func_increment");

        jdbcCall.addDeclaredParameter(new SqlParameter("i", Types.INTEGER));
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("i", 3);

        Integer ret = jdbcCall.executeFunction(Integer.class, param);
        assertThat(ret).isEqualTo(4);
    }

    @DisplayName("ストアドファンクション(名前なし)")
    @Test
    void testFunction_add() {

        jdbcTemplate.setResultsMapCaseInsensitive(true);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("test_func_add");

        // 引数名を付けない場合は、暗黙的な名前にする
        jdbcCall.addDeclaredParameter(new SqlParameter("$1", Types.INTEGER));
        jdbcCall.addDeclaredParameter(new SqlParameter("$2", Types.INTEGER));
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("$1", 1)
                .addValue("$2", 3);

        Integer ret = jdbcCall.executeFunction(Integer.class, param);
        assertThat(ret).isEqualTo(4);
    }

    /**
     * 名前を付けない場合
     */
    @Test
    void testFunction_add_nonName() {

        jdbcTemplate.setResultsMapCaseInsensitive(true);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("test_func_add");

        // 引数名を付けない場合
        List<SqlParameterValue> param = new ArrayList<SqlParameterValue>();
        param.add(new SqlParameterValue(Types.INTEGER, 1));
        param.add(new SqlParameterValue(Types.INTEGER, 3));

        Integer ret = jdbcCall.executeFunction(Integer.class, param.toArray());
        assertThat(ret).isEqualTo(4);
    }

    @DisplayName("ストアドファンクション(OUTパラメータ2つ)")
    @Test
    void testFunction_split_daytime() {

        jdbcTemplate.setResultsMapCaseInsensitive(true);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("test_func_split_daytime");

        jdbcCall.addDeclaredParameter(new SqlParameter("day", Types.VARCHAR));

        jdbcCall.addDeclaredParameter(new SqlOutParameter("year", Types.INTEGER, new RowMapper<Integer>() {

            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("year");
//                return rs.getInt(1);
            }
        }));

        jdbcCall.addDeclaredParameter(new SqlOutParameter("date", Types.INTEGER, new RowMapper<Integer>() {

            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
//                return rs.getInt("date");
                return rs.getInt(1);
            }
        }));

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("day", "20210807");

        Map<String, Object> ret = jdbcCall.execute(param);
        assertThat(ret).containsEntry("year", 2021);
        assertThat(ret).containsEntry("date", 807);
    }

    /**
     * OUTパラメータのときのexecuteFunction呼び出し。
     * ⇒うまくいかない。
     */
    @Test
    void testFunction_split_daytime2() {

        jdbcTemplate.setResultsMapCaseInsensitive(true);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("test_func_split_daytime");

        jdbcCall.addDeclaredParameter(new SqlParameter("day", Types.VARCHAR));

        jdbcCall.addDeclaredParameter(new SqlOutParameter("year", Types.INTEGER, new RowMapper<Integer>() {

            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("year");
            }
        }));

        jdbcCall.addDeclaredParameter(new SqlOutParameter("date", Types.INTEGER, new RowMapper<Integer>() {

            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("date");
            }
        }));

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("day", "20210807");

        Object ret = jdbcCall.executeFunction(Object.class, param);
        System.out.println(ret);
    }

    /**
     * ストアドプロシージャ、引数なし
     */
    @Test
    void testProc_no_args() {

        jdbcTemplate.setResultsMapCaseInsensitive(true);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withoutProcedureColumnMetaDataAccess()
                .withProcedureName("test_proc_hello");

        Object ret = jdbcCall.execute();

    }

    @Test
    void testAutoProcedureCall_split_daytime() {

        SplitDaytimeParam param = new SplitDaytimeParam();
        param.setTarget("20210807");

        sqlMapper.call("test_func_split_daytime", param)
            .execute();
        System.out.println(param);

    }

    @Test
    void testAutoFunctionCall_split_daytime() {

        SplitDaytimeParam param = new SplitDaytimeParam();
        param.setTarget("20210807");

        sqlMapper.call(Void.class, "test_func_split_daytime", param)
            .execute();
        assertThat(param.year).isEqualTo(2021);
        assertThat(param.date).isEqualTo(807);
        System.out.println(param);


    }

    @Test
    void testAutoProcedureCall_hello() {

       sqlMapper.call("test_proc_hello")
           .execute();

    }

    @DisplayName("ストアドファンクション - 引数なし")
    @Test
    void testAutoFunctionCall_hello() {

        String result = sqlMapper.call(String.class, "test_func_hello")
            .execute();
        assertThat("Hello World!").isEqualTo(result);

    }

    @DisplayName("ストアドファンクション - 引数あり")
    @Test
    void testAutoFunctionCall_increment() {

        IncrementParam param = new IncrementParam();
        param.setI(3);
        int result = sqlMapper.call(int.class, "test_func_increment", param)
                .execute();
        assertThat(4).isEqualTo(result);
    }

//    @DisplayName("ストアドファンクション - 戻り値がリスト")
//    @Test
//    void testAutoFunctionCall_list() {
//
//        List<String> result = sqlMapper.call(String.class, "test_func_customer_name_list2")
//                .execute();
//
////        CustomerListParam param = new CustomerListParam();
////        sqlMapper.call("test_func_customer_name_list", param)
////            .execute();
////        System.out.println(param);
//
//    }

    /**
     * 複数行を返すファンクション - 1行しか取れない
     */
    @Test
    void testDirectFunctionCall_list() {


        jdbcTemplate.setResultsMapCaseInsensitive(true);

        List<SqlParameter> declaredParams = new ArrayList<>();
        declaredParams.add(new SqlOutParameter("1", Types.VARCHAR));

        Map<String, Object> ret = jdbcTemplate.call(new CallableStatementCreator() {

            @Override
            public CallableStatement createCallableStatement(final Connection con) throws SQLException {

                CallableStatement stmnt = con.prepareCall("{? = call test_func_customer_name_list2()}");
                stmnt.registerOutParameter(1, Types.VARCHAR);

                return stmnt;
            }
        }, declaredParams);

        System.out.println(ret);

    }

    @Test
    void testDirectFunctionCall_list2() {


        jdbcTemplate.setResultsMapCaseInsensitive(true);

        List<SqlParameter> declaredParams = new ArrayList<>();
//        declaredParams.add(new SqlParameter("value", Types.INTEGER));
//        declaredParams.add(new SqlReturnResultSet("returnvalue", new RowMapper<String>() {
//
//            @Override
//            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
//                return rs.getString(0);
//            }
//
//        }));
        declaredParams.add(new SqlOutParameter("1", Types.ARRAY));
//        declaredParams.add(new SqlOutParameter("returnvalue", Types.VARCHAR));


        Map<String, Object> ret = jdbcTemplate.call(new CallableStatementCreator() {

            @Override
            public CallableStatement createCallableStatement(final Connection con) throws SQLException {

                CallableStatement stmnt = con.prepareCall("{? = call test_func_customer_name_list2()}");
                stmnt.registerOutParameter(1, Types.ARRAY);

                return stmnt;
            }
        }, declaredParams);
//        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
//                .withFunctionName("test_func_customer_name_list2");
//
//        Map<String, Object> ret = jdbcCall.execute(declaredParams);
//        String ret = jdbcCall.executeFunction(String.class, declaredParams);

        System.out.println(ret);

    }


    @Data
    static class SplitDaytimeParam {

        @In(name = "day")
        String target;

        @Out
        Integer year;

        @Out
        Integer date;

    }

    @Data
    static class IncrementParam {

        int i;

    }

//    @Data
    static class CustomerListParam {

        @com.github.mygreen.sqlmapper.core.annotation.ResultSet
        List<String> result;

    }


}
