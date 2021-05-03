package com.github.mygreen.sqlmapper.metamodel;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * エンティティのメタモデルのベースクラス。
 * <p>エンティティのメタモデルは基本的にこのクラスを実装して作成します。
 *
 *
 * @author T.TSUCHIE
 *
 * @param <T> エンティティタイプ
 */
public abstract class EntityPathBase<T> implements EntityPath<T> {

    /**
     * エンティティタイプ
     */
    @Getter
    private final Class<? extends T> type;

    /**
     * エンティティのメタ情報
     */
    @Getter
    private final PathMeta pathMeta;

    /**
     * プロパティのインスタンスのマップ
     */
    private final Map<String, PropertyPath<?>> propertyPathMap = new HashMap<>();

    /**
     * エンティティのメタモデルのインスタンスを作成します。
     * @param type エンティティのタイプ。
     * @param name エンティティ名。
     */
    public EntityPathBase(final Class<? extends T> type, final String name) {
        this.type = type;
        this.pathMeta = new PathMeta(name, PathType.ROOT);
    }

    @Override
    public PropertyPath<?> getPropertyPath(final String propertyName) {
        return propertyPathMap.get(propertyName);
    }

    /**
     * 文字列型のプロパティを作成します。
     * @param property プロパティ名
     * @return 文字列型のプロパティ。
     */
    protected StringPath createString(final String property) {
        StringPath propertyPath = new StringPath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * boolean型またはそのラッパークラス型のプロパティを作成します。
     * @param property プロパティ名
     * @return boolean型またはそのラッパークラス型のプロパティ
     */
    protected BooleanPath createBoolean(final String property) {
        BooleanPath propertyPath = new BooleanPath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * 数値型のプロパティを作成します。
     * @param property プロパティ名
     * @param type 数値型のクラス
     * @return 数値型のプロパティ
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <A extends Number & Comparable<A>> NumberPath<A> createNumber(final String property, final Class<? super A> type) {
        NumberPath<A> propertyPath = new NumberPath<>((Class)type, this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * 列挙型のプロパティを作成します。
     * @param <A> 列挙型のタイプ
     * @param property プロパティ名
     * @param type 列挙型のクラス
     * @return 列挙型のプロパティ
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <A extends Enum<A>> EnumPath<A> createEnum(final String property, final Class<? super A> type) {
        EnumPath<A> propertyPath = new EnumPath<>((Class)type, this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * {@link java.sql.Date} 型のプロパティを作成します。
     * @param property プロパティ名
     * @return {@link java.sql.Date} 型のプロパティ
     */
    protected SqlDatePath createSqlDate(final String property) {
        SqlDatePath propertyPath = new SqlDatePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * {@link java.sql.Time} 型のプロパティを作成します。
     * @param property プロパティ名
     * @return {@link java.sql.Time} 型のプロパティ
     */
    protected SqlTimePath createSqlTime(final String property) {
        SqlTimePath propertyPath = new SqlTimePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * {@link java.sql.Timestamp} 型のプロパティを作成します。
     * @param property プロパティ名
     * @return {@link java.sql.Timestamp} 型のプロパティ
     */
    protected SqlTimestampPath createSqlTimestamp(final String property) {
        SqlTimestampPath propertyPath = new SqlTimestampPath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * {@link java.util.Date} 型のプロパティを作成します。
     * @param property プロパティ名
     * @return {@link java.util.Date} 型のプロパティ
     */
    protected UtilDatePath createUtilDate(final String property) {
        UtilDatePath propertyPath = new UtilDatePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * {@link java.time.LocalDate} 型のプロパティを作成します。
     * @param property プロパティ名
     * @return {@link java.time.LocalDate} 型のプロパティ
     */
    protected LocalDatePath createLocalDate(String property) {
        LocalDatePath propertyPath = new LocalDatePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * {@link java.time.LocalTime} 型のプロパティを作成します。
     * @param property プロパティ名
     * @return {@link java.time.LocalTime} 型のプロパティ
     */
    protected LocalTimePath createLocalTime(final String property) {
        LocalTimePath propertyPath = new LocalTimePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * {@link java.time.LocalDateTime} 型のプロパティを作成します。
     * @param property プロパティ名
     * @return {@link java.time.LocalDateTime} 型のプロパティ
     */
    protected LocalDateTimePath createLocalDateTime(final String property) {
        LocalDateTimePath propertyPath = new LocalDateTimePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    /**
     * 汎用的な型のプロパティを作成します。
     * <p>{@literal byte[]} 型など専用の式がないプロパティ型のときに用います。
     *
     * @param <A> プロパティの型
     * @param property プロパティ名
     * @param type プロパティのクラスタイプ
     * @return 汎用的な型のプロパティ
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <A> GeneralPath<A> createGeneral(String property, Class<? super A> type) {
        GeneralPath propertyPath = new GeneralPath<A>((Class)type, this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(this, context);
    }

}
