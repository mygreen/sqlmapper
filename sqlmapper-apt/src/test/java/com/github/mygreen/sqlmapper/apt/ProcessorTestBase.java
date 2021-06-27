package com.github.mygreen.sqlmapper.apt;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;


/**
 * {@link AbstractProcessor}のテスト。
 * <p>QueryDSLの<a href="https://github.com/querydsl/querydsl/blob/master/querydsl-apt/src/test/java/com/querydsl/apt/AbstractProcessorTest.java">AbstractProcessorTest</a>
 *  を参考にしています。
 * </p>
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ProcessorTestBase {

    private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    private List<String> getClassPath() {
        List<String> list = new ArrayList<>();

        // アノテーションが定義されている場所にクラスパスを通す
//        list.add(new File("../sqlmapper-core/target/classes").getAbsoluteFile().toString());


        return list;
    }

    /**
     * 指定したアノテーションプロセッサーを実行します。
     *
     * @param processorClass アノテーションプロセッサ
     * @param classes 処理対象のクラス名称。
     * @param target 生成したファイルの保存ディレクトリ
     * @throws IOException ファイル生成に失敗した場合
     */
    protected void process(Class<? extends AbstractProcessor> processorClass, List<String> classes, String target) throws IOException {
        File out = new File("target/" + target);
        FileUtils.deleteQuietly(out);
        if (!out.mkdirs()) {
            fail("Creation of " + out.getPath() + " failed");
        }
        compile(processorClass, classes, target);
    }

    protected void compile(Class<? extends AbstractProcessor> processorClass, List<String> classes, String target) throws IOException {
        List<String> options = new ArrayList<String>(classes.size() + 3);
        options.add("-s");
        options.add("target/" + target);

        // 自身のAnnotationProcessorのみ実行する。
        options.add("-proc:only");
        options.add("-processor");
        options.add(processorClass.getName());

        options.add("-sourcepath");
        options.add("src/test/java");

        List<String> classpathList = getClassPath();
        if(!classpathList.isEmpty()) {
            options.add("-classpath");
            options.add(String.join(File.pathSeparator, classpathList));
        }

        options.addAll(getAPTOptions());
        options.addAll(classes);

        ByteArrayOutputStream out = getStdOut();
        ByteArrayOutputStream err = getStdErr();
        int compilationResult = compiler.run(null, out, err, options.toArray(new String[options.size()]));

//        Processor.elementCache.clear();
        if (compilationResult != 0) {
            System.err.println(compiler.getClass().getName());
            fail("Compilation Failed:\n " + new String(err.toByteArray(), "UTF-8"));
        }
    }

    protected ByteArrayOutputStream getStdOut() {
        return new ByteArrayOutputStream();
    }

    protected ByteArrayOutputStream getStdErr() {
        return new ByteArrayOutputStream();
    }

    protected Collection<String> getAPTOptions() {
        return Collections.emptyList();
    }
}
