package gamesuite.core.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;

import java.io.InputStream;
import java.util.Set;

public class JsonSchemaValidator {
    private static final JsonSchema schema;
    
    static {
        try {
            InputStream schemaStream = JsonSchemaValidator.class
                .getClassLoader()
                .getResourceAsStream("schema.json");
            
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            schema = factory.getSchema(schemaStream);
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
}