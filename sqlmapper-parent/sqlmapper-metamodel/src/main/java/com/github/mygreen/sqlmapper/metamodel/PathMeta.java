package com.github.mygreen.sqlmapper.metamodel;

import java.util.Optional;

import lombok.Getter;
import lombok.NonNull;

/**
 * パス情報を表現するクラス。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class PathMeta {

    /**
     * 親情報
     */
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

    /**
     * 親情報を取得します。
     * @return 親を持たない場合は{@literal null} を返します。
     */
    public Path<?> getParent() {
        return parent.orElse(null);
    }

    /**
     * 親情報を取得します。
     * @return 親情報。
     */
    public Optional<Path<?>> getParentAsOptional() {
        return parent;
    }

    /**
     * ルートのパスを親をだ取り検索します。
     * @return 自身がルート({@link PathType#ROOT})の場合は{@literal null}を返します。
     */
    public Path<?> findRootPath() {
        if(type == PathType.ROOT) {
            return null;
        }
        PathMeta pathMeta = this;
        Path<?> parent = null;
        do{
            parent = pathMeta.getParent();
            if(parent == null) {
                // ありえないが、nullチェックをする。
                break;
            }
            pathMeta = parent.getPathMeta();
        } while(parent.getPathMeta().getType() != PathType.ROOT);

        return parent;
    }
}
