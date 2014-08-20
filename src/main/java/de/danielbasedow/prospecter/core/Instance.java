package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.schema.Schema;
import de.danielbasedow.prospecter.core.schema.SchemaBuilder;
import de.danielbasedow.prospecter.core.schema.SchemaBuilderJSON;
import de.danielbasedow.prospecter.core.schema.SchemaConfigurationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

/**
 * Prospecter instance that contains all the schemas
 */
public class Instance {
    private final String homeDirectory;
    private static final Logger LOGGER = LoggerFactory.getLogger(Instance.class);
    private Map<String, Schema> schemas;

    public Instance(String homeDirectory) {
        this.homeDirectory = homeDirectory;
        schemas = new HashMap<String, Schema>();
    }

    public void initialize() throws SchemaConfigurationError {
        File dir = new File(homeDirectory);
        if (!dir.isDirectory() || !dir.canRead()) {
            LOGGER.error("Can't open home directory '" + homeDirectory + "' make sure it is a directory and readable.");
            throw new SchemaConfigurationError("Can't read home directory");
        }

        File[] files = dir.listFiles();
        if (files == null) {
            LOGGER.error("Error reading home directory. Make sure it is not empty.");
            throw new SchemaConfigurationError("No files in home directory");
        }
        for (File file : files) {
            if (file.isDirectory() && file.canRead()) {
                File[] schemaFiles = file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return "schema.json".equals(name);
                    }
                });
                if (schemaFiles.length == 1) {
                    LOGGER.info("Found schema.json in '" + file.getAbsoluteFile() + "'");
                    //directory name is schema name
                    String schemaName = file.getName();
                    SchemaBuilder schemaBuilder = new SchemaBuilderJSON(schemaFiles[0]);
                    schemas.put(schemaName, schemaBuilder.getSchema());
                }
            }
        }
    }

    public void shutDown() {
        for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
            Schema schema = entry.getValue();
            if (schema != null) {
                schema.close();
            }
        }
    }

    public Schema getSchema(String name) {
        return schemas.get(name);
    }
}
