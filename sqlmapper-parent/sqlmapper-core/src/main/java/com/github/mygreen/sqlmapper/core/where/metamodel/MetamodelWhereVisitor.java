package com.github.mygreen.sqlmapper.core.where.metamodel;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.util.Assert;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.core.where.Where;
import com.github.mygreen.sqlmapper.core.where.WhereVisitor;

import lombok.RequiredArgsConstructor;


/**
 * メタモデルの条件式のVisitorの実装。
 *
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class MetamodelWhereVisitor implements WhereVisitor {

    /**
     * 検索対象となるテーブルのエンティティ情報
     */
    private final Map<Class<?>, EntityMeta> entityMetaMap;

    /**
     * SQLの方言
     */
    private final Dialect dialect;

    /**
     * エンティティメタファクトリ
     */
    private final EntityMetaFactory entityMetaFactory;

    /**
     * テーブル名のエイリアス管理
     */
    private final TableNameResolver tableNameResolver;

    /**
     * 式を巡回するときの情報。
     *
     */
    private Optional<VisitorContext> visitorContext = Optional.empty();

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException 引数whereのインスタンスが {@link MetamodelWhere}でない場合にスローされます。
     */
    @Override
    public void visit(Where where) {
        Assert.isInstanceOf(MetamodelWhere.class, where);

        visit((MetamodelWhere) where);

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private synchronized void visit(MetamodelWhere where) {

        synchronized (visitorContext) {
            // 処理前に初期化する
            visitorContext = Optional.empty();

            // Metamodel用のVisitorに委譲する
            VisitorContext context = new VisitorContext(entityMetaMap, dialect, entityMetaFactory, tableNameResolver);
            ExpressionVisitor visitor = new ExpressionVisitor();

            // DBごとにカスタマイズされた演算子の処理の登録
            dialect.getOperationHandlerMap().forEach((k, v) -> visitor.register((Class)k, v));

            where.getPredicate().accept(visitor, context);

            // 結果を格納する
            this.visitorContext = Optional.of(context);
        }

    }

    /**
     * 組み立てたクライテリアを取得します。
     * @return クライテリア
     * @throws NoSuchElementException {@link #visit(Where)}による処理が完了していない場合にスローされます。
     */
    public String getCriteria() {
        return visitorContext.get().getCriteria().toString();
    }

    /**
     * SQLのパラメータ変数を取得します。
     * SQLのプレースホルダ―順に設定されています。
     * @return SQL中のパラメータ変数。
     * @throws NoSuchElementException {@link #visit(Where)}による処理が完了していない場合にスローされます。
     */
    public List<Object> getParamValues() {
        return Collections.unmodifiableList(visitorContext.get().getParamValues());
    }

}
