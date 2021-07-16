package com.github.mygreen.sqlmapper.core.id;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * IDを自動生成する処理のインタフェースです。
 *
 * @version 0.3
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
     * @param context 生成対象のIDの情報
     * @return 生成したID
     * @throws DataIntegrityViolationException IDの生成に失敗した場合にスローされます。
     */
    Object generateValue(IdGenerationContext context);

    /**
     * 指定した個数分のIDを新たに生成します。
     * @param context 生成対象のIDの情報
     * @param num 生成するIDの個数
     * @return DataIntegrityViolationException IDの生成に失敗した場合にスローされます。
     */
    default Object[] generateValues(final IdGenerationContext context, final int num) {
        Object[] values = new Object[num];
        for(int i=0; i < num; i++) {
            values[i] = generateValue(context);
        }
        return values;
    }

}
