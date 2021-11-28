package com.github.mygreen.sqlmapper.core.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * クエリ実行時のテストのサポート
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
public abstract class QueryTestSupport {

    @Autowired
    protected Dialect dialect;

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected PlatformTransactionManager transactionManager;

    @Autowired
    protected ResourceLoader resourceLoader;

    /**
     * 実行するSQLファイルの文字コード
     */
    @Setter(AccessLevel.PROTECTED)
    protected Charset sqlEncoding = StandardCharsets.UTF_8;

    /**
     * 実行するSQLファイルのルートパス
     */
    @Setter(AccessLevel.PROTECTED)
    protected String sqlRootPath = "script";

    /**
     * データのリセットを行います。
     * <ul>
     *  <li>テーブルデータの削除</li>
     *  <li>ID、シーケンスリセット</li>
     * </ul>
     */
    protected void resetData() {
        log.info("rest db data");
        executeSqlFileAndCommit("reset_data.sql");
    }

    /**
     * 任意のSQLファイルを実行し、コミットします。
     * <p>SQLファイルは、{@link #sqlRootPath}/{@link Dialect#getName()} 以下のパスから読み込みます。
     * @param paths 実行するSQLのパス。SQLの格納先は省略して指定します。
     */
    protected void executeSqlFileAndCommit(final String... paths) {

        List<String> sqls = List.of(paths).stream()
                .map(p -> String.join("/", sqlRootPath, dialect.getName(), p))
                .map(p -> loadResource(p, sqlEncoding))
                .collect(Collectors.toList());

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        txNew().executeWithoutResult(action -> {
            sqls.forEach(s -> jdbcTemplate.execute(s));
        });

    }

    /**
     * リソースを文字列として読み込みます。
     * @param path リソースパス
     * @param charset リソースの文字コード
     * @return リソースの内容
     */
    protected String loadResource(final String path, final Charset charset) {

        Resource resource = resourceLoader.getResource(path);

        try(InputStream in = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset))) {

            StringBuilder out = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                if(out.length() > 0) {
                    out.append("\r\n");
                }
                out.append(line);
            }
            return out.toString();

        } catch(IOException e) {
            throw new UncheckedIOException("fail load sql file : " + path, e);
        }

    }

    /**
     * REQUIRED_NEWのトランザクションテンプレートを作成します。
     */
    protected TransactionTemplate txNew() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return template;
    }



}
