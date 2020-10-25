package com.github.mygreen.sqlmapper.metamodel;

/**
 * プロパティのパスを表現します。
 *
 * @author T.TSUCHIE
 * @param <T> プロパティのクラスタイプ
 *
 */
public interface PropertyPath<T> extends Path<T> {

   /**
    *
    * @return 昇順の並び順
    */
    default OrderSpecifier asc() {
       return new OrderSpecifier(OrderType.ASC, this);
   }

   /**
    *
    * @return 降順の並び順
    */
   default public OrderSpecifier desc() {
       return new OrderSpecifier(OrderType.DESC, this);
   }

}
