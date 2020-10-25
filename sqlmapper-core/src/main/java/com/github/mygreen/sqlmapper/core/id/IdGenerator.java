    package com.github.mygreen.sqlmapper.core.id;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * 識別子のジェネレータです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface IdGenerator {

    /**
     * サポートしているクラスタイプかどうか
     * @param type 検査対象のクラスタイプ
     * @return trueのときサポート対象
     */
    boolean isSupportedType(Class<?> type);

    /**
     * サポートしているクラスのタイプ一覧を取得する
     * @return サポートしているクラスのタイプ一覧
     */
    Class<?>[] getSupportedTypes();

    /**
     * 識別子を新たに生成します。
     * @return 生成した識別子
     * @throws DataIntegrityViolationException 識別子の生成に失敗した場合にスローされます。
     */
    Object generateValue();

    /**
     * 指定した個数分の識別子を新たに生成します。
     * @param num 生成する識別子の個数
     * @return DataIntegrityViolationException 識別子の生成に失敗した場合にスローされます。
     */
    default Object[] generateValues(int num) {
        Object[] values = new Object[num];
        for(int i=0; i < num; i++) {
            values[i] = generateValue();
        }
        return values;
    }

}
