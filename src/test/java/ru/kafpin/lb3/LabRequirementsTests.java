package ru.kafpin.lb3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.kafpin.lb3.dto.AddressForm;
import ru.kafpin.lb3.dto.ServicemanForm;
import ru.kafpin.lb3.model.Serviceman;
import ru.kafpin.lb3.repository.MilitaryRankRepository;
import ru.kafpin.lb3.repository.ServicemanRepository;
import ru.kafpin.lb3.service.ServicemanService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LabRequirementsTests {
    @Autowired MockMvc mvc;
    @Autowired ServicemanRepository repository;
    @Autowired MilitaryRankRepository ranks;
    @Autowired ServicemanService service;
    private TestSession adminSession;

    @BeforeEach
    void authenticateAsAdmin() {
        adminSession = authenticatedSession("admin", "USER", "ADMIN");
    }

    @Test
    void userMayViewPagesButCannotUseAdminOperations() throws Exception {
        TestSession userSession = authenticatedSession("user", "USER");
        mvc.perform(getAs("/servicemen", userSession)).andExpect(status().isOk());
        mvc.perform(getAs("/ranks", userSession)).andExpect(status().isOk());
        mvc.perform(getAs("/servicemen/new", userSession)).andExpect(status().isForbidden());
        mvc.perform(postAs("/servicemen", userSession).params(validParams()))
                .andExpect(status().isForbidden());
        mvc.perform(postAs("/ranks", userSession).param("name", "майор"))
                .andExpect(status().isForbidden());
    }

    @Test
    void savesServicemanWithAddressAndExistingRank() throws Exception {
        mvc.perform(post("/servicemen").params(validParams()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/servicemen"));
        Serviceman saved = repository.findAll().getFirst();
        assertThat(saved.getAddress().getId()).isNotNull();
        assertThat(saved.getAddress().getCity()).isEqualTo("Москва");
        assertThat(saved.getRank().getId()).isEqualTo(rankId());
        mvc.perform(get("/servicemen"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Иванов")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("101000")));
    }

    @Test
    void searchesBySurnameAndNationality() throws Exception {
        seed("Иванов", "русский", "Москва", "Командир", rankId());
        seed("Иванов", "татарин", "Москва", "Командир", rankId());
        seed("Петров", "русский", "Москва", "Командир", rankId());
        var result = mvc.perform(get("/servicemen").param("lastName", "иван").param("nationality", "РУССКИЙ"))
                .andExpect(status().isOk()).andReturn();
        assertThat(found(result)).hasSize(1).allMatch(s -> s.getLastName().equals("Иванов") && s.getNationality().equals("русский"));
    }

    @Test
    void searchesByCityAndRank() throws Exception {
        Long otherRank = ranks.findAll().getLast().getId();
        seed("Иванов", "русский", "Москва", "Командир", rankId());
        seed("Петров", "русский", "Казань", "Командир", rankId());
        seed("Сидоров", "русский", "Москва", "Командир", otherRank);
        var result = mvc.perform(get("/servicemen").param("city", "моск").param("rankId", rankId().toString()))
                .andExpect(status().isOk()).andReturn();
        assertThat(found(result)).hasSize(1).allMatch(s -> s.getLastName().equals("Иванов"));
    }

    @Test
    void searchesByPositionAndRank() throws Exception {
        Long otherRank = ranks.findAll().getLast().getId();
        seed("Иванов", "русский", "Москва", "Командир", rankId());
        seed("Петров", "русский", "Москва", "Инженер", rankId());
        seed("Сидоров", "русский", "Москва", "Командир", otherRank);
        var result = mvc.perform(get("/servicemen").param("position", "команд").param("rankId", rankId().toString()))
                .andExpect(status().isOk()).andReturn();
        assertThat(found(result)).hasSize(1).allMatch(s -> s.getLastName().equals("Иванов"));
    }

    @Test
    void rejectsInvalidPersonalDataAndNestedAddress() throws Exception {
        var params = validParams();
        params.set("lastName", "   ");
        params.set("birthDate", LocalDate.now().plusDays(1).toString());
        params.set("address.city", "");
        mvc.perform(post("/servicemen").params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("servicemen/form"))
                .andExpect(model().attributeHasFieldErrors("servicemanForm", "lastName", "birthDate", "address.city"));
        assertThat(repository.count()).isZero();
    }

    @Test
    void rejectsMalformedDateAndMissingRank() throws Exception {
        var params = validParams();
        params.set("birthDate", "not-a-date");
        params.set("rankId", "999999");
        mvc.perform(post("/servicemen").params(params))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("servicemanForm", "birthDate", "rankId"));
        assertThat(repository.count()).isZero();
    }

    @Test
    void validatesSearchParameters() throws Exception {
        mvc.perform(get("/servicemen").param("rankId", "abc"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("search", "rankId"));
        mvc.perform(get("/servicemen").param("rankId", "999999"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("search", "rankId"));
        mvc.perform(get("/servicemen").param("city", "x".repeat(121)))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("search", "city"));
    }

    @Test
    void addsRankAndRejectsBlankAndDuplicateNames() throws Exception {
        long initial = ranks.count();
        mvc.perform(post("/ranks").param("name", "майор"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/ranks").param("name", "МАЙОР"))
                .andExpect(model().attributeHasFieldErrors("rankForm", "name"));
        mvc.perform(post("/ranks").param("name", " "))
                .andExpect(model().attributeHasFieldErrors("rankForm", "name"));
        assertThat(ranks.count()).isEqualTo(initial + 1);
        mvc.perform(get("/ranks")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("майор")));
    }

    @Test
    void emptyFiltersReturnAllRecordsAndWildcardsAreLiteral() throws Exception {
        seed("Иванов", "русский", "Москва", "Командир", rankId());
        var all = mvc.perform(get("/servicemen")).andExpect(status().isOk()).andReturn();
        assertThat(found(all)).hasSize(1);
        var literal = mvc.perform(get("/servicemen").param("lastName", "%")).andReturn();
        assertThat(found(literal)).isEmpty();
        mvc.perform(get("/servicemen/new")).andExpect(status().isOk())
                .andExpect(view().name("servicemen/form"));
        mvc.perform(get("/")).andExpect(redirectedUrl("/servicemen"));
    }

    private Long rankId() { return ranks.findAll().getFirst().getId(); }

    private MockHttpServletRequestBuilder get(String path) {
        return getAs(path, adminSession);
    }

    private MockHttpServletRequestBuilder post(String path) {
        return postAs(path, adminSession);
    }

    private MockHttpServletRequestBuilder getAs(String path, TestSession session) {
        return MockMvcRequestBuilders.get(path).session(session.session());
    }

    private MockHttpServletRequestBuilder postAs(String path, TestSession session) {
        return MockMvcRequestBuilders.post(path).session(session.session())
                .param("_csrf", session.csrfToken());
    }

    private TestSession authenticatedSession(String username, String... roles) {
        UserDetails user = User.withUsername(username).password("test").roles(roles).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpSessionCsrfTokenRepository csrfRepository = new HttpSessionCsrfTokenRepository();
        CsrfToken csrfToken = csrfRepository.generateToken(request);
        csrfRepository.saveToken(csrfToken, request, response);
        new XorCsrfTokenRequestAttributeHandler().handle(request, response, () -> csrfToken);
        CsrfToken formToken = (CsrfToken) request.getAttribute(csrfToken.getParameterName());
        return new TestSession(session, formToken.getToken());
    }

    private record TestSession(MockHttpSession session, String csrfToken) { }

    @SuppressWarnings("unchecked")
    private List<Serviceman> found(org.springframework.test.web.servlet.MvcResult result) {
        return (List<Serviceman>) result.getModelAndView().getModel().get("servicemen");
    }

    private MultiValueMap<String, String> validParams() {
        var p = new LinkedMultiValueMap<String, String>();
        p.add("lastName", "Иванов"); p.add("firstName", "Иван"); p.add("patronymic", "Иванович");
        p.add("nationality", "русский"); p.add("birthDate", "1990-05-14");
        p.add("position", "Командир"); p.add("rankId", rankId().toString());
        p.add("address.postalCode", "101000"); p.add("address.country", "Россия");
        p.add("address.region", "Московская"); p.add("address.district", "Центральный");
        p.add("address.city", "Москва"); p.add("address.street", "Лесная");
        p.add("address.house", "1"); p.add("address.apartment", "2");
        return p;
    }

    private void seed(String lastName, String nationality, String city, String position, Long rankId) {
        var form = new ServicemanForm();
        form.setLastName(lastName); form.setFirstName("Иван"); form.setPatronymic("Иванович");
        form.setNationality(nationality); form.setBirthDate(LocalDate.of(1990, 5, 14));
        form.setPosition(position); form.setRankId(rankId);
        var a = new AddressForm();
        a.setPostalCode("101000"); a.setCountry("Россия"); a.setRegion("Область");
        a.setDistrict("Район"); a.setCity(city); a.setStreet("Лесная"); a.setHouse("1"); a.setApartment("2");
        form.setAddress(a); service.create(form);
    }
}

