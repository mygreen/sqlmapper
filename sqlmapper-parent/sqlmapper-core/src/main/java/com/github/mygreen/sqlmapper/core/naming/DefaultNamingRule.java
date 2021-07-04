package com.github.mygreen.sqlmapper.core.naming;


/**
 * {@link NamingRule}の標準実装。
 * <p>エンティティのクラス名、プロパティ名をキャメルケースから、DBのテーブル名、カラム名としてスネークケースに変換する。</p>
 * <p>大文字に変換する。</p>
 *
 *
 * @author T.TSUCHIE
 *
 */
public class DefaultNamingRule implements NamingRule {

    @Override
    public String entityToTable(final String entityName) {

        // パッケージ名の除去
        String simpleName = entityName;
        int index = simpleName.lastIndexOf('.');

        if(index >= 0){
            simpleName = simpleName.substring(index + 1);
        }

        // 内部クラスの場合、クラス名を排除する。
        index = simpleName.lastIndexOf('$');
        if(index >= 0){
            simpleName = simpleName.substring(index + 1);
        }

        StringBuilder sb = new StringBuilder();

        for(int i=0; i < simpleName.length(); i++) {
            char c = simpleName.charAt(i);

            if('A' <= c && c <= 'Z' && sb.length() > 0) {
                // 先頭以外が大文字の場合
                sb.append('_');
            }
            sb.append(String.valueOf(c).toUpperCase());
        }

        return sb.toString();

    }

//    @Override
//    public String tableToEntity(final String tableName) {
//
//        StringBuilder sb = new StringBuilder();
//
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }

    @Override
    public String propertyToColumn(final String propertyName) {
        StringBuilder sb = new StringBuilder();

        for(int i=0; i < propertyName.length(); i++) {
            char c = propertyName.charAt(i);
            if('A' <= c && c <= 'Z') {
                // 大文字の場合
                sb.append('_');
            }

            sb.append(String.valueOf(c).toUpperCase());
        }

        return sb.toString();
    }

    @Override
    public String columnToProperty(final String columnName) {
        StringBuilder sb = new StringBuilder();

        boolean uppercase = false;

        for(int i=0; i < columnName.length(); i++){
            char c = columnName.charAt(i);
            if(c == '_'){
                uppercase = true;

            } else {
                if(uppercase){
                    sb.append(String.valueOf(c).toUpperCase());
                    uppercase = false;
                } else {
                    sb.append(String.valueOf(c).toLowerCase());
                }
            }
        }

        return sb.toString();
    }

}
