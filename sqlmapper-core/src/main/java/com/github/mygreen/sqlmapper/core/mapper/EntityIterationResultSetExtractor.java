package com.github.mygreen.sqlmapper.core.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.github.mygreen.sqlmapper.core.query.IterationCallback;
import com.github.mygreen.sqlmapper.core.query.IterationContext;

import lombok.RequiredArgsConstructor;

/**
 *
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> エンティティタイプ
 */
@RequiredArgsConstructor
public class EntityIterationResultSetExtractor<T, R> implements ResultSetExtractor<R> {

    private final EntityRowMapper<T> entityRowMapper;

    /**
     * 反復コールバック
     */
    private final IterationCallback<T, R> callback;

    @Override
    public R extractData(final ResultSet rs) throws SQLException, DataAccessException {

        final IterationContext iterationContext = new IterationContext();

        R result = null;
        while(rs.next()) {
            int rowNum = rs.getRow();
            T entity = entityRowMapper.mapRow(rs, rowNum);

            iterationContext.setRowNum(rowNum);
            result = callback.iterate(entity, iterationContext);

            if(iterationContext.isExit()) {
                return result;
            }

        }

        return result;
    }

}
