package com.github.mygreen.sqlmapper.core.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.sqlmapper.core.annotation.Column;
import com.github.mygreen.sqlmapper.core.annotation.Enumerated;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.SequenceGenerator;
import com.github.mygreen.sqlmapper.core.annotation.TableGenerator;
import com.github.mygreen.sqlmapper.core.annotation.Temporal;
import com.github.mygreen.sqlmapper.core.annotation.Version;
import com.github.mygreen.sqlmapper.core.config.TableIdGeneratorProperties;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.id.IdGenerationContext;
import com.github.mygreen.sqlmapper.core.id.IdGenerator;
import com.github.mygreen.sqlmapper.core.id.IdentityIdGenerator;
import com.github.mygreen.sqlmapper.core.id.SequenceIdGenerator;
import com.github.mygreen.sqlmapper.core.id.TableIdContext;
import com.github.mygreen.sqlmapper.core.id.TableIdGenerator;
import com.github.mygreen.sqlmapper.core.id.TableIdIncrementer;
import com.github.mygreen.sqlmapper.core.id.UUIDGenerator;
import com.github.mygreen.sqlmapper.core.naming.NamingRule;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.type.ValueTypeRegistry;
import com.github.mygreen.sqlmapper.core.util.ClassUtils;
import com.github.mygreen.sqlmapper.core.util.NameUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * プロパティのメタ情報を作成します。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class PropertyMetaFactory {

    @Getter
    @Setter
    @Autowired
    private NamingRule namingRule;

    @Getter
    @Setter
    @Autowired
    private MessageFormatter messageFormatter;

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
    private JdbcTemplate jdbcTemplate;

    @Getter
    @Setter
    @Autowired
    private TableIdGeneratorProperties tableIdGeneratorProperties;

    @Getter
    @Setter
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * プロパティのメタ情報を作成します。
     * @param field フィールド
     * @param entityMeta エンティティのメタ情報。空の場合はID情報の処理をスキップします。
     * @return プロパティのメタ情報
     */
    public PropertyMeta create(final Field field, final Optional<EntityMeta> entityMeta) {

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
        if(!propertyMeta.isEmbedded() && !propertyMeta.isTransient()) {

            doColumnMeta(propertyMeta);
            entityMeta.ifPresent(em -> doIdGenerator(propertyMeta, em));

            // プロパティに対する型変換を設定します。
            ValueType<?> valueType = valueTypeRegistry.findValueType(propertyMeta);

            // OracleなどBoolean型を純粋にサポートしていない場合は、int型に変換するタイプに変換する。
            valueType = dialect.getValueType(valueType);
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
        if(generationType != GenerationType.AUTO && !dialect.supportsGenerationType(generationType)) {
            throw new InvalidEntityException(entityMeta.getEntityType(), messageFormatter.create("property.anno.attr.notSupportForDialect")
                    .paramWithClass("classType", entityMeta.getEntityType())
                    .param("property", propertyMeta.getName())
                    .paramWithAnno("anno", GeneratedValue.class)
                    .param("attrName", "strategy")
                    .param("attrValue", generationType)
                    .param("dialectName", dialect.getName())
                    .format());
        }

        // 生成戦略の補完
        if(generationType == GenerationType.AUTO) {
            generationType = dialect.getDefaultGenerationType();
        }

        final IdGenerator idGenerator;
        if(StringUtils.hasLength(annoGeneratedValue.get().generator())) {
            /*
             * 属性「generator」が指定されている場合は、「strategy」の値は無視する。
             * ただし、IDENTITYの場合はクエリ実行前に処理されてしまうので、
             * Autoに補完して間違った処理をされないようにする。
             */
            generationType = GenerationType.AUTO;

            final String generatorName = annoGeneratedValue.get().generator();
            try {
                idGenerator = applicationContext.getBean(generatorName, IdGenerator.class);

            } catch(NoSuchBeanDefinitionException  e) {
                throw new InvalidEntityException(entityMeta.getEntityType(), messageFormatter.create("property.anno.attr.noSuchBeanDefinition")
                        .paramWithClass("classType", entityMeta.getEntityType())
                        .param("property", propertyMeta.getName())
                        .paramWithAnno("anno", GeneratedValue.class)
                        .param("attrName", "generator")
                        .param("attrValue", generatorName)
                        .format(), e);
            } catch(BeanNotOfRequiredTypeException e) {
                throw new InvalidEntityException(entityMeta.getEntityType(), messageFormatter.create("property.anno.attr.beanNotOfRequiredType")
                        .paramWithClass("classType", entityMeta.getEntityType())
                        .param("property", propertyMeta.getName())
                        .paramWithAnno("anno", GeneratedValue.class)
                        .param("attrName", "generator")
                        .param("attrValue", generatorName)
                        .paramWithClass("requiredType", IdGenerator.class)
                        .format(), e);
            }
        } else if(generationType == GenerationType.IDENTITY) {
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
            tableIdContext.setTable(tableIdGeneratorProperties.getTable());
            tableIdContext.setSchema(tableIdGeneratorProperties.getSchema());
            tableIdContext.setCatalog(tableIdGeneratorProperties.getCatalog());
            tableIdContext.setPkColumn(tableIdGeneratorProperties.getPkColumn());
            tableIdContext.setValueColumn(tableIdGeneratorProperties.getValueColumn());
            tableIdContext.setAllocationSize(tableIdGeneratorProperties.getAllocationSize());
            tableIdContext.setInitialValue(tableIdGeneratorProperties.getInitialValue());

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

                if(!a.pkColumn().isEmpty()) {
                    tableIdContext.setPkColumn(a.pkColumn());
                }

                if(!a.valueColumn().isEmpty()) {
                    tableIdContext.setValueColumn(a.valueColumn());
                }

                if(a.initialValue() >= 0L) {
                    tableIdContext.setInitialValue(a.initialValue());
                } else {
                    throw new InvalidEntityException(entityMeta.getEntityType(), messageFormatter.create("property.anno.attr.min")
                            .paramWithClass("classType", entityMeta.getEntityType())
                            .param("property", propertyMeta.getName())
                            .paramWithAnno("anno", TableGenerator.class)
                            .param("attrName", "initialValue")
                            .param("attrValue", a.initialValue())
                            .param("min", 0)
                            .format());
                }

                if(a.allocationSize() >= 1L) {
                    tableIdContext.setAllocationSize(a.allocationSize());
                } else {
                    throw new InvalidEntityException(entityMeta.getEntityType(), messageFormatter.create("property.anno.attr.min")
                            .paramWithClass("classType", entityMeta.getEntityType())
                            .param("property", propertyMeta.getName())
                            .paramWithAnno("anno", TableGenerator.class)
                            .param("attrName", "allocationSize")
                            .param("attrValue", a.allocationSize())
                            .param("min", 1)
                            .format());
                }

            });


            if(annoTableGenerator.isPresent() && annoTableGenerator.get().pkColumn().isEmpty()) {
                sequenceName = annoTableGenerator.get().pkColumn();
            }

            TableIdGenerator tableIdGenerator = new TableIdGenerator(
                    new TableIdIncrementer(jdbcTemplate, tableIdContext),
                    propertyMeta.getPropertyType(), sequenceName);


            if(!annoGeneratedValue.get().format().isEmpty()) {
                try {
                    tableIdGenerator.setFormatter(new DecimalFormat(annoGeneratedValue.get().format()));
                } catch(IllegalArgumentException e) {
                    throw new InvalidEntityException(entityMeta.getEntityType(), messageFormatter.create("property.anno.attr.wrongFormat")
                            .paramWithClass("classType", entityMeta.getEntityType())
                            .param("property", propertyMeta.getName())
                            .paramWithAnno("anno", TableGenerator.class)
                            .param("attrName", "format")
                            .param("attrValue", annoGeneratedValue.get().format())
                            .format(), e);
                }
            }

            idGenerator = tableIdGenerator;


        } else if(generationType == GenerationType.UUID) {
            idGenerator = new UUIDGenerator(propertyType);

        } else {
            throw new InvalidEntityException(entityMeta.getEntityType(), messageFormatter.create("property.anno.attr.notSupportValue")
                    .paramWithClass("classType", entityMeta.getClass())
                    .param("property", propertyMeta.getName())
                    .paramWithAnno("anno", GeneratedValue.class)
                    .param("attrName", "strategy")
                    .paramWithEnum("attrValue", generationType)
                    .format());
        }

        if(!idGenerator.isSupportedType(propertyType)) {
            throw new InvalidEntityException(entityMeta.getClass(), messageFormatter.create("property.anno.notSupportTypeList")
                    .paramWithClass("classType", entityMeta.getEntityType())
                    .param("property", propertyMeta.getName())
                    .paramWithAnno("anno", GeneratedValue.class)
                    .paramWithClass("actualType", propertyType)
                    .paramWithClass("expectedTypeList", idGenerator.getSupportedTypes())
                    .format());
        }

        propertyMeta.setIdGenerator(idGenerator);
        propertyMeta.setIdGeneratonType(generationType);

        // 生成対象のIDの情報
        final IdGenerationContext generationContext = new IdGenerationContext();
        generationContext.setTableMeta(entityMeta.getTableMeta());
        generationContext.setColumnMeta(propertyMeta.getColumnMeta());
        generationContext.setEntityType(entityMeta.getEntityType());
        generationContext.setPropertyType(propertyMeta.getPropertyType());
        propertyMeta.setIdGenerationContext(generationContext);

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

            throw new InvalidEntityException(declaringClass, messageFormatter.create("property.anno.notSupportTypeList")
                    .paramWithClass("entityType", declaringClass)
                    .param("propperty", propertyMeta.getName())
                    .paramWithAnno("anno", Id.class)
                    .paramWithClass("actualType", propertyType)
                    .paramWithClass("expectedTypeList", String.class, Integer.class, int.class, Long.class, long.class)
                    .format());
        }

        // 主キーでないのに値の生成用のアノテーションが付与されている場合
        if(!propertyMeta.isId() && propertyMeta.hasAnnotation(GeneratedValue.class)) {

            throw new InvalidEntityException(declaringClass, messageFormatter.create("property.anno.notIdWithGeneratedValue")
                    .paramWithClass("entityType", declaringClass)
                    .param("propperty", propertyMeta.getName())
                    .paramWithAnno("anno", GeneratedValue.class)
                    .format());
        }


        // 列挙型のタイプチェック
        if(propertyMeta.hasAnnotation(Enumerated.class) && !propertyType.isEnum()) {

            throw new InvalidEntityException(declaringClass, messageFormatter.create("property.anno.notSupportType")
                    .paramWithClass("entityType", declaringClass)
                    .param("propperty", propertyMeta.getName())
                    .paramWithAnno("anno", Enumerated.class)
                    .paramWithClass("actualType", propertyType)
                    .paramWithClass("expectedType", Enum.class)
                    .format());
        }

        // バージョンキーのタイプチェック
        if(propertyMeta.hasAnnotation(Version.class)
                && propertyType != Integer.class && propertyType != int.class
                && propertyType != Long.class && propertyType != long.class) {

            throw new InvalidEntityException(declaringClass, messageFormatter.create("property.anno.notSupportTypeList")
                    .paramWithClass("entityType", declaringClass)
                    .param("propperty", propertyMeta.getName())
                    .paramWithAnno("anno", Version.class)
                    .paramWithClass("actualType", propertyType)
                    .paramWithClass("expectedTypeList", Integer.class, int.class, Long.class, long.class)
                    .format());

        }

        // 時制のタイプチェック
        if(!propertyMeta.hasAnnotation(Temporal.class)
                && propertyType == java.util.Date.class) {

            // 時制の型が不明なプロパティに対して、@Temporalが付与されていない場合
            throw new InvalidEntityException(declaringClass, messageFormatter.create("property.anno.requiredAnnoTemporal")
                    .paramWithClass("entityType", declaringClass)
                    .param("propperty", propertyMeta.getName())
                    .paramWithAnno("anno", Temporal.class)
                    .format());
        }

    }

}
