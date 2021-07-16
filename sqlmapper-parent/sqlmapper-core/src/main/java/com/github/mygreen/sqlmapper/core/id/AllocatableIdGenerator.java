package com.github.mygreen.sqlmapper.core.id;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 挿入前に予めIDを生成を行うID生成を行う抽象クラスです。
 * <p>大量にレコードを導入するときは効率的に処理を行うことができます。
 * <p>ただし、予めIDを生成してキャッシュしておくため、プロセスを再起動すると生成済みのキャッシュされたIDは使われず欠番となります。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public abstract class AllocatableIdGenerator {

    /**
     * 割り当てサイズ
     */
    @Getter
    protected final long allocationSize;

    /**
     * 割り当てているIDのキャッシュ。
     */
    protected Map<String, AllocatedIdContext> allocatedIdCache = new ConcurrentHashMap<>();

    /**
     * 現在のカウンターの値を取得する。
     * @param key 取得するシーケンス名
     * @return 現在のカウンターの値
     */
    protected abstract long getCurrentValue(String key);

    /**
     * 新たに値を割り当てる。
     * @param key 割り当てるキーの名称
     * @param allocationSize 割り当てる値
     * @return 割り当て後のカウンターの値
     */
    protected abstract long allocateValue(String key, long allocationSize);

    /**
     * 新しいIDを取得します。
     * @param key シーケンス名
     * @return 新たらしいID
     */
    public long nextValue(final String key) {
        return allocatedIdCache.computeIfAbsent(key, k -> new AllocatedIdContext()).getNextValue(key);

    }

    /**
     * 割り当てられたIDの情報を保持する。
     *
     *
     * @author T.TSUCHIE
     *
     */
    public class AllocatedIdContext {

        /**
         * 現在値
         */
        private long currentValue = -1l;

        /**
         * 割り当て済みの値
         */
        private long allocated = -1L;

        /**
         * 新しいIDを払い出します。
         * <p>未割当のキャッシュしているIDがあれば、そちらを払い出します。
         *
         * @param key キー名
         * @return 新しいIDを返します。
         */
        public synchronized long getNextValue(final String key) {

            if(currentValue < 0l) {
                this.currentValue = getCurrentValue(key);
            }

            if(allocated < 0l || allocated >= allocationSize) {
                currentValue = allocateValue(key, allocationSize) - allocationSize;
                this.allocated = 1l;
            }

            if(allocated < allocationSize) {
                return currentValue + allocated++;
            }

            return currentValue;

        }

    }

}
