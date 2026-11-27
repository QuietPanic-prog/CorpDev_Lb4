package ru.kafpin.lb3.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.kafpin.lb3.dto.RankForm;
import ru.kafpin.lb3.model.MilitaryRank;
import ru.kafpin.lb3.repository.MilitaryRankRepository;

@Controller
@RequestMapping("/ranks")
public class RankController {
    private final MilitaryRankRepository repository;

    public RankController(MilitaryRankRepository repository) {
        this.repository = repository;
    }

    @InitBinder("rankForm")
    void bind(WebDataBinder binder) {
        binder.setAllowedFields("name");
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rankForm", new RankForm());
        model.addAttribute("ranks", repository.findAll());
        return "ranks";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("rankForm") RankForm form,
            BindingResult result, Model model) {
        if (!result.hasErrors() && repository.existsByNameIgnoreCase(form.getName().strip())) {
            result.rejectValue("name", "duplicate", "Такое звание уже существует");
        }
        if (result.hasErrors()) {
            model.addAttribute("ranks", repository.findAll());
            return "ranks";
        }
        MilitaryRank rank = new MilitaryRank();
        rank.setName(form.getName().strip());
        repository.save(rank);
        return "redirect:/ranks";
    }
}

