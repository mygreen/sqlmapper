package com.github.mygreen.sqlmapper.core.testdata;

import com.github.mygreen.sqlmapper.core.annotation.Embeddable;
import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.Entity;

import lombok.Data;

/**
 * 埋め込み主キーを持つエンティティ
 *
 * @author T.TSUCHIE
 *
 */
@Data
@Entity
public class EmbeddedEntity {

    @EmbeddedId
    private PK id;

    private String name;

    private boolean deleted;

    @Data
    @Embeddable
    public static class PK {

        private String key1;

        private long key2;

    }
}
