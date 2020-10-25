package com.github.mygreen.sqlmapper.metamodel;

import java.util.Optional;

import lombok.Getter;
import lombok.NonNull;

/**
 * エンティティのパス情報を表現するクラス。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class PathMeta {

    private Optional<Path<?>> parent = Optional.empty();

    /**
     * 要素の値
     */
    @Getter
    private String element;

    /**
     * パスのタイプ
     */
    @Getter
    private PathType type;

    public PathMeta(@NonNull Path<?> parent, @NonNull String element, @NonNull PathType type) {
        this.parent = Optional.of(parent);
        this.element = element;
        this.type = type;
    }

    public PathMeta(@NonNull String element, @NonNull PathType type) {
        this.element = element;
        this.type = type;
    }

    public Path<?> getParent() {
        return parent.orElseGet(null);
    }

    public Optional<Path<?>> getParentAsOptional() {
        return parent;
    }
}
