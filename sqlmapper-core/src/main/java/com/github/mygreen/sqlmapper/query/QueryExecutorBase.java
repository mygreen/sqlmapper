package com.github.mygreen.sqlmapper.query;

import com.github.mygreen.sqlmapper.SqlMapperContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * クエリ実行のヘルパクラス
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> エンティティのタイプ
 */
@RequiredArgsConstructor
public abstract class QueryExecutorBase {

    @Getter
    protected final SqlMapperContext context;

    /**
     * 準備が完了している場合に<code>true</code>です。
     */
    private boolean completed = false;

    /**
     * 準備を完了した状態にします。
     */
    protected void completed() {
        this.completed = true;
    }

    /**
     * クエリ実行の準備が完了していないことをチェックします。
     *
     * @param methodName メソッド名
     * @throws IllegalOperateException クエリ準備が完了しているにも関わらず2度呼び出された場合にスローされます。
     */
    protected void assertNotCompleted(final String methodName) {
        if(!completed) {
            throw new IllegalOperateException(context.getMessageFormatter().create("query.notPrepared")
                    .paramWithClass("classType", getClass())
                    .param("methodName", methodName)
                    .format());
        }
    }

    /**
     * クエリ実行の準備を行います。
     */
    public abstract void prepare();

}
