package ru.kafpin.lb3.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.kafpin.lb3.dto.SearchForm;
import ru.kafpin.lb3.dto.ServicemanForm;
import ru.kafpin.lb3.repository.MilitaryRankRepository;
import ru.kafpin.lb3.service.ServicemanService;

@Controller
public class ServicemanController {
    private final ServicemanService service;
    private final MilitaryRankRepository ranks;

    public ServicemanController(ServicemanService service, MilitaryRankRepository ranks) {
        this.service = service;
        this.ranks = ranks;
    }

    @InitBinder("servicemanForm")
    void bindServiceman(WebDataBinder binder) {
        binder.setAllowedFields("lastName", "firstName", "patronymic", "nationality",
                "birthDate", "position", "rankId", "address.postalCode", "address.country",
                "address.region", "address.district", "address.city", "address.street",
                "address.house", "address.apartment");
    }

    @InitBinder("search")
    void bindSearch(WebDataBinder binder) {
        binder.setAllowedFields("lastName", "nationality", "city", "position", "rankId");
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/servicemen";
    }

    @GetMapping("/servicemen")
    public String list(@Valid @ModelAttribute("search") SearchForm search,
            BindingResult result, Model model) {
        if (!result.hasFieldErrors("rankId") && search.getRankId() != null
                && !ranks.existsById(search.getRankId())) {
            result.rejectValue("rankId", "missing", "Выбранное звание не существует");
        }
        model.addAttribute("ranks", ranks.findAll());
        model.addAttribute("servicemen", result.hasErrors() ? List.of() : service.search(search));
        return "servicemen/list";
    }

    @GetMapping("/servicemen/new")
    public String form(Model model) {
        model.addAttribute("servicemanForm", new ServicemanForm());
        model.addAttribute("ranks", ranks.findAll());
        return "servicemen/form";
    }

    @PostMapping("/servicemen")
    public String create(@Valid @ModelAttribute("servicemanForm") ServicemanForm form,
            BindingResult result, Model model) {
        if (!result.hasFieldErrors("rankId") && form.getRankId() != null
                && !ranks.existsById(form.getRankId())) {
            result.rejectValue("rankId", "missing", "Выбранное звание не существует");
        }
        if (result.hasErrors()) {
            model.addAttribute("ranks", ranks.findAll());
            return "servicemen/form";
        }
        service.create(form);
        return "redirect:/servicemen";
    }
}

