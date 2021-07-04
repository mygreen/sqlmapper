package com.github.mygreen.sqlmapper.metamodel.support;

/**
 * 式を文字列として評価するためのデバッグ用のVisitorのコンテキスト
 *
 *
 * @author T.TSUCHIE
 *
 */
public class DebugVisitorContext {

    /**
     * 組み立ててたクライテリア
     */
    private StringBuilder criteria = new StringBuilder();

    /**
     * クライテリアを追加します。
     * @param text 追加する文字列
     * @return 現在のクライテリア
     */
    public StringBuilder append(final String text) {
        this.criteria.append(text);
        return criteria;
    }

    /**
     * 組み立てたクライテリアを取得します。
     * @return
     */
    public String getCriteria() {
        return criteria.toString();
    }
}
