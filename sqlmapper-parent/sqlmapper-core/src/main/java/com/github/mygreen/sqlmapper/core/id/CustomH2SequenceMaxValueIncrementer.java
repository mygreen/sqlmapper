package com.github.mygreen.sqlmapper.core.id;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;

/**
 * H2DB用の非Oracleモード用のシーケンスのインクリメント処理。
 * <p>v2.xからデフォルトが非Oracleモードとなったため。
 *
 * @since 0.3.2
 * @author T.TSUCHIE
 *
 */
public class CustomH2SequenceMaxValueIncrementer extends AbstractSequenceMaxValueIncrementer {

    /**
     * コンストラクタ。
     * @param dataSource データソース
     * @param sequenceName シーケンス名
     */
    public CustomH2SequenceMaxValueIncrementer(DataSource dataSource, String sequenceName) {
        super(dataSource, sequenceName);
    }


    @Override
    protected String getSequenceQuery() {
        return "values next value for " + getIncrementerName();
    }
}
