package com.github.mygreen.sqlmapper.core.meta;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.util.LinkedCaseInsensitiveMap;

import com.github.mygreen.sqlmapper.core.annotation.CreatedAt;
import com.github.mygreen.sqlmapper.core.annotation.CreatedBy;
import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Lob;
import com.github.mygreen.sqlmapper.core.annotation.Transient;
import com.github.mygreen.sqlmapper.core.annotation.UpdatedAt;
import com.github.mygreen.sqlmapper.core.annotation.UpdatedBy;
import com.github.mygreen.sqlmapper.core.annotation.Version;
import com.github.mygreen.sqlmapper.core.id.IdGenerationContext;
import com.github.mygreen.sqlmapper.core.id.IdGenerator;
import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * プロパティのメタ情報です。
 *
 * @author T.TSUCHIE
 * @version 0.3
 *
 */
public class PropertyMeta extends PropertyBase {

    /**
     * 埋め込み型の主キーの子プロパティかどうか。
     */
    private boolean embeddedableId = false;

    /**
     * 埋め込みプロパティの情報
     * <p>key=プロパティ名</p>
     */
    private LinkedCaseInsensitiveMap<PropertyMeta> embeddedablePropertyMetaMap = new LinkedCaseInsensitiveMap<>();

    /**
     * 埋め込みのときの親のプロパティ情報
     */
    private Optional<PropertyMeta> parent = Optional.empty();

    /**
     * カラムのメタ情報
     */
    @Getter
    @Setter
    private ColumnMeta columnMeta;

    /**
     * 値の変換処理
     */
    @Getter
    @Setter
    private ValueType<?> valueType;

    /**
     * IDの生成タイプ
     */
    @Getter
    private Optional<GeneratedValue.GenerationType> idGenerationType = Optional.empty();

    /**
     * IDの生成処理
     */
    @Getter
    private Optional<IdGenerator> idGenerator = Optional.empty();

    /**
     * 生成対象の識別子の情報。
     * <p>ID生成時に渡す際の情報として使用するので、効率化のためにここで事前に作成して保持しておく。
     */
    @Getter
    private Optional<IdGenerationContext> idGenerationContext = Optional.empty();

    /**
     * プロパティのインスタンス情報を作成します。
     * @param name プロパティ名
     * @param propertyType プロパティのクラスタイプ
     */
    public PropertyMeta(String name, Class<?> propertyType) {
        super(name, propertyType);
    }

    /**
     * カラム用のプロパティかどうか判定する。
     * @return カラム情報を持つときtrueを返す。
     */
    public boolean isColumn() {
        return columnMeta != null;
    }

    /**
     * 埋め込みプロパティ情報を追加する
     * @param embeddedablePropertyMeta 埋め込みプロパティ
     */
    public void addEmbeddedablePropertyMeta(@NonNull PropertyMeta embeddedablePropertyMeta) {

        embeddedablePropertyMeta.parent = Optional.of(this);

        // 親が埋め込み主キーの時
        if(hasAnnotation(EmbeddedId.class) && !isTransient()) {
            embeddedablePropertyMeta.embeddedableId = true;
        }

        this.embeddedablePropertyMetaMap.put(embeddedablePropertyMeta.getName(), embeddedablePropertyMeta);

    }

    /**
     * 埋め込み用のクラスのプロパティかどか判定する。
     * @return 埋め込み用のクラスのプロパティの場合trueを変す。
     */
    public boolean hasParent() {
        return parent.isPresent();
    }

    /**
     * 埋め込み用のクラスのプロパティの親情報を取得する。
     * @return 親情のプロパティ情報
     * @throws NoSuchElementException 親が存在しないときにスローされます。
     */
    public PropertyMeta getParent() {
        return parent.get();
    }

    /**
     * 埋め込みプロパティの一覧を取得する。
     * @return
     */
    public Collection<PropertyMeta> getEmbeddedablePopertyMetaList() {
        return embeddedablePropertyMetaMap.values();
    }

    /**
     * 主キーかどうか判定する。
     * <p>アノテーション {@link Id}を付与されているかどうかで判定する。</p>
     * <p>また、親が{@link EmbeddedId} を付与された埋め込みIDの場合は、子も主キーとなるためtrueを返す。</p>
     *
     * @return 主キーの場合は {@literal true}  を返す。
     */
    public boolean isId() {

        return hasAnnotation(Id.class) || embeddedableId;
    }

    /**
     * 埋め込み用のプロパティかどうか判定する。
     * <p>埋め込みプロパティの子の場合は、falseを返す。
     * @return 埋め込みの場合trueを返す。
     */
    public boolean isEmbedded() {
        return hasAnnotation(EmbeddedId.class);
    }

    /**
     * 識別子の生成タイプを設定する。
     * @param generationType IDの生成タイプ
     */
    public void setIdGeneratonType(GeneratedValue.GenerationType generationType) {
        this.idGenerationType = Optional.ofNullable(generationType);
    }

    /**
     * 識別子の生成処理を設定する。
     * @param idGenerator 識別子の生成処理。
     */
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = Optional.ofNullable(idGenerator);
    }

    /**
     * 生成対象の識別子の情報を設定する。
     * @param idGenerationContext 生成対象の識別子の情報
     */
    public void setIdGenerationContext(IdGenerationContext idGenerationContext) {
        this.idGenerationContext = Optional.ofNullable(idGenerationContext);
    }

    /**
     * 永続化対象外かどうか判定する。
     * <p>永続化対象外とは、アノテーション {@link Transient}が付与されているか、
     * または、フィールドに修飾子 {@literal transient} が付与されているかどうかで判定します。
     * @return 永続化対象外のとき {@literal true} を返す。
     */
    public boolean isTransient() {
        return hasAnnotation(Transient.class)
                || field.map(f -> Modifier.isTransient(f.getModifiers())).orElse(false);
    }

    /**
     * バージョンキーかどうか判定する。
     * @return バージョンキーのとき {@literal true} を返す。
     */
    public boolean isVersion() {
        return hasAnnotation(Version.class);
    }

    /**
     * SQLのカラムがLOB(CLOB/BLOC)かどうか判定する。
     * <p>アノテーション {@link Lob} が付与されているかで判定する。</p>
     * @return LOBの場合はtrueを返す。
     */
    public boolean isLob() {
        return hasAnnotation(Lob.class);
    }

    /**
     * 作成日時用のプロパティがかどうか判定する。
     * @return 作成日時用のプロパティのとき {@literal true} を返す。
     */
    public boolean isCreatedAt() {
        return hasAnnotation(CreatedAt.class);
    }

    /**
     * 作成者用のプロパティがかどうか判定する。
     * @return 作成者用のプロパティのとき {@literal true} を返す。
     */
    public boolean isCreatedBy() {
        return hasAnnotation(CreatedBy.class);
    }

    /**
     * 修正日時用のプロパティがかどうか判定する。
     * @return 修正日時用のプロパティのとき {@literal true} を返す。
     */
    public boolean isUpdatedAt() {
        return hasAnnotation(UpdatedAt.class);
    }

    /**
     * 修正者用のプロパティがかどうか判定する。
     * @return 修正者用のプロパティのとき {@literal true} を返す。
     */
    public boolean isUpdatedBy() {
        return hasAnnotation(UpdatedBy.class);
    }


}
