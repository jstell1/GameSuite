package gamesuite.core.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.imageio.IIOException;

public class JsonSchemaValidator {
    private static final JsonSchema schema;
    private static final JsonSchema gameSchema;
    
    static {
        //try {
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
            
        //} catch (Exception e) {
          //  throw new IllegalStateException("Failed to load JSON schema", e);
        //}
    }
    
    public static Set<ValidationMessage> validate(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        return schema.validate(jsonNode);
    }
    
    public static boolean isValid(String json) throws JsonProcessingException {

        //try {
            Set<ValidationMessage> errors = validate(json);
            if(!errors.isEmpty()) {
                for(ValidationMessage v : errors) {
                    System.out.println(v.toString());
                }
            }
            return errors.isEmpty();
        //} catch (IOException e) {
            // TODO: handle exception
            //throw new IOException("validate function failed", e);
        //}
        //return false;
    }
 
    public static boolean isValidGameSchema(JsonNode json) throws JsonProcessingException {

        //try {
            //ObjectMapper mapper = new ObjectMapper();
            //JsonNode jsonNode = mapper.readTree(json);
            return gameSchema.validate(json).isEmpty();
        //} catch (JsonProcessingException e) {
            // TODO: handle exception
          //  throw new IOException("validate function failed", e);
        //} 
        //eturn false;
    }
        
}