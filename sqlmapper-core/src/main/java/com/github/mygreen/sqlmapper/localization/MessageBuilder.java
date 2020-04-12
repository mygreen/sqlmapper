package com.github.mygreen.sqlmapper.localization;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * メッセージを組み立てるクラス。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class MessageBuilder {

    private final MessageSourceAccessor messageSource;

    private final MessageInterpolator messageInterpolator;

    public MessageBuilder(@NonNull MessageSource messageSource, @NonNull MessageInterpolator messageInterpolator) {
        this.messageSource = new MessageSourceAccessor(messageSource);
        this.messageInterpolator = messageInterpolator;
    }

    /**
     * メッセージの組み立ての開始
     * @param code メッセージコード
     * @return
     */
    public Builder create(final String code) {
        Assert.hasLength(code, "code should not be empty.");

        return new Builder(messageSource, messageInterpolator, code);
    }

    public static class Builder {

        private final MessageSourceAccessor messageSource;

        private final MessageInterpolator messageInterpolator;

        private final String code;

        private Map<String, Object> vars = new HashMap<>();

        private Builder(MessageSourceAccessor messageSource, MessageInterpolator messageInterpolator, String code) {
            this.messageSource = messageSource;
            this.messageInterpolator = messageInterpolator;
            this.code = code;
        }

        /**
         * メッセージ変数を追加する。
         * @param key 変数名
         * @param value 値
         * @return 自身のインスタンス
         */
        public Builder var(final String key, final Object value) {
            vars.put(key, value);
            return this;
        }

        /**
         * メッセージ変数として配列を追加する。
         * @param key 変数名
         * @param values 値
         * @return 自身のインスタンス
         */
        public Builder varWithArrays(final String key, final Object... values) {
            vars.put(key, values);
            return this;
        }

        /**
         * メッセージ変数としてアノテーション名を追加する。
         * @param key 変数名
         * @param annoClass アノテーションのクラス名
         * @return 自身のインスタンス
         */
        public Builder varWithAnno(final String key, final Class<? extends Annotation> annoClass) {
            return var(key, "@" + annoClass.getSimpleName());
        }

        /**
         * メッセージ変数としてクラス名を追加する。
         * <p>クラス名は、FQCNの形式</p>
         * @param key 変数名
         * @param clazz クラスタイプ
         * @return 自身のインスタンス
         */
        public Builder varWithClass(final String key, final Class<?> clazz) {

            final String className;
            if(clazz.isArray()) {
                // 配列の場合
                Class<?> elementType = clazz.getComponentType();
                className = elementType.getName() + "[]";

            } else {
                className = clazz.getName();

            }

            return var(key, className);
        }

        /**
         * メッセージ変数としてクラス名を追加する。
         * <p>クラス名は、FQCNの形式</p>
         * @param key 変数名
         * @param clazzes クラスタイプ
         * @return 自身のインスタンス
         */
        public Builder varWithClass(final String key, final Class<?>... clazzes) {

            List<String> list = new ArrayList<>();
            for(Class<?> clazz : clazzes) {
                final String className;
                if(clazz.isArray()) {
                    // 配列の場合
                    Class<?> elementType = clazz.getComponentType();
                    className = elementType.getName() + "[]";

                } else {
                    className = clazz.getName();
                }

                list.add(className);
            }

            return var(key, list);
        }

        /**
         * メッセージ変数として列挙型を追加する。
         * @param key 変数名
         * @param enums 列挙型の要素
         * @return 自身のタイプ
         */
        public Builder varWithEnum(final String key, final Enum<?> enums) {

            vars.put(key, enums.getClass().getSimpleName() + "#" + enums.name());
            return this;

        }

        /**
         * メッセージをフォーマットして値を取得します。
         * <p>変換したメッセージに対しても再帰的に処理しません。</p>
         * @return フォーマットしたメッセージ
         * @throws IllegalArgumentException 指定したメッセージコードが見つからない場合
         */
        public String format() {

            return format(false);
        }

        /**
         * メッセージをフォーマットして値を取得します。
         * @param recusrsive 変換したメッセージに対しても再帰的に処理するかどうか。
         * @return フォーマットしたメッセージ
         * @throws NoSuchMessageException 指定したメッセージコードが見つからない場合
         */
        public String format(final boolean recusrsive) {

            final String message = messageSource.getMessage(code);
            return messageInterpolator.interpolate(message, vars, recusrsive, messageSource);
        }

    }

}
