package comtech.util.velocity;

import comtech.util.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.ListTool;
import org.apache.velocity.tools.generic.NumberTool;

import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 23.12.2009
 * Time: 11:00:33
 */
@SuppressWarnings({"deprecation"})
public class VelocityTemplate {

    private VelocityEngine velocityEngine;

    private DateTool dateTool = new DateTool();
    private NumberTool numberTool = new NumberTool();
    @SuppressWarnings({"deprecation"})
    private ListTool listTool = new ListTool();
    private MiscTool miscTool = new MiscTool();

    private StringResourceRepository resources;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private static Log log = LogFactory.getLog(VelocityTemplate.class);

    public VelocityTemplate() {
        try {
            Properties properties = new Properties();

//-------------------------------------------------------------------------------
// E V E N T H A N D L E R P R O P E R T I E S
//-------------------------------------------------------------------------------
            properties.put("eventhandler.include.class", "org.apache.velocity.app.event.implement.IncludeRelativePath");

//-------------------------------------------------------------------------------
// R E S O U R C E L O A D E R P R O P E R T I E S
//-------------------------------------------------------------------------------

//properties.put("resource.loader", "webapp");
//properties.put("webapp.resource.loader.description", "Velocity Web Application Resource Loader");
//properties.put("webapp.resource.loader.class", "org.apache.velocity.tools.view.WebappResourceLoader");

            properties.put("resource.loader", "string");
            properties.put("string.resource.loader.description", "Velocity StringResource loader");
            properties.put("string.resource.loader.class",
                           "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
            properties.put("string.resource.loader.repository.class",
                           "org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl");


//-------------------------------------------------------------------------------
// R U N T I M E L O G
//-------------------------------------------------------------------------------
            properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.Log4JLogChute");
            properties.put("runtime.log.logsystem.log4j.logger", "org.apache.velocity");

//-------------------------------------------------------------------------------
// T E M P L A T E E N C O D I N G
//-------------------------------------------------------------------------------
            properties.put("input.encoding", "UTF-8");
            properties.put("output.encoding", "UTF-8");
            properties.put("default.contentType", "text/html");

            velocityEngine = new VelocityEngine();
            velocityEngine.init(properties);

            resources = StringResourceLoader.getRepository();
            resources.setEncoding("UTF-8");

        } catch (Exception e) {
            log.error("Could not initialize velocity engine", e);
            throw new IllegalStateException("Could not initialize velocity engine", e);
        }
    }

    public String merge(String template, VelocityContext context) throws Exception {
        if (template == null || template.length() == 0) {
            return "";
        }
        if (velocityEngine != null) {
            String templateName = getTemplateName(template);
            StringResource stringResource;
            lock.readLock().lock();
            try {
                stringResource = resources.getStringResource(templateName);
            } finally {
                lock.readLock().unlock();
            }
            if (stringResource == null) {
                lock.writeLock().lock();
                try {
                    resources.putStringResource(templateName, template);
                } finally {
                    lock.writeLock().unlock();
                }
            }
            context.put("dateTool", dateTool);
            context.put("numberTool", numberTool);
            context.put("listTool", listTool);
            context.put("miscTool", miscTool);
            StringWriter writer = new StringWriter();
            velocityEngine.getTemplate(templateName).merge(context, writer);
            return writer.toString();
        } else {
            throw new IllegalStateException("Velocity engine not initialized");
        }
    }

    private String getTemplateName(String template) {
        return StringUtils.hexencode(DigestUtils.md5(template));
    }

}
