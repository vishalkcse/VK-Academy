package in.vk.main.controllers;

import in.vk.main.entities.Resource;
import in.vk.main.repositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    @PostConstruct
    public void clearOldResources() {
        try {
            resourceRepository.deleteAll();
        } catch (Exception e) {
            // Silently handle if table doesn't exist yet
        }
    }

    @GetMapping("/free-resources")
    public String getFreeResources(HttpSession session, Model model) {
        // Manually adding the sessionUser to the model so the header recognizes the logged-in state
        model.addAttribute("sessionUser", session.getAttribute("sessionUser"));
        
        List<Resource> resources = resourceRepository.findAll();
        model.addAttribute("resources", resources);
        return "free-resources";
    }
}
