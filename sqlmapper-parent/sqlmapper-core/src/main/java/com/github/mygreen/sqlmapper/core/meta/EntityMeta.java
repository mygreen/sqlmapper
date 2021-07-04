package com.github.mygreen.sqlmapper.core.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.util.LinkedCaseInsensitiveMap;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * エンティティのメタ情報です。
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class EntityMeta {

    /**
     * エンティティのクラスタイプです。
     */
    @Getter
    private final Class<?> entityType;

    /**
     * エンティティの名称です。SQL自動作成時のテーブルのエイリアス名などに使用されます。
     */
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private TableMeta tableMeta;

    /**
     * プロパティ情報
     * <p>key=プロパティ名</p>
     */
    private LinkedCaseInsensitiveMap<PropertyMeta> propertyMetaMap = new LinkedCaseInsensitiveMap<>();

    /**
     * 主キーのプロパティ情報
     */
    @Getter
    private List<PropertyMeta> idPropertyMetaList = new ArrayList<>();

    /**
     * バージョンキー用のカラムプロパティ
     */
    @Getter
    private Optional<PropertyMeta> versionPropertyMeta = Optional.empty();

    /**
     * 作成日時用のカラムプロパティ
     */
    @Getter
    private Optional<PropertyMeta> createdAtPropertyMeta = Optional.empty();

    /**
     * 作成者用のカラムプロパティ
     */
    @Getter
    private Optional<PropertyMeta> createdByPropertyMeta = Optional.empty();

    /**
     * 修正日時用のカラムプロパティ
     */
    @Getter
    private Optional<PropertyMeta> modifiedAtPropertyMeta = Optional.empty();

    /**
     * 修正者用のカラムプロパティ
     */
    @Getter
    private Optional<PropertyMeta> modifiedByPropertyMeta = Optional.empty();

    /**
     * カラム用のプロパティ情報
     * <p>key=カラム名</p>
     */
    private LinkedCaseInsensitiveMap<PropertyMeta> columnPropertyMetaMap = new LinkedCaseInsensitiveMap<>();

    /**
     * プロパティのメタ情報を追加します。
     * @param propertyMeta メタ情報
     */
    public void addPropertyMeta(@NonNull PropertyMeta propertyMeta) {

        this.propertyMetaMap.put(propertyMeta.getName(), propertyMeta);

        setupRoleProperty(propertyMeta);

        // 埋め込みプロパティの場合、子プロパティを追加する。
        if(propertyMeta.isEmbedded()) {
            propertyMeta.getEmbeddedablePopertyMetaList().forEach(p -> setupRoleProperty(p));
        }

    }

    /**
     * プロパティメタ情報を取得します。
     * @param propertyName プロパティ名
     * @return プロパティメタ情報
     */
    public Optional<PropertyMeta> getPropertyMeta(@NonNull String propertyName) {

        return Optional.ofNullable(propertyMetaMap.get(propertyName));

    }

    /**
     * プロパティ名を指定して、プロパティメタ情報を取得します。
     * 埋め込みID内のプロパティも抽出対象とします。
     *
     * @param propertyName プロパティ名
     * @return プロパティメタ情報
     */
    public Optional<PropertyMeta> findPropertyMeta(@NonNull String propertyName) {

        PropertyMeta foundPropertyMeta = propertyMetaMap.get(propertyName);
        if(foundPropertyMeta != null) {
            return Optional.of(foundPropertyMeta);
        }

        for(PropertyMeta propertyMeta : getAllPropertyMeta()) {
            if(propertyMeta.isEmbedded()) {
                for(PropertyMeta embeddedPropertyMeta : propertyMeta.getEmbeddedablePopertyMetaList()) {
                    if(embeddedPropertyMeta.getName().equals(propertyName)) {
                        return Optional.of(embeddedPropertyMeta);
                    }
                }
            }
        }

        return Optional.empty();


    }

    /**
     * 全てのプロパティメタ情報の一覧を返します。
     * @return プロパティメタ情報の一覧
     */
    public Collection<PropertyMeta> getAllPropertyMeta() {
        return propertyMetaMap.values();
    }

    /**
     * プロパティメタ情報のサイズを返します。
     * @return プロパティメタ情報のサイズ
     */
    public int getPropertyMetaSize() {
        return propertyMetaMap.size();
    }

    /**
     * プロパティメタデータがあるかどうかを返します。
     * @param propertyName プロパティ名
     * @return {@literal true}のときプロパティメタデータがあります。
     */
    public boolean hasPropertyMeta(String propertyName) {
        return propertyMetaMap.containsKey(propertyName);
    }

    /**
     * カラムに紐づくプロパティメタ情報を取得します。
     * @param columnName カラム名。大文字・小文字の区別はしない。
     * @return カラムに紐づくプロパティメタ情報
     */
    public Optional<PropertyMeta> getColumnPropertyMeta(@NonNull String columnName) {

        return Optional.ofNullable(columnPropertyMetaMap.get(columnName));
    }

    /**
     * カラムに紐づく全てのプロパティメタ情報を取得します。
     * @return カラムに紐づく全てのプロパティメタ情報
     */
    public Collection<PropertyMeta> getAllColumnPropertyMeta() {
        return columnPropertyMetaMap.values();
    }

    /**
     * カラムに結びつくプロパティメタデータがあるかどうかを返します。
     *
     * @param columnName カラム名
     * @return プロパティメタデータがあるかどうか
     */
    public boolean hasColumnPropertyMeta(String columnName) {
        return columnPropertyMetaMap.containsKey(columnName);
    }

    /**
     * 埋め込み用IDのプロパティメタ情報を取得します。
     * @return 埋め込み用IDのプロパティメタ情報
     */
    public Optional<PropertyMeta> getEmbeddedIdPropertyMeta() {
        return propertyMetaMap.values().stream()
                .filter(p -> p.isEmbedded())
                .findFirst();
    }

    /**
     * バージョンを表すプロパティメタデータを持つかどうか。
     * @return バージョンを表すプロパティメタデータがあれば {@literal true} を返します。
     */
    public boolean hasVersionPropertyMeta() {
        return versionPropertyMeta.isPresent();
    }

    /**
     * 作成日時を表すプロパティのメタデータを持つかどうか。
     * @return 作成日時を表すプロパティのメタデータがあれば {@literal true} を返す。
     */
    public boolean hasCreatedAtPropertyMeta() {
        return createdAtPropertyMeta.isPresent();
    }

    /**
     * 作成者を表すプロパティのメタデータを持つかどうか。
     * @return 作成者を表すプロパティのメタデータがあれば {@literal true} を返す。
     */
    public boolean hasCreatedByPropertyMeta() {
        return createdByPropertyMeta.isPresent();
    }

    /**
     * 修正日時を表すプロパティのメタデータを持つかどうか。
     * @return 修正日時を表すプロパティのメタデータがあれば {@literal true} を返す。
     */
    public boolean hasModifiedAtPropertyMeta() {
        return modifiedAtPropertyMeta.isPresent();
    }

    /**
     * 修正者を表すプロパティのメタデータを持つかどうか。
     * @return 修正者を表すプロパティのメタデータがあれば {@literal true} を返す。
     */
    public boolean hasModifiedByPropertyMeta() {
        return modifiedByPropertyMeta.isPresent();
    }

    /**
     * 役割を持つプロパティを設定する
     * @param propertyMeta 対象のプロパティメタ情報
     */
    private void setupRoleProperty(PropertyMeta propertyMeta) {

        if(propertyMeta.isTransient()) {
            return;
        }

        if(propertyMeta.isId()) {
            this.idPropertyMetaList.add(propertyMeta);
        }

        if(propertyMeta.isVersion()) {
            this.versionPropertyMeta = Optional.of(propertyMeta);
        }

        if(propertyMeta.isCreatedAt()) {
            this.createdAtPropertyMeta = Optional.of(propertyMeta);
        }

        if(propertyMeta.isCreatedBy()) {
            this.createdByPropertyMeta = Optional.of(propertyMeta);
        }

        if(propertyMeta.isModifiedAt()) {
            this.modifiedAtPropertyMeta = Optional.of(propertyMeta);
        }

        if(propertyMeta.isModifiedBy()) {
            this.modifiedByPropertyMeta = Optional.of(propertyMeta);
        }

        if(propertyMeta.getColumnMeta() != null) {
            this.columnPropertyMetaMap.put(propertyMeta.getColumnMeta().getName(), propertyMeta);
        }

    }
}
