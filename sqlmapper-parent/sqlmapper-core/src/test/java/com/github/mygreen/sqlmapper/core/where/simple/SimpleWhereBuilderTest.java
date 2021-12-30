package com.github.mygreen.sqlmapper.core.where.simple;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mygreen.sqlmapper.core.where.Where;


/**
 * {@link SimpleWhereBuilder}のテスタ。
 *
 * @author T.TSUCHIE
 *
 */
public class SimpleWhereBuilderTest extends SimpleWhereBuilder {

    @DisplayName("単純なAND")
    @Test
    void testSimpleWhere_And() {

        Where where = new SimpleWhereBuilder()
                .exp("first_name = ?", "Taro")
                .exp("age >= ?", 20)
                ;

        SimpleWhereVisitor visitor = new SimpleWhereVisitor();
        where.accept(visitor);

        String sql = visitor.getCriteria();
        List<Object> params = visitor.getParamValues();

        assertThat(sql).isEqualTo("first_name = ? and age >= ?");
        assertThat(params).containsExactly("Taro", 20);

    }

    @DisplayName("単純なOR")
    @Test
    void testSimpleWhere_Or() {


        Where where = new SimpleWhereBuilder()
                .exp("first_name = ?", "Taro")
                .exp("age >= ?", 20)
                .or().exp("last_name like ?", "%Yama%")
                ;

        SimpleWhereVisitor visitor = new SimpleWhereVisitor();
        where.accept(visitor);

        String sql = visitor.getCriteria();
        List<Object> params = visitor.getParamValues();

        assertThat(sql).isEqualTo("(first_name = ? and age >= ?) or (last_name like ?)");
        assertThat(params).containsExactly("Taro", 20, "%Yama%");

    }

    @DisplayName("ネストしたANDとORの組み合わせ")
    @Test
    void testSimpleWhere_Complex() {

        Where where = new SimpleWhereBuilder()
                .exp("first_name = ?", "Taro")
                .exp("age >= ?", 20)
                .or().exp("last_name like ?", "%Yama%")
                .and(new SimpleWhere().exp("role = ?", "ADMIN"))
                ;

        SimpleWhereVisitor visitor = new SimpleWhereVisitor();
        where.accept(visitor);

        String sql = visitor.getCriteria();
        List<Object> params = visitor.getParamValues();

        assertThat(sql).isEqualTo("(first_name = ? and age >= ?) or (last_name like ? and (role = ?))");
        assertThat(params).containsExactly("Taro", 20, "%Yama%", "ADMIN");

    }

    @DisplayName("プレースホルダーの個数が一致しない場合")
    @Test
    void testExp_notMatchPlaceHolder() {

        assertThatThrownBy(() -> new SimpleWhereBuilder().exp("first_name = ?"))
            .isInstanceOf(IllegalArgumentException.class);


        assertThatThrownBy(() -> new SimpleWhereBuilder().exp("first_name =", "Yamada"))
        .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new SimpleWhereBuilder().exp("first_name = ? and age >= ?", "Yamada"))
        .isInstanceOf(IllegalArgumentException.class);

    }
}
