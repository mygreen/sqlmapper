    package com.github.mygreen.sqlmapper.core.id;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * IDを自動生成する処理のインタフェースです。
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
     * IDを新たに生成します。
     * @return 生成したID
     * @throws DataIntegrityViolationException IDの生成に失敗した場合にスローされます。
     */
    Object generateValue();

    /**
     * 指定した個数分のIDを新たに生成します。
     * @param num 生成するIDの個数
     * @return DataIntegrityViolationException IDの生成に失敗した場合にスローされます。
     */
    default Object[] generateValues(int num) {
        Object[] values = new Object[num];
        for(int i=0; i < num; i++) {
            values[i] = generateValue();
        }
        return values;
    }

}
