package com.github.mygreen.sqlmapper.core.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.query.SelectForUpdateType;

/**
 * Oracle v12+用の方言の定義。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class OracleDialect extends DialectBase {

    /**
     * {@inheritDoc}
     *
     * @return {@literal "oracle"}を返します。
     */
    @Override
    public String getName() {
        return "oracle";
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * <ul>
     *  <li>{@link GenerationType#IDENTITY} : {@literal true}</li>
     *  <li>{@link GenerationType#SEQUENCE} : {@literal true}</li>
     *  <li>{@link GenerationType#TABLE} : {@literal true}</li>
     *  <li>その他 : {@literal false}</li>
     * </ul>
     */
    @Override
    public boolean isSupportedGenerationType(GenerationType generationType) {
        switch(generationType) {
            case IDENTITY:
                return true;
            case SEQUENCE:
                return true;
            case TABLE:
                return true;
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link OracleSequenceMaxValueIncrementer} のインスタンスを返す。
     */
    @Override
    public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName) {
        return new OracleSequenceMaxValueIncrementer(dataSource, sequenceName);
    }

    /**
     * {@inheritDoc}
     *
     * @return コメントの形式 /{@literal *}+ヒント{@literal *}/ の形式で返します。
     */
    @Override
    public String getHintComment(final String hint) {
        return "/*+ " + hint + " */ ";
    }

    /**
     * {@inheritDoc}
     *
     * @return 必ず{@literal true} を返します。
     */
    @Override
    public boolean isSupportedSelectForUpdate(final SelectForUpdateType type) {
        // 全てのタイプをサポートする
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * <ul>
     *   <li>{@link SelectForUpdateType#NORMAL} : {@literal for update}</li>
     *   <li>{@link SelectForUpdateType#NOWAIT} : {@literal for update nowait}</li>
     *   <li>{@link SelectForUpdateType#WAIT} : {@literal for update wait <waitSeconds>}</li>
     * </ul>
     */
    @Override
    public String getForUpdateSql(final SelectForUpdateType type, final int waitSeconds) {

        StringBuilder buf = new StringBuilder(20)
                .append(" for update");

        switch(type) {
            case NORMAL:
                break;
            case NOWAIT:
                buf.append(" nowait");
                break;
            case WAIT:
                buf.append(" wait ").append(waitSeconds);
                break;
        }

        return buf.toString();
    }
}
