package com.github.mygreen.sqlmapper.core.query;

import com.github.mygreen.sqlmapper.core.SqlMapperContext;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * クエリ実行のサポートクラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <Q> クエリの実行条件。
 *
 */
@RequiredArgsConstructor
public abstract class QueryExecutorSupport<Q extends QuerySupport<?>> {

    /**
     * クエリの実行条件
     */
    @Getter
    protected final Q query;

    /**
     * 設定情報
     */
    @Getter
    protected final SqlMapperContext context;

    public QueryExecutorSupport(@NonNull Q query) {
        this.query = query;
        this.context = query.getContext();
    }

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
