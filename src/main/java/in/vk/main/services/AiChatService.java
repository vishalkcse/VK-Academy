package in.vk.main.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiChatService 
{
    @Value("${gemini.api.key}")
    private String apiKey;

    // Confirmed working high-performance model for this specific API account
    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=";

    public String getAiResponse(String userPrompt, String courseInfo) 
    {
        try 
        {
            /* 
             * We use RestTemplate to communicate with the Gemini API. 
             * A new instance is used here for request isolation.
             */
            RestTemplate restTemplate = new RestTemplate();
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contentMap = new HashMap<>();
            Map<String, String> partMap = new HashMap<>();
            
            /* 
             * The prompt is engineered to give the AI a specific personality (Elite Assistant) 
             * and provide it with real-time course knowledge fetched from the database.
             */
            String prompt = "Role: VK Academy Expert Assistant. Context: You are an elite assistant for VK Academy. Knowledge Base: " + courseInfo + ". Guidelines: Be professional, concise, and helpful. Answer questions about both fixed courses and upcoming live batches. User Question: " + userPrompt;
            partMap.put("text", prompt);
            
            contentMap.put("parts", List.of(partMap));
            requestBody.put("contents", List.of(contentMap));

            String fullUrl = API_URL + apiKey;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(fullUrl, entity, String.class);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseEntity.getBody());
            
            JsonNode textNode = root.path("candidates").get(0).path("content").path("parts").get(0).path("text");
            if (!textNode.isMissingNode()) {
                return textNode.asText();
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Elite AI Error: " + e.getMessage());
        }
        
        return "Hello! I'm your VK Academy assistant. Our AI is currently optimizing. Please try your question again in a moment!";
    }
}
