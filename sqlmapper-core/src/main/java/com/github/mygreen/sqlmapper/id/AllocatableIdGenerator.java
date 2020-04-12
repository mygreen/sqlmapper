package com.github.mygreen.sqlmapper.id;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AllocatableIdGenerator {

    /**
     * 割り当てサイズ
     */
    @Getter
    private final long allocationSize;

    /**
     * 割り当てているIDのキャッシュ。
     */
    private Map<String, IdContext> idContextMap = new ConcurrentHashMap<>();

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
     * 次のID番号を取得する。
     * @param sequenceName
     * @return
     */
    public long nextValue(final String key) {
        return idContextMap.computeIfAbsent(key, k -> new IdContext()).getNextValue(key);

    }

    /**
     * 自動生成される識別子の情報を保持する。
     *
     *
     * @author T.TSUCHIE
     *
     */
    public class IdContext {

        /**
         * 現在値
         */
        protected long currentValue = -1l;

        /**
         * 割り当て済みの値
         */
        protected long allocated = -1L;

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
