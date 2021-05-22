package com.github.mygreen.sqlmapper.metamodel;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import lombok.Data;

/**
 * 埋め込みプロパティを持つエンティティのテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
class EmbeddedPathTest {

    @Test
    void testPathMeta() {

        MSampleEntity entity = MSampleEntity.sampleEntity;

        // エンティティのチェック
        assertThat(entity.getType()).isEqualTo(SampleEntity.class);
        assertThat(entity.getPathMeta().getParent()).isNull();
        assertThat(entity.getPathMeta().getElement()).isEqualTo("sampleEntity");
        assertThat(entity.getPathMeta().getType()).isEqualTo(PathType.ROOT);

        // プロパティのチェック - 埋め込みプロパティ
        {
            EntityPath<PK> prop = entity.id;
            assertThat(prop.getType()).isEqualTo(PK.class);
            assertThat(prop.getPathMeta().getParent()).isEqualTo(entity);
            assertThat(prop.getPathMeta().getElement()).isEqualTo("id");
            assertThat(prop.getPathMeta().getType()).isEqualTo(PathType.EMBEDDED);

        }

        // プロパティのチェック - 埋め込みエンティティ内のプロパティ
        {
            PropertyPath<String> prop = entity.id.key1;
            assertThat(prop.getType()).isEqualTo(String.class);
            assertThat(prop.getPathMeta().getParent()).isEqualTo(entity.id);
            assertThat(prop.getPathMeta().getElement()).isEqualTo("key1");
            assertThat(prop.getPathMeta().getType()).isEqualTo(PathType.PROPERTY);
        }

    }

    @Test
    void testBuildCondition() {

        MSampleEntity entity = MSampleEntity.sampleEntity;

        Predicate exp = entity.id.key1.eq("100")
                .and(entity.name.contains("Taro"));
        String resultString = exp.toString();

        assertThat(resultString).isEqualTo("sampleEntity.id.key1 = 100 and contains(sampleEntity.name, Taro)");

    }

    @Data
    static class PK {

        private String key1;

        private long key2;

    }

    @Data
    static class SampleEntity {

        private PK id;

        private String name;

        private boolean deleted;

    }

    static class MPK extends EntityPathBase<PK> {

        public final StringPath key1 = createString("key1");

        public final NumberPath<Long> key2 = createNumber("key2", Long.class);

        public MPK(Class<? extends PK> type, EntityPathBase<?> parent, String name) {
            super(type, parent, name);
        }

        public MPK(EntityPathBase<?> parent, String name) {
            super(PK.class, parent, name);
        }

    }

    static class MSampleEntity extends EntityPathBase<SampleEntity> {

        public static final MSampleEntity sampleEntity = new MSampleEntity("sampleEntity");

        public final MPK id = new MPK(this, "id");

        public final StringPath name = createString("name");

        public final BooleanPath deleted = createBoolean("deleted");

        public MSampleEntity(Class<? extends SampleEntity> type, String name) {
            super(type, name);
        }

        public MSampleEntity(String name) {
            super(SampleEntity.class, name);
        }

    }

}
