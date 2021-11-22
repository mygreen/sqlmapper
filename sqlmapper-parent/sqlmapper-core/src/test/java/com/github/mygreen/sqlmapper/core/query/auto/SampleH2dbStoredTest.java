package com.github.mygreen.sqlmapper.core.query.auto;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.test.config.TestConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class SampleH2dbStoredTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void testJdbcCallWithProcedure() {

        // ResultSetのマップのキーを大文字・小文字区別のないのLinkedCaseInsensitiveMap にする。
        jdbcTemplate.setResultsMapCaseInsensitive(true);

        // ResultSetの処理を行わない
//        jdbcTemplate.setSkipResultsProcessing(true);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("is_prime_number");

//        jdbcCall.se

        // H2DBの場合は、ResultSetで戻り値を取得する必要がある。
        jdbcCall.addDeclaredParameter(new SqlParameter("param1", Types.INTEGER));
        jdbcCall.addDeclaredParameter(new SqlReturnResultSet("return", new RowMapper<Boolean>() {

            @Override
            public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
//                return rs.getBoolean("return");
//                int index = rs.findColumn("return");
//                return rs.getBoolean(index);
                // 名前でなく列で取得する必要がある。
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                System.out.printf("columnCount=%d\n", columnCount);

                // カラム名「PUBLIC.IS_PRIME_NUMBER(?1)」
                System.out.printf("columnName=%s\n", meta.getColumnName(1));

                return rs.getBoolean(1);
            }

        }));

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("param1", 3);

//        Map<String, Object> ret = jdbcCall.execute(3);
        Map<String, Object> ret = jdbcCall.execute(param);
//        System.out.println(ret.get("return"));
        System.out.println(ret);
    }

    /**
     * H2の場合は、executeFunctionだと失敗する。
     */
    @Disabled("H2の場合は、ストアドファンクションの場合は失敗するため。")
    @Test
    void testFunc_is_prime_number() {

        // ResultSetのマップのキーを大文字・小文字区別のないのLinkedCaseInsensitiveMap にする。
        jdbcTemplate.setResultsMapCaseInsensitive(true);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("is_prime_number");

        // H2DBの場合は、ResultSetで戻り値を取得する必要がある。
        jdbcCall.addDeclaredParameter(new SqlParameter("value", Types.INTEGER));
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("value", 3);

        Boolean ret = jdbcCall.executeFunction(Boolean.class, param);
        System.out.println(ret);
    }

    /**
     * CallableStatementを使用して直接呼ぶ
     */
    @Test
    void testDirectCall2() {


        jdbcTemplate.setResultsMapCaseInsensitive(true);

        List<SqlParameter> declaredParams = new ArrayList<>();
        declaredParams.add(new SqlParameter("value", Types.INTEGER));
        declaredParams.add(new SqlReturnResultSet("return", new RowMapper<Boolean>() {

            @Override
            public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getBoolean(1);
            }

        }));

        Map<String, Object> ret = jdbcTemplate.call(new CallableStatementCreator() {

            @Override
            public CallableStatement createCallableStatement(final Connection con) throws SQLException {

                CallableStatement stmnt = con.prepareCall("{? = call is_prime_number(?)}");
                stmnt.setInt(2, 3);

                return stmnt;
            }
        }, declaredParams);

        System.out.println(ret);

    }

    @Test
    void testDirectCall3() {


        jdbcTemplate.setResultsMapCaseInsensitive(true);

        List<SqlParameter> declaredParams = new ArrayList<>();
        declaredParams.add(new SqlParameter("value", Types.INTEGER));
        declaredParams.add(new SqlReturnResultSet("return", new RowMapper<Boolean>() {

            @Override
            public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getBoolean(1);
            }

        }));

        Map<String, Object> ret = jdbcTemplate.call(new CallableStatementCreator() {

            @Override
            public CallableStatement createCallableStatement(final Connection con) throws SQLException {

                CallableStatement stmnt = con.prepareCall("{call is_prime_number(?)}");
                stmnt.setInt(1, 3);

                return stmnt;
            }
        }, declaredParams);

        System.out.println(ret);

    }

}
