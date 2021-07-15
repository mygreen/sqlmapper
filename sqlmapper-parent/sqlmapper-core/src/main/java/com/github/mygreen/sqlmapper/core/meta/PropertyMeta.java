package com.github.mygreen.sqlmapper.core.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.util.LinkedCaseInsensitiveMap;

import com.github.mygreen.sqlmapper.core.annotation.CreatedAt;
import com.github.mygreen.sqlmapper.core.annotation.CreatedBy;
import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Lob;
import com.github.mygreen.sqlmapper.core.annotation.UpdatedAt;
import com.github.mygreen.sqlmapper.core.annotation.UpdatedBy;
import com.github.mygreen.sqlmapper.core.annotation.Transient;
import com.github.mygreen.sqlmapper.core.annotation.Version;
import com.github.mygreen.sqlmapper.core.id.IdGenerator;
import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * プロパティのメタ情報です。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class PropertyMeta {

    /**
     * プロパティ名
     */
    @Getter
    private final String name;

    /**
     * プロパティタイプ
     */
    @Getter
    private final Class<?> propertyType;

    /**
     * フィールド情報
     */
    private Optional<Field> field = Optional.empty();

    /**
     * setterメソッド
     */
    private Optional<Method> writeMethod = Optional.empty();

    /**
     * getterメソッド
     */
    private Optional<Method> readMethod = Optional.empty();

    /**
     * アノテーションの情報
     */
    private Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();

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
     * 識別子の生成タイプ
     */
    @Getter
    private Optional<GeneratedValue.GenerationType> idGenerationType = Optional.empty();

    /**
     * 識別子の生成処理
     */
    @Getter
    private Optional<IdGenerator> idGenerator = Optional.empty();

    /**
     * プロパティが定義されているクラス情報を取得します。
     * @return プロパティが定義されているクラス情報
     * @throws IllegalStateException クラス情報を取得するための情報が不足している場合。
     */
    public Class<?> getDeclaringClass() {

        if(field.isPresent()) {
            return field.get().getDeclaringClass();
        }

        if(readMethod.isPresent()) {
            return readMethod.get().getDeclaringClass();
        }

        if(writeMethod.isPresent()) {
            return writeMethod.get().getDeclaringClass();
        }

        throw new IllegalStateException("not found availabeld info.");

    }

    /**
     * 読み込み可能なプロパティか判定する。
     * <p>getterメソッドまたはpublicなフィールドが存在する場合</p>
     * @return {@literal true}のとき読み込み可能。
     */
    public boolean isReadable() {

        if(readMethod.isPresent()) {
            return true;
        }

        if(field.isPresent()) {
            if(Modifier.isPublic(field.get().getModifiers())) {
                return true;
            }
        }

        return false;

    }

    /**
     * 書込み可能なプロパティか判定する。
     * <p>setterメソッドまたはpublicなフィールドが存在する場合</p>
     * @return {@literal true}のと書き込み可能。
     */
    public boolean isWritable() {

        if(writeMethod.isPresent()) {
            return true;
        }

        if(field.isPresent()) {
            if(Modifier.isPublic(field.get().getModifiers())) {
                return true;
            }
        }

        return false;

    }

    /**
     * プロパティに対するフィールドを設定します。
     * @param field フィールド（nullを許容します）
     */
    public void setField(Field field) {
        this.field = Optional.ofNullable(field);
    }

    /**
     * プロパティに対するフィールドを情報を取得します。
     * @return プロパティに対するフィールド情報
     */
    public Optional<Field> getField() {
        return field;
    }

    /**
     * プロパティに対するsetterメソッドを設定します。
     * @param method setterメソッド（nullを許容します）
     */
    public void setWriteMethod(Method method) {
        this.writeMethod = Optional.ofNullable(method);
    }

    /**
     * プロパティに対するsetterメソッドを取得します。
     * @return プロパティに対するsetterメソッド。
     */
    public Optional<Method> getWriteMethod() {
        return writeMethod;
    }

    /**
     * プロパティに対するgetterメソッドを設定します。
     * @param method getterメソッド（nullを許容します）
     */
    public void setReadMethod(Method method) {
        this.readMethod = Optional.ofNullable(method);
    }

    /**
     * プロパティに対するgetterメソッドを取得します。
     * @return プロパティに対するgetterメソッド
     */
    public Optional<Method> getReadMethod() {
        return readMethod;
    }

    /**
     * アノテーションを追加します。
     * @param annoClass アノテーションのタイプ
     * @param anno 追加するアノテーション
     */
    public void addAnnotation(@NonNull Class<? extends Annotation> annoClass, @NonNull Annotation anno) {
        this.annotationMap.put(annoClass, anno);
    }

    /**
     * 指定したアノテーションを持つか判定します。
     * @param <A> アノテーションのタイプ。
     * @param annoClass アノテーションのクラスタイプ。
     * @return trueの場合、アノテーションを持ちます。
     */
    public <A extends Annotation> boolean hasAnnotation(@NonNull Class<A> annoClass) {
        return annotationMap.containsKey(annoClass);
    }

    /**
     * タイプを指定して、アノテーションを取得する。
     * @param <A> アノテーションのタイプ。
     * @param annoClass アノテーションのクラスタイプ。
     * @return 存在しない場合、空を返します。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annoClass) {
        return Optional.ofNullable((A)annotationMap.get(annoClass));
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
     * 永続化対象外かどうか判定する。
     * @return 永続化対象外のとき {@literal true} を返す。
     */
    public boolean isTransient() {
        return hasAnnotation(Transient.class);
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
