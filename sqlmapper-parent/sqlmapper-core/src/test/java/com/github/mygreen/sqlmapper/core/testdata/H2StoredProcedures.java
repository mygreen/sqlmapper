package com.github.mygreen.sqlmapper.core.testdata;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * H2DB用のストアドファンクション
 *
 * <p>H2のプロシージャの仕様:{@link http://www.h2database.com/html/features.html#user_defined_functions}
 *
 * @author T.TSUCHIE
 *
 */
public class H2StoredProcedures {

    /**
     * 整数が素数かどうか判定します。
     * @param value 判定対象の値
     * @return 素数かどうか。
     */
    public static boolean isPrime(final int value) {
        return BigInteger.valueOf(value).isProbablePrime(100);
    }

    public static ResultSet findCustomerByName(final Connection con, final String name) throws SQLException {

        String sql = "select * from customer where first_name like ? or last_name like ?";
        PreparedStatement st = con.prepareStatement(sql);

        st.setString(1, "%" + name + "%");
        st.setString(2, "%" + name + "%");

        return st.executeQuery();

    }

}
