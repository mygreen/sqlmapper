package com.github.mygreen.sqlmapper.metamodel;

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

    @Getter
    private final Class<? extends T> type;

    @Getter
    private final PathMeta pathMeta;

    public EntityPathBase(Class<? extends T> type, String name) {
        this.type = type;
        this.pathMeta = new PathMeta(name, PathType.ROOT);
    }

    protected StringPath createString(String property) {
        return new StringPath(this, property);
    }

    protected BooleanPath createBoolean(String property) {
        return new BooleanPath(this, property);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <A extends Number & Comparable<A>> NumberPath<A> createNumber(String property, Class<? super A> type) {
        return new NumberPath<A>((Class)type, this, property);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <A extends Enum<A>> EnumPath<A> createEnum(String property, Class<? super A> type) {
        return new EnumPath<A>((Class)type, this, property);
    }

    protected SqlDatePath createSqlDate(String property) {
        return new SqlDatePath(this, property);
    }

    protected SqlTimePath createSqlTime(String property) {
        return new SqlTimePath(this, property);
    }

    protected SqlTimestampPath createSqlTimestamp(String property) {
        return new SqlTimestampPath(this, property);
    }

    protected UtilDatePath createUtilDate(String property) {
        return new UtilDatePath(this, property);
    }

    protected LocalDatePath createLocalDate(String property) {
        return new LocalDatePath(this, property);
    }

    protected LocalTimePath createLocalTime(String property) {
        return new LocalTimePath(this, property);
    }

    protected LocalDateTimePath createLocalDateTime(String property) {
        return new LocalDateTimePath(this, property);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <A> GeneralPath<A> createGeneral(String property, Class<? super A> type) {
        return new GeneralPath<A>((Class)type, this, property);
    }

    @Override
    public <C> void accept(Visitor<C> visitor, C context) {
        visitor.visit(this, context);
    }

}
