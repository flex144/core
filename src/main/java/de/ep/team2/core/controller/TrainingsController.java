package de.ep.team2.core.controller;

import de.ep.team2.core.dtos.CreatePlanDto;
import org.apache.xpath.operations.Mod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.LinkedList;

@Controller
@RequestMapping("/mods/")
public class TrainingsController {

    @PostMapping("/plans")
    public String trainingsPlanCreate(@ModelAttribute("createDto") CreatePlanDto dto, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("createDto", dto);
        return "redirect:/mods/createplan";
    }
}
