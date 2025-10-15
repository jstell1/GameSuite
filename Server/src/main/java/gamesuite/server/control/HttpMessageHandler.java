package gamesuite.server.control;

import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import gamesuite.core.network.JsonSchemaValidator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.util.StreamUtils;



@RestController
public class HttpMessageHandler {
    private ServerGameRepo gmRepo;
    private WebSocketMessageHandler handler;
    private InputStream schemaStream;
    private JsonNode schemaRoot;

    public HttpMessageHandler(ServerGameRepo gmRepo, WebSocketMessageHandler handler) {
        this.gmRepo = gmRepo;
        this.handler = handler;
        this.schemaStream = JsonSchemaValidator.class.getClassLoader().getResourceAsStream("schema.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.schemaRoot = mapper.readTree(schemaStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/")
    public String home() throws IOException {
        ClassPathResource htmlFile = new ClassPathResource("static/index.html");
        return StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);
    }

    @GetMapping("/schema")
    public ResponseEntity<JsonNode> getSchema() {
        ObjectMapper mapper = new ObjectMapper();
        String str = null;
        try {
            
            str = mapper.writeValueAsString(this.schemaRoot);
        } catch (Exception e) {}
        return new ResponseEntity<>(this.schemaRoot, HttpStatus.OK);
    }
}
