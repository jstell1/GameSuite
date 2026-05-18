package gamesuite.core.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;

import java.io.InputStream;
import java.util.Set;

public class JsonSchemaValidator {
    private static final JsonSchema schema;
    private static final JsonSchema gameSchema;
    
    static {
        try {
            InputStream schemaStream = JsonSchemaValidator.class
                .getClassLoader()
                .getResourceAsStream("schema.json");
                JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
                schema = factory.getSchema(schemaStream);

            schemaStream = JsonSchemaValidator.class
                .getClassLoader()
                .getResourceAsStream("GameSchema.json");
             factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
                gameSchema = factory.getSchema(schemaStream);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON schema", e);
        }
    }
    
    public static Set<ValidationMessage> validate(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            return schema.validate(jsonNode);
        } catch (Exception e) {
            throw new RuntimeException("Validation failed", e);
        }
    }
    
    public static boolean isValid(String json) {
        Set<ValidationMessage> errors = validate(json);
        return errors.isEmpty();
    }

    public static boolean isValidGameSchema(String json) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            return gameSchema.validate(jsonNode).isEmpty();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
}