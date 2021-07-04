package com.github.mygreen.sqlmapper.core.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.Path;
import com.github.mygreen.sqlmapper.metamodel.PathType;

/**
 * テーブル名と別名を管理します。
 *
 * @author T.TSUCHIE
 *
 */
public class TableNameResolver {

    /**
     * エイリアス名となるテーブルのインデックスです。
     */
    private int tableIndex = 0;

    /**
     * テーブル名と別名のマップです。
     * key=テーブル名やエンティティ名、value=別名
     */
    private Map<String, String> tableAliasMap = new HashMap<>();

    /**
     * キーとなる名前に紐づいている別名を取得します。
     * @param name キーとなる名前。
     * @return 別名。見つからない場合は、{@literal null} を返します。
     */
    public String getTableAlias(String name) {
        return tableAliasMap.get(name);
    }

    /**
     * テーブル名のエイリアスをエンティティ情報から取得します。
     * @param entityPath エンティティパス
     * @return 別名。見つからない場合は、{@literal null} を返します。
     */
    public String getTableAlias(EntityPath<?> entityPath) {
        return tableAliasMap.get(getEntityPathName(entityPath));
    }

    /**
     * テーブル名のエイリアスをエンティティ情報から取得します。
     * @param entityPath エンティティパス
     * @return 別名。見つからない場合は、{@literal null} を返します。
     * @throws IllegalArgumentException 引数が{@link EntityPath} のインスタンスでない場合にスローされます。
     */
    public String getTableAlias(Path<?> entityPath) {
        Assert.isInstanceOf(EntityPath.class, entityPath);
        return getTableAlias((EntityPath<?>) entityPath);
    }

    /**
     * テーブルの別名を新たに準備します。
     * 既に登録済みのキーとなる名前の場合、登録されている別名を返します。
     *
     * @param name キーとなる名前。
     * @return 別名。
     */
    public String prepareTableAlias(String name) {

        return tableAliasMap.computeIfAbsent(name, key -> createTableAlias());

    }

    /**
     * テーブルの別名を新たに準備します。
     * 既に登録済みのキーとなる名前の場合、登録されている別名を返します。
     *
     * @param entityPath エンティティのパス情報。
     * @return テーブル名の別名。
     */
    public String prepareTableAlias(Path<?> entityPath) {
        return prepareTableAlias(getEntityPathName(entityPath));
    }

    /**
     * エンティティのパス名を取得します。
     * @param entityPath エンティティのパス情報。
     * @return テーブル名の別名。
     */
    private String getEntityPathName(Path<?> entityPath) {

        StringBuilder buff = new StringBuilder();

        buff.append(entityPath.getPathMeta().getElement());

        // ネストしている場合は親をたどる。
        Path<?> parent = entityPath.getPathMeta().getParent();
        while(parent != null && parent.getPathMeta().getType() != PathType.ROOT) {
            buff.insert(0, parent.getPathMeta().getElement() + ".");
            parent = entityPath.getPathMeta().getParent();
        }

        return buff.toString();
    }

    /**
     * テーブル名の別名を作成します。
     * @return テーブルの別名
     */
    private String createTableAlias() {
        return String.format("T%d_", (++tableIndex));
    }

}
