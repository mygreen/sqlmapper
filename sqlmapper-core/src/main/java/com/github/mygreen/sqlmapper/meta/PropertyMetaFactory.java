package com.github.mygreen.sqlmapper.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.ReflectionUtils;

import com.github.mygreen.sqlmapper.annotation.Column;
import com.github.mygreen.sqlmapper.annotation.Enumerated;
import com.github.mygreen.sqlmapper.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.annotation.Id;
import com.github.mygreen.sqlmapper.annotation.SequenceGenerator;
import com.github.mygreen.sqlmapper.annotation.TableGenerator;
import com.github.mygreen.sqlmapper.annotation.Temporal;
import com.github.mygreen.sqlmapper.annotation.Version;
import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.id.IdGenerator;
import com.github.mygreen.sqlmapper.id.IdentityIdGenerator;
import com.github.mygreen.sqlmapper.id.SequenceIdGenerator;
import com.github.mygreen.sqlmapper.id.TableIdContext;
import com.github.mygreen.sqlmapper.id.TableIdGenerator;
import com.github.mygreen.sqlmapper.id.TableIdIncrementer;
import com.github.mygreen.sqlmapper.id.UUIDGenerator;
import com.github.mygreen.sqlmapper.localization.MessageBuilder;
import com.github.mygreen.sqlmapper.naming.NamingRule;
import com.github.mygreen.sqlmapper.type.ValueType;
import com.github.mygreen.sqlmapper.type.ValueTypeRegistry;
import com.github.mygreen.sqlmapper.util.ClassUtils;
import com.github.mygreen.sqlmapper.util.NameUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * プロパティのメタ情報を作成します。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
public class PropertyMetaFactory {

    @Getter
    @Setter
    @Autowired
    private NamingRule namingRule;

    @Getter
    @Setter
    @Autowired
    private MessageBuilder messageBuilder;

    @Getter
    @Setter
    @Autowired
    private ValueTypeRegistry valueTypeRegistry;

    @Getter
    @Setter
    @Autowired
    private Dialect dialect;

    @Getter
    @Setter
    @Autowired
    private DataSource dataSource;

    @Getter
    @Setter
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Getter
    @Setter
    @Autowired
    private Environment env;

    /**
     * プロパティのメタ情報を作成します。
     * @param field フィールド
     * @param entityMeta エンティティのメタ情報
     * @return プロパティのメタ情報
     */
    public PropertyMeta create(final Field field, final EntityMeta entityMeta) {

        final Class<?> declaringClass = field.getDeclaringClass();
        final PropertyMeta propertyMeta = new PropertyMeta(field.getName(), field.getType());
        doField(propertyMeta, field);

        // フィールドに対するgetter/setterメソッドを設定します。
        for(Method method : declaringClass.getMethods()) {
            ReflectionUtils.makeAccessible(method);

            int modifiers = method.getModifiers();
            if(Modifier.isStatic(modifiers)) {
                continue;
            }

            if(ClassUtils.isSetterMethod(method)) {
                doSetterMethod(propertyMeta, method);

            } else if(ClassUtils.isGetterMethod(method) || ClassUtils.isBooleanGetterMethod(method)) {
                doGetterMethod(propertyMeta, method);
            }
        }

        // 永続化対象のプロパティはカラム情報を設定します。
        if(!propertyMeta.isTransient()) {

            doColumnMeta(propertyMeta);
            doIdGenerator(propertyMeta, entityMeta);

            // プロパティに対する型変換を設定します。
            ValueType<?> valueType = valueTypeRegistry.findValueType(propertyMeta);
            propertyMeta.setValueType(valueType);


            validateColumnProperty(declaringClass, propertyMeta);

        }

        return propertyMeta;

    }

    /**
     * プロパティのメタ情報に対する処理を実行します。
     * @param propertyMeta プロパティのメタ情報
     * @param field フィールド情報
     */
    private void doField(final PropertyMeta propertyMeta, final Field field) {

        propertyMeta.setField(field);

        final Annotation[] annos = field.getAnnotations();
        for(Annotation anno : annos) {
            if(!isSupportedAnnotation(anno)) {
                continue;
            }

            final Class<? extends Annotation> annoClass = anno.annotationType();

            if(propertyMeta.hasAnnotation(annoClass)) {
                final String message = messageBuilder.create("property.anno.duplicated")
                        .varWithClass("classType", field.getDeclaringClass())
                        .var("property", field.getName())
                        .varWithAnno("anno", annoClass)
                        .format();
                log.warn(message);
                continue;
            }

            propertyMeta.addAnnotation(annoClass, anno);
        }
    }

    /**
     * サポートするアノテーションか判定する。
     * <p>確実に重複するJava標準のアノテーションは除外するようにします。</p>
     *
     * @param anno 判定対象のアノテーション
     * @return tureのときサポートします。
     */
    private boolean isSupportedAnnotation(final Annotation anno) {

        final String name = anno.annotationType().getName();
        if(name.startsWith("java.lang.annotation.")) {
            return false;
        }

        return true;
    }

    /**
     * setterメソッドの情報を処理する。
     * @param propertyMeta プロパティのメタ情報
     * @param method setterメソッド
     */
    private void doSetterMethod(final PropertyMeta propertyMeta, final Method method) {

        final String methodName = method.getName();
        final String propertyName = NameUtils.uncapitalize(methodName.substring(3));

        if(!propertyMeta.getName().equals(propertyName)) {
            // プロパティ名が一致しない場合はスキップする
            return;
        }

        propertyMeta.setWriteMethod(method);

        final Annotation[] annos = method.getAnnotations();
        for(Annotation anno : annos) {
            if(!isSupportedAnnotation(anno)) {
                continue;
            }

            final Class<? extends Annotation> annoClass = anno.annotationType();

            if(propertyMeta.hasAnnotation(annoClass)) {
                final String message = messageBuilder.create("property.anno.duplicated")
                        .varWithClass("classType", method.getDeclaringClass())
                        .var("property", method.getName())
                        .varWithAnno("anno", annoClass)
                        .format();
                log.warn(message);
                continue;
            }

            propertyMeta.addAnnotation(annoClass, anno);
        }

    }

    /**
     * getterメソッドの情報を処理する。
     * @param propertyMeta プロパティのメタ情報
     * @param method getterメソッド
     */
    private void doGetterMethod(final PropertyMeta propertyMeta, final Method method) {

        final String methodName = method.getName();
        final String propertyName;
        if(methodName.startsWith("get")) {
            propertyName = NameUtils.uncapitalize(methodName.substring(3));
        } else {
            // 「is」から始まる場合
            propertyName = NameUtils.uncapitalize(methodName.substring(2));
        }

        if(!propertyMeta.getName().equals(propertyName)) {
            // プロパティ名が一致しない場合はスキップする
            return;
        }

        propertyMeta.setReadMethod(method);

        final Annotation[] annos = method.getAnnotations();
        for(Annotation anno : annos) {
            if(!isSupportedAnnotation(anno)) {
                continue;
            }

            final Class<? extends Annotation> annoClass = anno.annotationType();

            if(propertyMeta.hasAnnotation(annoClass)) {
                final String message = messageBuilder.create("property.anno.duplicated")
                        .varWithClass("classType", method.getDeclaringClass())
                        .var("property", method.getName())
                        .varWithAnno("anno", annoClass)
                        .format();
                log.warn(message);
                continue;
            }

            propertyMeta.addAnnotation(annoClass, anno);
        }

    }

    /**
     * カラム情報を処理する。
     * @param propertyMeta プロパティのメタ情報
     */
    private void doColumnMeta(final PropertyMeta propertyMeta) {

        final ColumnMeta columnMeta = new ColumnMeta();

        final String defaultColumnName = namingRule.propertyToColumn(propertyMeta.getName());

        Optional<Column> annoColumn = propertyMeta.getAnnotation(Column.class);
        if(annoColumn.isPresent()) {
            if(!annoColumn.get().name().isEmpty()) {
                columnMeta.setName(annoColumn.get().name());
            } else {
                columnMeta.setName(defaultColumnName);
            }

            columnMeta.setInsertable(annoColumn.get().insertable());
            columnMeta.setUpdatable(annoColumn.get().insertable());

        } else {
            columnMeta.setName(defaultColumnName);
        }

        propertyMeta.setColumnMeta(columnMeta);

    }

    /**
     * 主キーの生成情報を処理します。
     * @param propertyMeta プロパティのメタ情報
     * @param entityMeta エンティティのメタ情報
     */
    private void doIdGenerator(final PropertyMeta propertyMeta, final EntityMeta entityMeta) {

        if(!propertyMeta.isId()) {
            return;
        }

        Optional<GeneratedValue> annoGeneratedValue = propertyMeta.getAnnotation(GeneratedValue.class);
        if(annoGeneratedValue.isEmpty()) {
            return;
        }

        final Class<?> propertyType = propertyMeta.getPropertyType();

        GenerationType generationType = annoGeneratedValue.get().strategy();
        if(generationType == GenerationType.AUTO) {
            generationType = dialect.getDefaultGenerationType();
        }

        final IdGenerator idGenerator;
        if(generationType == GenerationType.IDENTITY) {
            IdentityIdGenerator identityIdGenerator = new IdentityIdGenerator(propertyType);
            if(!annoGeneratedValue.get().format().isEmpty()) {
                identityIdGenerator.setFormatter(new DecimalFormat(annoGeneratedValue.get().format()));
            }
            idGenerator = identityIdGenerator;

        } else if(generationType == GenerationType.SEQUENCE) {
            Optional<SequenceGenerator> annoSequenceGenerator = propertyMeta.getAnnotation(SequenceGenerator.class);
            final String sequenceName;
            if(annoSequenceGenerator.isPresent()) {
                sequenceName = NameUtils.tableFullName(annoSequenceGenerator.get().sequenceName(),
                        annoSequenceGenerator.get().catalog(),
                        annoSequenceGenerator.get().schema());
            } else {
                sequenceName = entityMeta.getTableMeta().getName() + "_" + propertyMeta.getColumnMeta().getName();
            }
            SequenceIdGenerator sequenceIdGenerator = new SequenceIdGenerator(
                    dialect.getSequenceIncrementer(dataSource, sequenceName), propertyType);

            if(!annoGeneratedValue.get().format().isEmpty()) {
                sequenceIdGenerator.setFormatter(new DecimalFormat(annoGeneratedValue.get().format()));
            }

            idGenerator = sequenceIdGenerator;

        } else if(generationType == GenerationType.TABLE) {
            Optional<TableGenerator> annoTableGenerator = propertyMeta.getAnnotation(TableGenerator.class);

            final TableIdContext tableIdContext = new TableIdContext();
            tableIdContext.setTable(env.getProperty("sqlmapper.tableIdGenerator.table"));
            tableIdContext.setSchema(env.getProperty("sqlmapper.tableIdGenerator.schema"));
            tableIdContext.setCatalog(env.getProperty("sqlmapper.tableIdGenerator.catalog"));
            tableIdContext.setPkColumn(env.getProperty("sqlmapper.tableIdGenerator.pkColumn"));
            tableIdContext.setValueColumn(env.getProperty("sqlmapper.tableIdGenerator.valueColumn"));

            String sequenceName = entityMeta.getTableMeta().getName() + "_" + propertyMeta.getColumnMeta().getName();

            annoTableGenerator.ifPresent(a -> {
                if(!a.table().isEmpty()) {
                    tableIdContext.setTable(a.table());
                }

                if(!a.schema().isEmpty()) {
                    tableIdContext.setSchema(a.schema());
                }

                if(!a.catalog().isEmpty()) {
                    tableIdContext.setCatalog(a.catalog());
                }

                if(!a.pkColumnName().isEmpty()) {
                    tableIdContext.setPkColumn(a.pkColumnName());
                }

                if(!a.valueColumnName().isEmpty()) {
                    tableIdContext.setValueColumn(a.valueColumnName());
                }

                if(a.initialValue() >= 0) {
                    tableIdContext.setInitialValue(a.initialValue());
                } else {
                    throw new InvalidEntityException(entityMeta.getEntityType(), messageBuilder.create("property.anno.attr.min")
                            .varWithClass("classType", entityMeta.getEntityType())
                            .var("property", propertyMeta.getName())
                            .varWithAnno("anno", TableGenerator.class)
                            .var("attrName", "initialValue")
                            .var("attrValue", a.initialValue())
                            .var("min", 0)
                            .format());
                }

                if(a.allocationSize() >= 1) {
                    tableIdContext.setAllocationSize(a.allocationSize());
                } else {
                    throw new InvalidEntityException(entityMeta.getEntityType(), messageBuilder.create("property.anno.attr.min")
                            .varWithClass("classType", entityMeta.getEntityType())
                            .var("property", propertyMeta.getName())
                            .varWithAnno("anno", TableGenerator.class)
                            .var("attrName", "allocationSize")
                            .var("attrValue", a.allocationSize())
                            .var("min", 1)
                            .format());
                }

            });


            if(annoTableGenerator.isPresent() && annoTableGenerator.get().pkColumnName().isEmpty()) {
                sequenceName = annoTableGenerator.get().pkColumnName();
            }

            TableIdGenerator tableIdGenerator = new TableIdGenerator(
                    new TableIdIncrementer(namedParameterJdbcTemplate, tableIdContext),
                    propertyMeta.getPropertyType(), sequenceName);

            if(!annoGeneratedValue.get().format().isEmpty()) {
                tableIdGenerator.setFormatter(new DecimalFormat(annoGeneratedValue.get().format()));
            }

            idGenerator = tableIdGenerator;


        } else if(generationType == GenerationType.UUID) {
            idGenerator = new UUIDGenerator(propertyType);

        } else {
            throw new InvalidEntityException(entityMeta.getEntityType(), messageBuilder.create("property.anno.attr.notSupportValue")
                    .varWithClass("classType", entityMeta.getClass())
                    .var("property", propertyMeta.getName())
                    .varWithAnno("anno", GeneratedValue.class)
                    .var("attrName", "strategy")
                    .varWithEnum("attrValue", generationType)
                    .format());
        }

        if(!idGenerator.isSupportedType(propertyType)) {
            throw new InvalidEntityException(entityMeta.getClass(), messageBuilder.create("property.anno.notSupportTypeList")
                    .varWithClass("classType", entityMeta.getEntityType())
                    .var("property", propertyMeta.getName())
                    .varWithAnno("anno", GeneratedValue.class)
                    .varWithClass("actualType", propertyType)
                    .varWithClass("expectedTypeList", idGenerator.getSupportedTypes())
                    .format());
        }

        propertyMeta.setIdGenerator(idGenerator);
        propertyMeta.setIdGeneratonType(generationType);
        //TODO: SpringからBeanを取得するする

    }

    /**
     * カラムとなるプロパティの整合性のチェック。
     * アノテーションとクラスタイプのチェック
     *
     * @param declaringClass プロパティが定義されているクラス
     * @param propertyMeta チェック対象のプロパティ
     */
    private void validateColumnProperty(final Class<?> declaringClass, final PropertyMeta propertyMeta) {

        final Class<?> propertyType = propertyMeta.getPropertyType();

        // 主キーのタイプチェック
        if(propertyMeta.isId()
                && propertyType != String.class
                && propertyType != UUID.class
                && propertyType != Integer.class && propertyType != int.class
                && propertyType != Long.class && propertyType != long.class) {

            throw new InvalidEntityException(declaringClass, messageBuilder.create("property.anno.notSupportTypeList")
                    .varWithClass("entityType", declaringClass)
                    .var("propperty", propertyMeta.getName())
                    .varWithAnno("anno", Id.class)
                    .varWithClass("actualType", propertyType)
                    .varWithClass("expectedTypeList", String.class, Integer.class, int.class, Long.class, long.class)
                    .format());
        }

        // 主キーでないのに値の生成用のアノテーションが付与されている場合
        if(!propertyMeta.isId() && propertyMeta.hasAnnotation(GeneratedValue.class)) {

            throw new InvalidEntityException(declaringClass, messageBuilder.create("property.anno.notIdWithGeneratedValue")
                    .varWithClass("entityType", declaringClass)
                    .var("propperty", propertyMeta.getName())
                    .varWithAnno("anno", GeneratedValue.class)
                    .format());
        }


        // 列挙型のタイプチェック
        if(propertyMeta.hasAnnotation(Enumerated.class) && !propertyType.isEnum()) {

            throw new InvalidEntityException(declaringClass, messageBuilder.create("property.anno.notSupportType")
                    .varWithClass("entityType", declaringClass)
                    .var("propperty", propertyMeta.getName())
                    .varWithAnno("anno", Enumerated.class)
                    .varWithClass("actualType", propertyType)
                    .varWithClass("expectedType", Enum.class)
                    .format());
        }

        // バージョンキーのタイプチェック
        if(propertyMeta.hasAnnotation(Version.class)
                && propertyType != Integer.class && propertyType != int.class
                && propertyType != Long.class && propertyType != long.class) {

            throw new InvalidEntityException(declaringClass, messageBuilder.create("property.anno.notSupportTypeList")
                    .varWithClass("entityType", declaringClass)
                    .var("propperty", propertyMeta.getName())
                    .varWithAnno("anno", Version.class)
                    .varWithClass("actualType", propertyType)
                    .varWithClass("expectedTypeList", Integer.class, int.class, Long.class, long.class)
                    .format());

        }

        // 時制のタイプチェック
        if(!propertyMeta.hasAnnotation(Temporal.class)
                && propertyType == java.util.Date.class) {

            // 時制の型が不明なプロパティに対して、@Temporalが付与されていない場合
            throw new InvalidEntityException(declaringClass, messageBuilder.create("property.anno.requiredAnnoTemporal")
                    .varWithClass("entityType", declaringClass)
                    .var("propperty", propertyMeta.getName())
                    .varWithAnno("anno", Temporal.class)
                    .format());
        }

    }

}
