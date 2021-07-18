package com.github.mygreen.sqlmapper.core.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;

/**
 * SQLiteの方言の定義。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqliteDialect extends DialectBase {

    /**
     * {@inheritDoc}
     *
     * @return {@literal "sqlite"} を返します。
     */
    @Override
    public String getName() {
        return "sqlite";
    }

   /**
    * {@inheritDoc}
    *
    * @return
    * <ul>
    *  <li>{@link GenerationType#IDENTITY} : {@literal true}</li>
    *  <li>{@link GenerationType#SEQUENCE} : {@literal false}</li>
    *  <li>{@link GenerationType#TABLE} : {@literal true}</li>
     *  <li>{@link GenerationType#UUID} : {@literal true}</li>
    * </ul>
    */
   @Override
   public boolean supportsGenerationType(GenerationType generationType) {
       switch(generationType) {
           case IDENTITY:
               return true;
           case SEQUENCE:
               return false;
           case TABLE:
               return true;
           case UUID:
               return true;
           default:
               return false;
       }
   }

    @Override
    public DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName) {
        // シーケンスはサポートしない
        return null;
    }
}
