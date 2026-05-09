package in.vk.main.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

    @GetMapping("/about")
    public String aboutPage(HttpSession session, Model model) {
        model.addAttribute("sessionUser", session.getAttribute("sessionUser"));
        return "about";
    }

    @GetMapping("/contact")
    public String contactPage(HttpSession session, Model model) {
        model.addAttribute("sessionUser", session.getAttribute("sessionUser"));
        return "contact";
    }

    @GetMapping("/batches")
    public String batchesPage(HttpSession session, Model model) {
        model.addAttribute("sessionUser", session.getAttribute("sessionUser"));
        return "batches";
    }
}
