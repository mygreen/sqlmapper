package com.github.mygreen.sqlmapper.apt;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class EntityMetaModelProcessorTest extends ProcessorTestBase {

    private static final String PACKAGE_PATH = "src/test/java/com/github/mygreen/sqlmapper/apt/domain/";

    @Test
    public void testProcess() throws IOException {

        File file = new File(PACKAGE_PATH, "Customer.java");
        process(EntityMetamodelProcessor.class, Collections.singletonList(file.getPath()), "generated-test-junit");

    }
}
