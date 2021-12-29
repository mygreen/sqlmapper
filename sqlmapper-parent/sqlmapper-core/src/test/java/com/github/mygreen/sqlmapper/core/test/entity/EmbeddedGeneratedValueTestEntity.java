package com.github.mygreen.sqlmapper.core.test.entity;

import com.github.mygreen.sqlmapper.core.annotation.Embeddable;
import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.SequenceGenerator;
import com.github.mygreen.sqlmapper.core.annotation.Table;
import com.github.mygreen.sqlmapper.core.annotation.TableGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 埋め込み型の主キーが自動生成の場合
 *
 * @author T.TSUCHIE
 *
 */
@Data
@Entity
@Table(name="TEST_EMBEDDED_GENERATED_VALUE")
public class EmbeddedGeneratedValueTestEntity {

    @EmbeddedId
    private PK id;

    private String name;

    private boolean deleted;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class PK {

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long key1;

        @SequenceGenerator(format = "000'@key2'")
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        private String key2;

        @TableGenerator(format = "000'@key3'")
        @GeneratedValue(strategy = GenerationType.TABLE)
        private String key3;

    }
}
