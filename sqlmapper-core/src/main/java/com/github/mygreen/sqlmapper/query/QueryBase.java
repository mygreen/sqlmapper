package com.github.mygreen.sqlmapper.query;

import com.github.mygreen.sqlmapper.SqlMapperContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * クエリの抽象クラス。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> エンティティのタイプ
 *
 */
@RequiredArgsConstructor
public abstract class QueryBase<T> {

    @Getter
    protected final SqlMapperContext context;

    /**
     * クエリが完了している場合に<code>true</code>です。
     */
    protected boolean completed;

    /**
     * クエリ実行を完了した状態にします。
     */
    protected void completed() {
        this.completed = true;
    }

    /**
     * このクエリが完了していないことをチェックします。
     *
     * @param methodName メソッド名
     * @throws IllegalOperateException クエリ実行が完了しているにも関わらず2度呼び出された場合にスローされます。
     */
    protected void assertNotCompleted(final String methodName) {
        if(completed) {
            throw new IllegalOperateException(context.getMessageBuilder().create("query.twiceExecution")
                    .varWithClass("classType", getClass())
                    .var("methodName", methodName)
                    .format());
        }
    }
}
