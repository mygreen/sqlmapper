package com.github.mygreen.sqlmapper.metamodel;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * エンティティのメタモデルの抽象クラス。
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

    public EntityPathBase(Class<? extends T> type, String name) {
        this.type = type;
        this.pathMeta = new PathMeta(name, PathType.ROOT);
    }

    @Override
    public PropertyPath<?> getPropertyPath(String propertyName) {
        return propertyPathMap.get(propertyName);
    }

    protected StringPath createString(String property) {
        StringPath propertyPath = new StringPath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    protected BooleanPath createBoolean(String property) {
        BooleanPath propertyPath = new BooleanPath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <A extends Number & Comparable<A>> NumberPath<A> createNumber(String property, Class<? super A> type) {
        NumberPath<A> propertyPath = new NumberPath<>((Class)type, this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <A extends Enum<A>> EnumPath<A> createEnum(String property, Class<? super A> type) {
        EnumPath<A> propertyPath = new EnumPath<>((Class)type, this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    protected SqlDatePath createSqlDate(String property) {
        SqlDatePath propertyPath = new SqlDatePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    protected SqlTimePath createSqlTime(String property) {
        SqlTimePath propertyPath = new SqlTimePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    protected SqlTimestampPath createSqlTimestamp(String property) {
        SqlTimestampPath propertyPath = new SqlTimestampPath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    protected UtilDatePath createUtilDate(String property) {
        UtilDatePath propertyPath = new UtilDatePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    protected LocalDatePath createLocalDate(String property) {
        LocalDatePath propertyPath = new LocalDatePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    protected LocalTimePath createLocalTime(String property) {
        LocalTimePath propertyPath = new LocalTimePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

    protected LocalDateTimePath createLocalDateTime(String property) {
        LocalDateTimePath propertyPath = new LocalDateTimePath(this, property);
        propertyPathMap.put(property, propertyPath);
        return propertyPath;
    }

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
