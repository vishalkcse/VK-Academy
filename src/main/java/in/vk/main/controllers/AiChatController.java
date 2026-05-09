package in.vk.main.controllers;

import in.vk.main.services.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class AiChatController 
{
    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private in.vk.main.services.CourseService courseService;

    @Autowired
    private in.vk.main.services.BatchService batchService;

    @PostMapping("/ask")
    public Map<String, String> askAi(@RequestBody Map<String, String> request) 
    {
        String userMessage = request.get("message");
        
        // Securely fetch and format course info
        String courseInfo = courseService.getAllCourseDetails().stream()
                .map(c -> c.getName() + " (Price: Rs. " + c.getDiscountedPrice() + ")")
                .collect(java.util.stream.Collectors.joining(", "));
                
        // Securely fetch and format upcoming batch info
        String batchInfo = batchService.getAllBatches().stream()
                .map(b -> b.getBatchName() + " (Starts: " + b.getStartDate() + ", Price: Rs. " + b.getDiscountedPrice() + ")")
                .collect(java.util.stream.Collectors.joining(", "));
                
        String combinedContext = "Available Courses: [" + courseInfo + "] | Upcoming Live Batches: [" + batchInfo + "]";
                
        String response = aiChatService.getAiResponse(userMessage, combinedContext);
        return Map.of("response", response);
    }
}
