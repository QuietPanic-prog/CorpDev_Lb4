package ru.kafpin.lb4.controller;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.kafpin.lb4.dto.ServicemanDTO;
import ru.kafpin.lb4.model.MilitaryRank;
import ru.kafpin.lb4.model.Serviceman;
import ru.kafpin.lb4.repository.MilitaryRankRepository;
import ru.kafpin.lb4.repository.ServicemanRepository;

@RestController
@RequestMapping("/api/servicemen")
public class ServicemanController {
    private final ServicemanRepository servicemen;
    private final MilitaryRankRepository ranks;

    public ServicemanController(ServicemanRepository servicemen, MilitaryRankRepository ranks) {
        this.servicemen = servicemen;
        this.ranks = ranks;
    }

    @GetMapping
    public List<Serviceman> list() {
        return servicemen.findAll();
    }

    @GetMapping("/{id}")
    public Serviceman get(@PathVariable Long id) {
        return findServiceman(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Serviceman create(@RequestBody ServicemanDTO request) {
        Serviceman serviceman = new Serviceman();
        fillServiceman(serviceman, request);
        return servicemen.save(serviceman);
    }

    @PutMapping("/{id}")
    @Transactional
    public Serviceman update(@PathVariable Long id, @RequestBody ServicemanDTO request) {
        Serviceman serviceman = findServiceman(id);
        fillServiceman(serviceman, request);
        return servicemen.save(serviceman);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {
        findServiceman(id);
        servicemen.deleteById(id);
    }

    // Аналог статистики из методички: агрегируем записи, при необходимости отобранные по званию.
    @GetMapping("/statistics")
    public Map<String, Object> statistics(@RequestParam(required = false) String rank) {
        List<Serviceman> selected = rank == null
                ? servicemen.findAll() : servicemen.findByRankName(rank);
        Map<String, Long> byRank = new LinkedHashMap<>();
        long totalAge = 0;
        LocalDate today = LocalDate.now();
        for (Serviceman serviceman : selected) {
            String name = serviceman.getRank().getName();
            byRank.put(name, byRank.getOrDefault(name, 0L) + 1);
            totalAge += Period.between(serviceman.getBirthDate(), today).getYears();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalServicemen", selected.size());
        result.put("averageAge", selected.isEmpty() ? 0.0 : (double) totalAge / selected.size());
        result.put("byRank", byRank);
        return result;
    }

    // Map используется для PATCH так же, как в примере методички.
    @PatchMapping("/{id}")
    @Transactional
    public Serviceman partialUpdate(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Serviceman serviceman = findServiceman(id);
        ServicemanDTO request = new ServicemanDTO(serviceman);
        for (Map.Entry<String, Object> update : updates.entrySet()) {
            Object rawValue = update.getValue();
            if (rawValue != null && !(rawValue instanceof String)) {
                throw new IllegalArgumentException("Поле " + update.getKey() + " должно содержать строку");
            }
            String value = (String) rawValue;
            switch (update.getKey()) {
                case "lastName" -> request.setLastName(value);
                case "firstName" -> request.setFirstName(value);
                case "patronymic" -> request.setPatronymic(value);
                case "nationality" -> request.setNationality(value);
                case "birthDate" -> request.setBirthDate(parseDate(value));
                case "position" -> request.setPosition(value);
                case "postalCode" -> request.setPostalCode(value);
                case "country" -> request.setCountry(value);
                case "region" -> request.setRegion(value);
                case "district" -> request.setDistrict(value);
                case "city" -> request.setCity(value);
                case "street" -> request.setStreet(value);
                case "house" -> request.setHouse(value);
                case "apartment" -> request.setApartment(value);
                case "rank" -> request.setRank(value);
                default -> throw new IllegalArgumentException("Неизвестное поле: " + update.getKey());
            }
        }
        // До проверки всех данных изменяется только DTO, а не сохранённая сущность.
        fillServiceman(serviceman, request);
        return servicemen.save(serviceman);
    }

    private Serviceman findServiceman(Long id) {
        return servicemen.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Военнослужащий с ID " + id + " не найден"));
    }

    private void fillServiceman(Serviceman serviceman, ServicemanDTO request) {
        requireText(request.getLastName(), "lastName");
        requireText(request.getFirstName(), "firstName");
        requireText(request.getNationality(), "nationality");
        requireText(request.getPosition(), "position");
        requireText(request.getPostalCode(), "postalCode");
        requireText(request.getCountry(), "country");
        requireText(request.getRegion(), "region");
        requireText(request.getCity(), "city");
        requireText(request.getStreet(), "street");
        requireText(request.getHouse(), "house");
        requireText(request.getRank(), "rank");
        if (request.getBirthDate() == null || !request.getBirthDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("birthDate должна быть датой в прошлом в формате YYYY-MM-DD");
        }
        MilitaryRank rank = ranks.findByName(request.getRank().trim())
                .orElseThrow(() -> new EntityNotFoundException("Звание " + request.getRank() + " не найдено"));

        serviceman.setLastName(request.getLastName());
        serviceman.setFirstName(request.getFirstName());
        serviceman.setPatronymic(request.getPatronymic());
        serviceman.setNationality(request.getNationality());
        serviceman.setBirthDate(request.getBirthDate());
        serviceman.setPosition(request.getPosition());
        serviceman.setPostalCode(request.getPostalCode());
        serviceman.setCountry(request.getCountry());
        serviceman.setRegion(request.getRegion());
        serviceman.setDistrict(request.getDistrict());
        serviceman.setCity(request.getCity());
        serviceman.setStreet(request.getStreet());
        serviceman.setHouse(request.getHouse());
        serviceman.setApartment(request.getApartment());
        serviceman.setRank(rank);
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Заполните поле " + field);
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("birthDate должна иметь формат YYYY-MM-DD");
        }
    }
}
