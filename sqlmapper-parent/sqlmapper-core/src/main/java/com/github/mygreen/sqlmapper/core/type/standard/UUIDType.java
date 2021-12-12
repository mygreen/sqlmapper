package com.github.mygreen.sqlmapper.core.type.standard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.type.ValueType;

/**
 * 文字列型を{@link UUID} 型のマッピングを処理します。
 * <p>DB側がUUID型に対応していない場合、永続化する際に文字列に変換して対応します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class UUIDType implements ValueType<UUID> {

    @Override
    public UUID getValue(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        return value != null ? UUID.fromString(value.toString()) : null;
    }

    @Override
    public Object getSqlParameterValue(UUID value) {
        /*
         * UUIDのオブジェクト型を使用する場合
         * ・PostgreSQLの場合は、OTHERで型指定が必要。
         * ・H2DBの場合は、逆にOTHER指定でエラーが出るため、何も指定しない。
         */
        return value != null ? value.toString() : null;
    }

    @Override
    public int getSqlType(Dialect dialect) {
        return Types.VARCHAR;
    }
}
