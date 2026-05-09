package in.vk.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import in.vk.main.entities.TickerContent;
import in.vk.main.repositories.TickerRepository;
import java.util.List;

@RestController
public class TickerRestController {

    @Autowired
    private TickerRepository tickerRepository;

    @GetMapping("/api/latest-updates")
    public String getLatestUpdates() {
        List<TickerContent> list = tickerRepository.findAll();
        if(!list.isEmpty()) {
            return list.get(0).getContent();
        }
        return "Welcome to VK Academy - Empowering Careers Through Tech!";
    }
}
