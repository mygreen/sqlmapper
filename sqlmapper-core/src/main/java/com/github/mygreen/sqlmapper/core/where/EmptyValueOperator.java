package com.github.mygreen.sqlmapper.core.where;

import lombok.Getter;

/**
 * 値を持たない演算子の式を表現します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class EmptyValueOperator implements ValueOperator {

    /**
     * プロパティ名
     */
    @Getter
    private final String propertyName;

    public EmptyValueOperator(final CharSequence propertyName) {
        this.propertyName = propertyName.toString();
    }

    @Override
    public void accept(WhereVisitor visitor) {
        visitor.visit(this);
    }

    /**
    * SQLを組み立てます。
    * @param columnName カラム名
    * @return 組み立てたSQL
    */
   public abstract String getSql(String columnName);

   public static class IS_NULL extends EmptyValueOperator {

       public IS_NULL(CharSequence propertyName) {
           super(propertyName);
       }

       @Override
       public String getSql(String columnName) {

           return columnName + " IS NULL";
       }

   }

   public static class IS_NOT_NULL extends EmptyValueOperator {

       public IS_NOT_NULL(CharSequence propertyName) {
           super(propertyName);
       }

       @Override
       public String getSql(String columnName) {

           return columnName + " IS NOT NULL";
       }

   }
}
