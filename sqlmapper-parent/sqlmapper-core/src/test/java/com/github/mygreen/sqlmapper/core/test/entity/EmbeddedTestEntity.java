package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Embeddable;
import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 埋め込み主キーを持つエンティティ
 *
 * @author T.TSUCHIE
 *
 */
@Data
@Entity
@Table(name="TEST_EMBEDDED")
public class EmbeddedTestEntity {

    @EmbeddedId
    private PK id;

    private String name;

    private boolean deleted;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class PK {

        private String key1;

        private long key2;

    }
}
