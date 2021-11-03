package com.example.redis.controller;

import java.time.Duration;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.example.redis.services.ImportantService;
import com.example.redis.utilities.FargateMetaData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SpringSessionController {

	private ImportantService service;

	@GetMapping("/")
	public String home(Model model, HttpSession session) {
		Integer views = (Integer) session.getAttribute("VIEWS");
		if (views == null) views = 0;

		session.setAttribute("VIEWS", ++views);

		model.addAttribute("sessionId", session.getId());
		model.addAttribute("views", views);

		FargateMetaData fargateMetaData = new FargateMetaData();

		model.addAttribute("taskArn", fargateMetaData.getTaskArn());
		model.addAttribute("image", fargateMetaData.getImage());
		model.addAttribute("fargateMetaData", fargateMetaData.toString());

		return "index";
	}

	@GetMapping("/cacheable")
	public String cacheable(Model model) {
		Instant start = Instant.now();
		service.longExecutingMethod();
		Instant finish = Instant.now();

		model.addAttribute("duration", Duration.between(start, finish).toMillis());

		return "cacheable";
	}

	@PostMapping("/destroy")
	public String destroySession(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/";
	}

	@PostMapping("/evict")
	public String evictCache() {
		service.evictCache();
		return "redirect:/";
	}

	@Autowired
    public SpringSessionController(ImportantService service) {
        this.service = service;
    }
}