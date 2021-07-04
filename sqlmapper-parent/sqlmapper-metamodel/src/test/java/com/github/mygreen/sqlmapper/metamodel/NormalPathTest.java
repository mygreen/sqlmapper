package com.github.mygreen.sqlmapper.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mygreen.sqlmapper.metamodel.expression.SqlTimestampExpression;

import lombok.Data;

/**
 * シンプルなクラス構成のテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
public class NormalPathTest {

    @DisplayName("chek meta info.")
    @Test
    void testPathMeta() {

        // エンティティのチェック
        MSampleEntity entity = MSampleEntity.sampleEntity;

        assertThat(entity.getType()).isEqualTo(SampleEntity.class);
        assertThat(entity.getPathMeta().getParent()).isNull();
        assertThat(entity.getPathMeta().getElement()).isEqualTo("sampleEntity");
        assertThat(entity.getPathMeta().getType()).isEqualTo(PathType.ROOT);

        // プロパティのチェック
        {
            PropertyPath<String> prop = entity.name;
            assertThat(prop.getType()).isEqualTo(String.class);
            assertThat(prop.getPathMeta().getParent()).isEqualTo(entity);
            assertThat(prop.getPathMeta().getElement()).isEqualTo("name");
            assertThat(prop.getPathMeta().getType()).isEqualTo(PathType.PROPERTY);

        }

    }

    @Test
    void testBuildCondition() {

        MSampleEntity entity = MSampleEntity.sampleEntity;
        Predicate exp = entity.name.lower().contains("yamada")
                .and(entity.age.add(10).gt(20))
                .and(entity.role.in(Role.Admin, Role.Normal))
                .and(entity.updateAt.after(SqlTimestampExpression.currentTimestamp()))
                .and(entity.deleted.isFalse())
                .and(entity.salary.goe(new BigDecimal("1000000")))
                ;

        String resultString = exp.toString();
        assertThat(resultString).isEqualTo("contains(lower(sampleEntity.name), yamada) and (sampleEntity.age + 10) > 20 and sampleEntity.role in [Admin, Normal] and sampleEntity.updateAt > current_timestamp and sampleEntity.deleted = false and sampleEntity.salary >= 1000000");

    }

    @Test
    void testBuildOrder() {
        MSampleEntity entity = MSampleEntity.sampleEntity;

        List<OrderSpecifier> orders = List.of(entity.name.desc(), entity.role.asc());
        String resultString = orders.stream()
                .map(o -> o.toString())
                .collect(Collectors.joining(", "));

        assertThat(resultString).isEqualTo("sampleEntity.name DESC, sampleEntity.role ASC");

    }

    static enum Role {
        Admin,
        Normal;
    }

    @Data
    static class SampleEntity {

        private String name;

        private Integer age;

        private Role role;

        private boolean deleted;

        private Timestamp updateAt;

        private BigDecimal salary;

    }

    static class MSampleEntity extends EntityPathBase<SampleEntity> {

        public static final MSampleEntity sampleEntity = new MSampleEntity("sampleEntity");

        public MSampleEntity(Class<? extends SampleEntity> type, String name) {
            super(type, name);
        }

        public MSampleEntity(String name) {
            super(SampleEntity.class, name);
        }

        public final StringPath name = createString("name");

        public final NumberPath<Integer> age = createNumber("age", Integer.class);

        public final EnumPath<Role> role = createEnum("role", Role.class);

        public final BooleanPath deleted = createBoolean("deleted");

        public final SqlTimestampPath updateAt = createSqlTimestamp("updateAt");

        public final NumberPath<BigDecimal> salary = createNumber("salary", BigDecimal.class);

    }
}
