package com.github.mygreen.sqlmapper.apt;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

public class EntityMetaModelProcessorTest extends ProcessorTestBase {

    private static final String PACKAGE_PATH = "src/test/java/com/github/mygreen/sqlmapper/apt/domain/";

    @Test
    public void testProcess_entity() throws IOException {

        File file = new File(PACKAGE_PATH, "Customer.java");
        process(EntityMetamodelProcessor.class, List.of(file.getPath()), "generated-test-junit");

    }

    @Test
    public void testProcess_mappedSuperclass() throws IOException {

        File file = new File(PACKAGE_PATH, "EntityBase.java");
        process(EntityMetamodelProcessor.class, List.of(file.getPath()), "generated-test-junit");

    }

    @Test
    public void testProcess_ineritanceEntity() throws IOException {

        File file = new File(PACKAGE_PATH, "Order.java");
        process(EntityMetamodelProcessor.class, List.of(file.getPath()), "generated-test-junit");

    }

    @Test
    public void testProcess_embeddedEntity() throws IOException {

        File file1 = new File(PACKAGE_PATH, "Detail.java");
        File file2 = new File(PACKAGE_PATH, "DetailPK.java");
        process(EntityMetamodelProcessor.class, List.of(file1.getPath(), file2.getPath()), "generated-test-junit");

    }

    @Test
    public void testProcess_embeddedEntity2() throws IOException {

        // 内部クラス
        File file = new File(PACKAGE_PATH, "Detail2.java");
        process(EntityMetamodelProcessor.class, List.of(file.getPath()), "generated-test-junit");
    }
}
