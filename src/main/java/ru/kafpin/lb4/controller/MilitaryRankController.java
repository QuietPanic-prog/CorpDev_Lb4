package ru.kafpin.lb4.controller;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.kafpin.lb4.model.MilitaryRank;
import ru.kafpin.lb4.repository.MilitaryRankRepository;
import ru.kafpin.lb4.repository.ServicemanRepository;

@RestController
@RequestMapping("/api/ranks")
public class MilitaryRankController {
    private final MilitaryRankRepository ranks;
    private final ServicemanRepository servicemen;

    public MilitaryRankController(MilitaryRankRepository ranks, ServicemanRepository servicemen) {
        this.ranks = ranks;
        this.servicemen = servicemen;
    }

    @GetMapping
    public List<MilitaryRank> list() {
        return ranks.findAll();
    }

    @GetMapping("/{id}")
    public MilitaryRank get(@PathVariable Long id) {
        return findRank(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public MilitaryRank create(@RequestBody MilitaryRank request) {
        String name = checkedName(request.getName());
        if (ranks.existsByName(name)) {
            throw new IllegalStateException("Звание с таким названием уже существует");
        }
        MilitaryRank rank = new MilitaryRank();
        rank.setName(name);
        return ranks.save(rank);
    }

    @PutMapping("/{id}")
    @Transactional
    public MilitaryRank update(@PathVariable Long id, @RequestBody MilitaryRank request) {
        MilitaryRank rank = findRank(id);
        String name = checkedName(request.getName());
        if (!name.equals(rank.getName()) && ranks.existsByName(name)) {
            throw new IllegalStateException("Звание с таким названием уже существует");
        }
        rank.setName(name);
        return ranks.save(rank);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {
        findRank(id);
        if (servicemen.existsByRankId(id)) {
            throw new IllegalStateException("Нельзя удалить звание, назначенное военнослужащим");
        }
        ranks.deleteById(id);
    }

    private MilitaryRank findRank(Long id) {
        return ranks.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Звание с ID " + id + " не найдено"));
    }

    private String checkedName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Укажите название звания");
        }
        return name.trim();
    }
}
