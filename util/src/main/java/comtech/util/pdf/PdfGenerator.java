package comtech.util.pdf;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class PdfGenerator {

    private FopFactory fopFactory;
    private TransformerFactory transformerFactory;
    private URIResolver classPathUriResolver;

    private static Log log = LogFactory.getLog(PdfGenerator.class);

    //Initialize global variables
    public PdfGenerator() throws IOException, SAXException {
        classPathUriResolver = new ClassPathUriResolver(PdfGenerator.class);
        transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setURIResolver(this.classPathUriResolver);
        fopFactory = FopFactory.newInstance();
        fopFactory.setURIResolver(classPathUriResolver);
        InputStream inputStream = PdfGenerator.class.getResourceAsStream("/fop/fop.xconf");
        if (inputStream != null) {
            File tempFile = File.createTempFile("fop-", ".xconf");
            tempFile.deleteOnExit();
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
            outputStream.close();
            fopFactory.setUserConfig(tempFile);
        } else {
            throw new IllegalStateException("Cant find resource \"/fop/fop.xconf\"");
        }
    }

    public byte[] createPdfWithClassPathXmlXsl(
            String xmlClassPath, String xslClassPath
    ) {
        try {
            Source xmlSource = classPathUriResolver.resolve(xmlClassPath, null);
            Source xsltSource = classPathUriResolver.resolve(xslClassPath, null);
            Transformer fopXmlTransformer = transformerFactory.newTransformer(xsltSource);
            fopXmlTransformer.setURIResolver(classPathUriResolver);
            return createPdf(xmlSource, fopXmlTransformer);
        } catch (Exception e) {
            log.warn("got exception while creating pdf: ", e);
            return null;
        }
    }

    public byte[] createPdfWithClassPathXsl(
            String stringXml, String xslClassPath
    ) {
        try {
            Source xmlSource = new StreamSource(new StringReader(stringXml));
            Source xsltSource = classPathUriResolver.resolve(xslClassPath, null);
            Transformer fopXmlTransformer = transformerFactory.newTransformer(xsltSource);
            fopXmlTransformer.setURIResolver(classPathUriResolver);
            return createPdf(xmlSource, fopXmlTransformer);
        } catch (Exception e) {
            log.warn("got exception while creating pdf: ", e);
            return null;
        }
    }

    public byte[] createPdfWithStringXsl(
            String stringXml, String stringXsl
    ) {
        try {
            Source xmlSource = new StreamSource(new StringReader(stringXml));
            Source xsltSource = new StreamSource(new StringReader(stringXsl));
            Transformer fopXmlTransformer = transformerFactory.newTransformer(xsltSource);
            return createPdf(xmlSource, fopXmlTransformer);
        } catch (Exception e) {
            log.warn("got exception while creating pdf: ", e);
            return null;
        }
    }

    private byte[] createPdf(Source xmlSource, Transformer fopXmlTransformer) {
        try {
            //Setup a buffer to obtain the content length
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            //Setup FOP
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, result);
            StringWriter fopXmlWriter = new StringWriter();
            //Start the transformation and rendering process
            fopXmlTransformer.transform(xmlSource, new StreamResult(fopXmlWriter));
            String fopXml = fopXmlWriter.toString();
            log.info("fopXml=" + fopXml);
            Source fopXmlSource = new StreamSource(new StringReader(fopXml));
            //Make sure the XSL transformation's result is piped through to FOP
            Transformer pdfTransformer = transformerFactory.newTransformer();
            pdfTransformer.transform(
                    fopXmlSource, new SAXResult(fop.getDefaultHandler())
            );
            return result.toByteArray();
        } catch (Exception e) {
            log.warn("got exception while creating pdf: ", e);
            return null;
        }
    }
}
