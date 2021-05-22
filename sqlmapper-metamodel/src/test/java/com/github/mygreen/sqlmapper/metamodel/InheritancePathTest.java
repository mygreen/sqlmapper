package com.github.mygreen.sqlmapper.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.Test;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 継承されたエンティティのテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
class InheritancePathTest {

    @Test
    void testPathMeta() {

        MInheritanceEntity entity = MInheritanceEntity.inheritanceEnity;

        // エンティティのチェック
        assertThat(entity.getType()).isEqualTo(InheritanceEntity.class);
        assertThat(entity.getPathMeta().getParent()).isNull();
        assertThat(entity.getPathMeta().getElement()).isEqualTo("inheritanceEnity");
        assertThat(entity.getPathMeta().getType()).isEqualTo(PathType.ROOT);

        // プロパティのチェック - 継承先
        {
            PropertyPath<String> prop = entity.id;
            assertThat(prop.getType()).isEqualTo(String.class);
            assertThat(prop.getPathMeta().getParent()).isEqualTo(entity);
            assertThat(prop.getPathMeta().getElement()).isEqualTo("id");
            assertThat(prop.getPathMeta().getType()).isEqualTo(PathType.PROPERTY);

        }

        // プロパティのチェック - 継承元
        {
            PropertyPath<String> prop = entity.updateBy;
            assertThat(prop.getType()).isEqualTo(String.class);
            assertThat(prop.getPathMeta().getParent()).isEqualTo(entity);
            assertThat(prop.getPathMeta().getElement()).isEqualTo("updateBy");
            assertThat(prop.getPathMeta().getType()).isEqualTo(PathType.PROPERTY);
        }

    }

    @Test
    void testBuildCondition() {

        MInheritanceEntity entity = MInheritanceEntity.inheritanceEnity;

        Predicate exp = entity.birthday.after(LocalDate.of(2000, 1, 1))
                .and(entity.version.goe(1L));
        String resultString = exp.toString();

        assertThat(resultString).isEqualTo("inheritanceEnity.birthday > 2000-01-01 and inheritanceEnity.version >= 1");

    }

    @Data
    static abstract class BaseEntity {

        private String updateBy;

        private Date updateAt;

        private long version;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    static class InheritanceEntity extends BaseEntity {

        private String id;

        private LocalDate birthday;

    }

    static abstract class MBaseEntity<E extends BaseEntity> extends EntityPathBase<E> {

        public final StringPath updateBy = createString("updateBy");

        public final UtilDatePath updateAt = createUtilDate("updateAt");

        public final NumberPath<Long> version = createNumber("version", Long.class);

        MBaseEntity(Class<? extends E> type, String name) {
            super(type, name);
        }
    }

    static class MInheritanceEntity extends MBaseEntity<InheritanceEntity> {

        public static final MInheritanceEntity inheritanceEnity = new MInheritanceEntity("inheritanceEnity");

        public final StringPath id = createString("id");

        public final LocalDatePath birthday = createLocalDate("birthday");

        public MInheritanceEntity(Class<InheritanceEntity> type, String name) {
            super(type, name);
        }

        public MInheritanceEntity(String name) {
            super(InheritanceEntity.class, name);
        }

    }

}
