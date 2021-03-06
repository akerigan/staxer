package org.staxer.igniter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staxer.util.staxer.StaxerUtils;
import org.staxer.util.staxer.domain.XmlSchema;

import java.io.File;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-17 14:35 (Europe/Moscow)
 */
public class ReadXsdSchema {

    private static Logger log = LoggerFactory.getLogger(ReadXsdSchema.class);

    public static void main(
            String[] args
    ) throws Exception {
        File baseDirectory = new File(".");
        log.info("baseDirectory = " + baseDirectory.getAbsolutePath());
        File xsdFile = new File(baseDirectory, "staxer-sample-common/src/main/resources/log4jConfiguration.xsd");
        System.out.println("xsdFile exists = " + xsdFile.exists());
        XmlSchema xmlSchema = StaxerUtils.readXmlSchema("", xsdFile.toURI(), null, null, "UTF-8");
        if (xmlSchema != null) {
            File sourceDir = new File(baseDirectory, "staxer-sample-common/src/main/java");
            StaxerUtils.createXsdBeans(
                    xmlSchema, sourceDir, "domain.log4jconf", true
            );
        }
    }


}
