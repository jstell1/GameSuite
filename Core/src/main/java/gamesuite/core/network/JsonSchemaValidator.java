package gamesuite.core.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;

import java.io.InputStream;
import java.util.Set;


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
   
    }
    
    public static Set<ValidationMessage> validate(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        return schema.validate(jsonNode);
    }
    
    public static boolean isValid(String json) throws JsonProcessingException {

            Set<ValidationMessage> errors = validate(json);
            if(!errors.isEmpty()) {
                for(ValidationMessage v : errors) {
                    System.out.println(v.toString());
                }
            }
            return errors.isEmpty();
     
    }
 
    public static boolean isValidGameSchema(JsonNode json) throws JsonProcessingException {


            return gameSchema.validate(json).isEmpty();
     
    }
        
}