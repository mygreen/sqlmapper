package com.github.mygreen.sqlmapper.apt.model;

import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.sqlmapper.core.annotation.Entity;

import lombok.Data;

/**
 * APTによる処理対象のプロパティ情報。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
public class EntityModel {

    /**
     * パッケージ名
     */
    private String packageName;

    /**
     * クラス名（パッケージ名除く）
     */
    private String className;

    /**
     * 親クラス
     */
    private Class<?> superClass;

    /**
     * アノテーション{@link Entity}の情報
     */
    private Entity entityAnno;

    /**
     * プロパティ情報
     */
    private List<PropertyModel> properties = new ArrayList<>();

    /**
     * エンティティのFQNを取得する。
     * @return エンティティのFQN
     */
    public String getFullName() {
        StringBuilder buff = new StringBuilder();
        if(packageName != null) {
            buff.append(packageName).append(".");
        }

        buff.append(className);

        return buff.toString();
    }

    /**
     * プロパティ情報を追加する。
     * @param property プロパティ情報
     */
    public void add(PropertyModel property) {
        this.properties.add(property);
    }

}
