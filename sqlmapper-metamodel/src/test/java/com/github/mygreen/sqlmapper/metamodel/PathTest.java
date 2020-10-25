package com.github.mygreen.sqlmapper.metamodel;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.mygreen.sqlmapper.metamodel.BooleanPath;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.EnumPath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.OrderSpecifier;
import com.github.mygreen.sqlmapper.metamodel.Path;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.SqlTimestampPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;
import com.github.mygreen.sqlmapper.metamodel.Visitor;
import com.github.mygreen.sqlmapper.metamodel.expression.Constant;
import com.github.mygreen.sqlmapper.metamodel.expression.SqlTimestampExpression;
import com.github.mygreen.sqlmapper.metamodel.expression.SubQueryExpression;
import com.github.mygreen.sqlmapper.metamodel.operation.Operation;
import com.github.mygreen.sqlmapper.metamodel.operator.BinaryOp;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Slf4j
class PathTest {

    @Test
    void testBuildCondition() {

        MSampleEntity entity = MSampleEntity.sampleEntity;
        Predicate exp = entity.name.lower().contains("yamada")
                .and(entity.age.add(10).gt(20))
                .and(entity.role.eq(Role.Admin))
                .and(entity.updateAt.after(SqlTimestampExpression.currentTimestamp()))
                .and(entity.deleted.isFalse());

        Visitor<Void> visitor = new SampleVisitor();
        exp.accept(visitor, null);

    }

    @Test
    void testBuildOrder() {
        MSampleEntity entity = MSampleEntity.sampleEntity;

        List<OrderSpecifier> orders = List.of(entity.name.desc());
        for(OrderSpecifier order : orders) {
            System.out.println(order.getPath().getPathMeta().getElement());
        }

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

        public final SqlTimestampPath updateAt = createSqlTimestampPath("updateAt");


    }

    static class SampleVisitor implements Visitor<Void> {

        @Override
        public void visit(Constant<?> expr, Void context) {
            log.info("visit - Constant");

        }

        @Override
        public void visit(Operation<?> expr, Void context) {
            log.info("visit - Operation={}", expr.getOperator());

            Operator op = expr.getOperator();
            if(op instanceof BinaryOp) {
                // 2項演算子の場合、プロパティとの比較かどうか判定する

            }

            expr.getArgs().forEach(arg -> arg.accept(this, context));

        }

        @Override
        public void visit(Path<?> expr, Void context) {
            log.info("visit - Path={}", expr.getPathMeta().getElement());

        }

        @Override
        public void visit(SubQueryExpression<?> expr, Void context) {
            log.info("visit - SubQueryExpression");

        }

    }
}
