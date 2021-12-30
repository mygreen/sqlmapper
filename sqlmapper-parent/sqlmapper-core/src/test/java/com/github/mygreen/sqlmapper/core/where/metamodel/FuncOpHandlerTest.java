package com.github.mygreen.sqlmapper.core.where.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.test.config.NoDbTestConfig;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.LocalDatePath;
import com.github.mygreen.sqlmapper.metamodel.LocalDateTimePath;
import com.github.mygreen.sqlmapper.metamodel.LocalTimePath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

import lombok.Data;


/**
 * {@link FuncOpHandler}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=NoDbTestConfig.class)
public class FuncOpHandlerTest extends MetamodelTestSupport {

    @Entity
    @Data
    public static class TestEntity {

        private long id;

        private String name;

        private String address;

        private BigDecimal salary;

        private LocalDate birthday;

        private LocalTime startTime;

        private LocalDateTime updateDateTime;

    }

    public static class MTestEntity extends EntityPathBase<TestEntity> {

        public static final MTestEntity test = new MTestEntity("test");

        public MTestEntity(Class<? extends TestEntity> type, String name) {
            super(type, name);
        }

        public MTestEntity(String name) {
            super(TestEntity.class, name);
        }

        public final NumberPath<Long> id = createNumber("id", Long.class);

        public final StringPath name = createString("name");

        public final StringPath address = createString("address");

        public final NumberPath<BigDecimal> salary = createNumber("salary", BigDecimal.class);

        public final LocalDatePath birthday = createLocalDate("birthday");

        public final LocalTimePath startTime = createLocalTime("startTime");

        public final LocalDateTimePath updateDateTime = createLocalDateTime("updateDateTime");

    }

    @DisplayName("lower(A)")
    @Test
    public void testLower() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.name.lower().eq("yamada");

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("lower(T1_.NAME) = ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("yamada");

    }

    @DisplayName("upper(A)")
    @Test
    public void testUpper() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.name.upper().eq("YAMADA");

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("upper(T1_.NAME) = ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("YAMADA");

    }

    @DisplayName("concat(A, B)")
    @Test
    public void testConcat() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.name.concat(" Taro").eq("Yamada Taro").or(entity.name.concat(entity.address).eq("Yamada XXX"));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("concat(T1_.NAME, ?) = ? or concat(T1_.NAME, T1_.ADDRESS) = ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(" Taro", "Yamada Taro", "Yamada XXX");

    }

    @DisplayName("current_date")
    @Test
    public void testCurrentDate() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.birthday.after(LocalDatePath.currentDate());

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.BIRTHDAY > current_date");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly();

    }

    @DisplayName("current_time")
    @Test
    public void testCurrentTime() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.startTime.after(LocalTimePath.currentTime()).or(entity.startTime.before(LocalTimePath.currentTime(3)));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.START_TIME > current_time or T1_.START_TIME < current_time(3)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly();

    }

    @DisplayName("current_timestamp")
    @Test
    public void testCurrentTimestamp() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.updateDateTime.after(LocalDateTimePath.currentTimestamp()).or(entity.updateDateTime.before(LocalDateTimePath.currentTimestamp(3)));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.UPDATE_DATE_TIME > current_timestamp or T1_.UPDATE_DATE_TIME < current_timestamp(3)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly();

    }

    @Test
    public void testCustomFunction() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.name.function("custom_format($left, 'yyyMMdd')").returnString()
                .function("custom_is_valid($left, ?)", "TEST").returnBoolean();

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("custom_is_valid(custom_format(T1_.NAME, 'yyyMMdd'), ?)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("TEST");

    }



}
