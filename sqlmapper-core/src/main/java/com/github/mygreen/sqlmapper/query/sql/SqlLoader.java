package com.github.mygreen.sqlmapper.query.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.sqlmapper.dialect.Dialect;
import com.github.mygreen.sqlmapper.sql.Node;
import com.github.mygreen.sqlmapper.sql.SqlParser;
import com.github.mygreen.sqlmapper.sql.SqlParserImpl;
import com.github.mygreen.sqlmapper.sql.TwoWaySQLException;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * SQLテンプレートを管理するクラス。
 * <p>SQLの解析結果をキャッシュしたりします。</p>
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SqlLoader {

    /**
     * SQLテンプレートをパースした結果をキャッシュするかどうか。
     */
    @Value("${sqlmapper.sqlTemplate.cacheMode}")
    @Getter
    @Setter
    private boolean cacheMode;

    /**
     * SQLテンプレートファイルの文字コード名
     */
    @Value("${sqlmapper.sqlTemplate.encoding}")
    @Getter
    @Setter
    private String encoding;

    /**
     * テンプレートファイルなどのリソースをロードする処理
     */
    @Getter
    @Setter
    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 現在のDBの方言。
     * SQLファイルの読み込む際にDBの名称付きのファイルを優先して読み込むために使用します。
     */
    @Getter
    @Setter
    @Autowired
    private Dialect dialect;

    @Getter
    @Setter
    @Autowired
    private MessageFormatter messageBuilder;

    /**
     * SQLのキャッシュ結果
     */
    private Map<String, Node> nodeCache = new ConcurrentHashMap<>();

    /**
     * SQLを読み込みパースした結果を取得します。
     * @param sqlText SQLの文字列
     * @throws TwoWaySQLException SQLファイルの読み込みやパースに失敗した場合にスローされます。
     */
    public Node loadSqlTextAsNode(@NonNull final String sqlText) {

        // ハッシュコードとパスは重複することはないので、ハッシュコードをキャッシュのキーにする。
        final String hashCode = String.valueOf(sqlText.hashCode());

        if(cacheMode && nodeCache.containsKey(hashCode)) {
            return nodeCache.get(hashCode);
        }

        SqlParser parser = createSqlParser(sqlText);
        Node node = parser.parse();

        if(cacheMode) {
            nodeCache.put(hashCode, node);
        }

        return node;

    }

    /**
     * SQLファイルを読み込みパースした結果を取得します。
     *
     * @param path SQLファイルのパス
     * @return SQLファイルをパースした結果
     * @throws TwoWaySQLException SQLファイルの読み込みやパースに失敗した場合にスローされます。
     */
    public Node loadSqlFileAsNode(@NonNull final String path) {

        if(cacheMode && nodeCache.containsKey(path)) {
            return nodeCache.get(path);
        }

        final String sqlText = loadSqlAsText(path);

        SqlParser parser = createSqlParser(sqlText);
        Node node = parser.parse();

        if(cacheMode) {
            nodeCache.put(path, node);
        }

        return node;

    }

    /**
     * {@link SqlParser} のインスタンスを作成します。
     * @param sql パース対象のSQL
     * @return {@link SqlParser} のインスタンス
     */
    protected SqlParser createSqlParser(final String sql) {

        final ExpressionParser expressionParser = new SpelExpressionParser();
        final SqlParser sqlParser = new SqlParserImpl(sql, expressionParser);

        return sqlParser;
    }

    /**
     * SQLファイルを文字列として読み込む。
     * @param path パス
     * @return SQLファイルの内容
     */
    protected String loadSqlAsText(final String path) {

        // 方言付きパスの読み込み
        String dialectPath = convertSqlPathWithDialect(path);
        try {
            Resource resource = resourceLoader.getResource(dialectPath);
            if(resource.isReadable()) {
                return readStream(resource.getInputStream());
            }
        } catch(IOException e) {
            // 読み込めない場合
            throw new TwoWaySQLException(messageBuilder.create("sqlTemplate.failLoad")
                    .param("path", dialectPath)
                    .format());

        }

        // 元々のパスでの読み込み
        try {
            Resource resource = resourceLoader.getResource(path);
            if(resource.isReadable()) {
                return readStream(resource.getInputStream());
            }

        } catch(IOException e) {
            // 読み込めない場合
            throw new TwoWaySQLException(messageBuilder.create("sqlTemplate.failLoad")
                    .param("path", path)
                    .format());

        }

        // 読み込み対象のファイルが見つからない場合
        throw new TwoWaySQLException(messageBuilder.create("sqlTemplate.notReadable")
                .param("path", path)
                .format());

    }

    /**
     * SQLのパスを方言名付きのSQLに変換する。
     * @param path 変換対象のパス
     * @return 変換したパス
     */
    private String convertSqlPathWithDialect(String path) {

        final StringBuilder sb = new StringBuilder(path.length());

        final String extension = StringUtils.getFilenameExtension(path);

        // 拡張子を除いたファイル名に方言名を付ける
        sb.append(StringUtils.stripFilenameExtension(path))
            .append("-").append(dialect.getName());

        // 拡張子がある場合は戻す。
        if(!StringUtils.isEmpty(extension)) {
            sb.append(".").append(extension);
        }

        return sb.toString();

    }

    /**
     * リソースをテキストとして読み込む
     * @param in リソース
     * @return 読み込んだテキスト
     */
    private String readStream(final InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(in; out) {
            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while((length = in.read(buf)) != -1){
                out.write(buf, 0, length);
            }

            return new String(out.toByteArray(), encoding);

        }
    }


}
