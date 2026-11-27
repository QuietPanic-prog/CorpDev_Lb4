package ru.kafpin.lb3.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kafpin.lb3.dto.ServicemanForm;
import ru.kafpin.lb3.dto.SearchForm;
import ru.kafpin.lb3.model.Address;
import ru.kafpin.lb3.model.Serviceman;
import ru.kafpin.lb3.repository.MilitaryRankRepository;
import ru.kafpin.lb3.repository.ServicemanRepository;

@Service
@Transactional(readOnly = true)
public class ServicemanService {
    private final ServicemanRepository repository;
    private final MilitaryRankRepository ranks;

    public ServicemanService(ServicemanRepository repository, MilitaryRankRepository ranks) {
        this.repository = repository;
        this.ranks = ranks;
    }

    public List<Serviceman> search(SearchForm form) {
        return repository.search(likeValue(form.getLastName()), text(form.getNationality()),
                likeValue(form.getCity()), likeValue(form.getPosition()), form.getRankId());
    }

    @Transactional
    public void create(ServicemanForm form) {
        var rank = ranks.findById(form.getRankId())
                .orElseThrow(() -> new IllegalArgumentException("Выбранное звание не существует"));
        var addressForm = form.getAddress();
        Address address = new Address();
        address.setPostalCode(text(addressForm.getPostalCode()));
        address.setCountry(text(addressForm.getCountry()));
        address.setRegion(text(addressForm.getRegion()));
        address.setDistrict(text(addressForm.getDistrict()));
        address.setCity(text(addressForm.getCity()));
        address.setStreet(text(addressForm.getStreet()));
        address.setHouse(text(addressForm.getHouse()));
        address.setApartment(text(addressForm.getApartment()));

        Serviceman serviceman = new Serviceman();
        serviceman.setLastName(text(form.getLastName()));
        serviceman.setFirstName(text(form.getFirstName()));
        serviceman.setPatronymic(text(form.getPatronymic()));
        serviceman.setNationality(text(form.getNationality()));
        serviceman.setBirthDate(form.getBirthDate());
        serviceman.setPosition(text(form.getPosition()));
        serviceman.setAddress(address);
        serviceman.setRank(rank);
        repository.save(serviceman);
    }

    private String text(String value) {
        return value == null ? "" : value.strip();
    }

    private String likeValue(String value) {
        return text(value).replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}

