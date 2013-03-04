package org.staxer.igniter.db;

import org.staxer.igniter.db.beans.BasicDataSourceXml;
import org.staxer.igniter.db.beans.BasicDataSourcesXml;
import org.staxer.util.StringUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-18 12:25 (Europe/Moscow)
 */
public class JdbcTemplateConfigurator {

    private Map<String, SimpleJdbcTemplate> jdbcTemplateMap = new HashMap<String, SimpleJdbcTemplate>();

    public JdbcTemplateConfigurator(BasicDataSourcesXml dataSourcesXml) {
        for (BasicDataSourceXml dataSourceXml : dataSourcesXml.getDatasource()) {
            BasicDataSource dataSource = new BasicDataSource();
            String name = dataSourceXml.getName();

            String dbDriverClassName = dataSourceXml.getDbDriverClassName();
            checkArgument(dbDriverClassName, "dbDriverClassName", name);
            dataSource.setDriverClassName(dbDriverClassName);

            String dbUrl = dataSourceXml.getDbUrl();
            checkArgument(dbUrl, "dbUrl", name);
            dataSource.setUrl(dbUrl);

            String username = dataSourceXml.getUsername();
            checkArgument(username, "username", name);
            dataSource.setUsername(username);

            String password = dataSourceXml.getPassword();
            checkArgument(password, "password", name);
            dataSource.setPassword(password);

            Integer minIdle = dataSourceXml.getMinIdle();
            if (minIdle == null) {
                minIdle = 3;
            }
            Integer maxActive = dataSourceXml.getMaxActive();
            if (maxActive == null) {
                maxActive = 10;
            }
            dataSource.setMinIdle(Math.min(minIdle, maxActive));
            dataSource.setMinIdle(Math.max(minIdle, maxActive));
            jdbcTemplateMap.put(name, new SimpleJdbcTemplate(dataSource));
        }
    }

    private void checkArgument(String argument, String argumentName, String jdbcTemplateName) {
        if (StringUtils.isEmpty(argument)) {
            throw new IllegalArgumentException(
                    "jdbcTemplate '" + jdbcTemplateName + "': argument '" + argumentName + "' is empty"
            );
        }
    }

    public SimpleJdbcTemplate getJdbcTemplate(String name) {
        return jdbcTemplateMap.get(name);
    }
}
