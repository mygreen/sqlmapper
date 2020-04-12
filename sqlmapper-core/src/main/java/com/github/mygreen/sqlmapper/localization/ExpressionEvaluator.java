package com.github.mygreen.sqlmapper.localization;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * EL式を簡易的に使用するためのクラス。
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
public class ExpressionEvaluator {
    
    private final ObjectCache<String, Expression> expressionCache = new ObjectCache<>();
    
    private final ExpressionParser expressionParser;
    
    private final Map<String, Method> customFunctions;
    
    /**
     * 
     * @param expressionParser EL式のパーサ
     * @param customFunctions EL式中で使用するカスタム関数
     */
    public ExpressionEvaluator(@NonNull ExpressionParser expressionParser, @NonNull Map<String, Method> customFunctions) {
        this.expressionParser = expressionParser;
        this.customFunctions = new ConcurrentHashMap<>(customFunctions);
    }
    
    /**
     * 
     * @param expressionParser EL式のパーサ
     */
    public ExpressionEvaluator(@NonNull ExpressionParser expressionParser) {
        this(expressionParser, Collections.emptyMap());
    }
    
    /**
     * 
     * @param expression 評価する式
     * @param variables 式中で利用する変数
     * @return 評価した結果
     * @throws ParseException 式のパースに失敗した場合
     * @throws EvaluationException 式の評価に失敗した場合
     */
    public Object evaluate(@NonNull final String expression, @NonNull final Map<String, Object> variables) {
        
        Assert.hasLength(expression, "expression should not be empty.");
        
        if(log.isDebugEnabled()) {
            log.debug("Evaluating JEXL expression: {}", expression);
        }
        
        Expression expr = expressionCache.get(expression);
        if(expr == null) {
            expr = expressionParser.parseExpression(expression);
            expressionCache.put(expression, expr);
        }
        
        final StandardEvaluationContext  context = new StandardEvaluationContext();
        
        // 変数の登録
        context.setVariables(variables);
        
        // カスタム関数の登録
        customFunctions.forEach((k, v) -> context.registerFunction(k, v));
        
        return expr.getValue(context);
        
    }
    
    public ExpressionParser getExpressionParser() {
        return expressionParser;
    }
    
}
