package ru.kafpin.lb4;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.kafpin.lb4.repository.MilitaryRankRepository;
import ru.kafpin.lb4.repository.ServicemanRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:lb4-rest-tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class RestApiTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ServicemanRepository servicemanRepository;

    @Autowired
    private MilitaryRankRepository militaryRankRepository;

    @BeforeEach
    void clearDatabase() {
        servicemanRepository.deleteAll();
        militaryRankRepository.deleteAll();
    }

    @Test
    void rankCrudCreatesReadsUpdatesAndDeletesTheSameRecord() throws Exception {
        long rankId = createRank("Рядовой");

        mvc.perform(get("/api/ranks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(rankId));
        mvc.perform(get("/api/ranks/{id}", rankId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Рядовой"));
        mvc.perform(put("/api/ranks/{id}", rankId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ефрейтор\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rankId))
                .andExpect(jsonPath("$.name").value("Ефрейтор"));
        mvc.perform(delete("/api/ranks/{id}", rankId))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/ranks/{id}", rankId))
                .andExpect(status().isNotFound());
    }

    @Test
    void rankNamesAreRequiredAndUniqueAndUsedRanksCannotBeDeleted() throws Exception {
        long rankId = createRank("Рядовой");
        long otherRankId = createRank("Ефрейтор");
        mvc.perform(post("/api/ranks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Рядовой\"}"))
                .andExpect(status().isConflict());
        mvc.perform(put("/api/ranks/{id}", otherRankId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Рядовой\"}"))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/ranks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \"}"))
                .andExpect(status().isBadRequest());

        long servicemanId = createServiceman("Рядовой", 25, "Москва");
        mvc.perform(delete("/api/ranks/{id}", rankId))
                .andExpect(status().isConflict());
        mvc.perform(get("/api/servicemen/{id}", servicemanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rank.id").value(rankId));
    }

    @Test
    void absentResourcesReturnNotFoundForReadUpdatePatchAndDelete() throws Exception {
        createRank("Рядовой");
        long missingId = Long.MAX_VALUE;
        mvc.perform(get("/api/ranks/{id}", missingId))
                .andExpect(status().isNotFound());
        mvc.perform(put("/api/ranks/{id}", missingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Сержант\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/ranks/{id}", missingId))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/servicemen/{id}", missingId))
                .andExpect(status().isNotFound());
        mvc.perform(put("/api/servicemen/{id}", missingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(servicemanJson("Рядовой", 25, "Москва")))
                .andExpect(status().isNotFound());
        patchServiceman(missingId, "{\"position\":\"Связист\"}")
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/servicemen/{id}", missingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void servicemanCrudPersistsAllFieldsAndReplacesTheRankWithoutChangingId() throws Exception {
        createRank("Рядовой");
        long newRankId = createRank("Ефрейтор");
        long servicemanId = createServiceman("Рядовой", 25, "Москва");

        mvc.perform(get("/api/servicemen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rank.name").value("Рядовой"))
                .andExpect(jsonPath("$[0].rank.servicemen").doesNotExist());

        String replacement = servicemanJson("Ефрейтор", 30, "Тула")
                .replace("Иванов", "Петров")
                .replace("Иван", "Пётр")
                .replace("Стрелок", "Связист")
                .replace("101000", "300000")
                .replace("Московская область", "Тульская область")
                .replace("Лесная", "Полевая")
                .replace("\"house\": \"10\"", "\"house\": \"20\"")
                .replace("\"apartment\": \"5\"", "\"apartment\": \"7\"");
        mvc.perform(put("/api/servicemen/{id}", servicemanId)
                        .contentType(MediaType.APPLICATION_JSON).content(replacement))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(servicemanId));
        mvc.perform(get("/api/servicemen/{id}", servicemanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Петров"))
                .andExpect(jsonPath("$.firstName").value("Пётр"))
                .andExpect(jsonPath("$.position").value("Связист"))
                .andExpect(jsonPath("$.postalCode").value("300000"))
                .andExpect(jsonPath("$.region").value("Тульская область"))
                .andExpect(jsonPath("$.city").value("Тула"))
                .andExpect(jsonPath("$.street").value("Полевая"))
                .andExpect(jsonPath("$.house").value("20"))
                .andExpect(jsonPath("$.apartment").value("7"))
                .andExpect(jsonPath("$.birthDate").value(LocalDate.now().minusYears(30).toString()))
                .andExpect(jsonPath("$.rank.id").value(newRankId));

        mvc.perform(delete("/api/servicemen/{id}", servicemanId))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/servicemen/{id}", servicemanId))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/servicemen"))
                .andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/api/ranks/{id}", newRankId))
                .andExpect(status().isOk());
    }

    @Test
    void mandatoryFieldsAreValidatedWhileOptionalFieldsCanBeBlank() throws Exception {
        createRank("Рядовой");
        String validJson = servicemanJson("Рядовой", 25, "Москва");
        for (String field : new String[]{"lastName", "firstName", "nationality", "birthDate",
                "position", "postalCode", "country", "region", "city", "street", "house", "rank"}) {
            String invalidJson = replaceField(validJson, field, "null");
            mvc.perform(post("/api/servicemen").contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
        String optionalBlankJson = replaceField(validJson, "patronymic", "\"\"");
        optionalBlankJson = replaceField(optionalBlankJson, "district", "\"\"");
        optionalBlankJson = replaceField(optionalBlankJson, "apartment", "\"\"");
        mvc.perform(post("/api/servicemen").contentType(MediaType.APPLICATION_JSON)
                        .content(optionalBlankJson))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/servicemen"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void futureOrMalformedBirthDatesAndUnknownRanksAreRejected() throws Exception {
        createRank("Рядовой");
        String validJson = servicemanJson("Рядовой", 25, "Москва");
        for (String invalidDate : new String[]{LocalDate.now().plusDays(1).toString(), "не дата"}) {
            mvc.perform(post("/api/servicemen").contentType(MediaType.APPLICATION_JSON)
                            .content(replaceField(validJson, "birthDate", "\"" + invalidDate + "\"")))
                    .andExpect(status().isBadRequest());
        }
        mvc.perform(post("/api/servicemen").contentType(MediaType.APPLICATION_JSON)
                        .content(replaceField(validJson, "rank", "\"Неизвестное звание\"")))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/servicemen"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void statisticsUsesStoredBirthDatesAndAppliesTheRankFilter() throws Exception {
        mvc.perform(get("/api/servicemen/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalServicemen").value(0))
                .andExpect(jsonPath("$.averageAge").value(0))
                .andExpect(jsonPath("$.byRank").isEmpty());
        createRank("Рядовой");
        createRank("Ефрейтор");
        createServiceman("Рядовой", 20, "Москва");
        createServiceman("Рядовой", 30, "Тула");
        createServiceman("Ефрейтор", 40, "Москва");

        mvc.perform(get("/api/servicemen/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalServicemen").value(3))
                .andExpect(jsonPath("$.averageAge").value(30.0))
                .andExpect(jsonPath("$.byRank['Рядовой']").value(2))
                .andExpect(jsonPath("$.byRank['Ефрейтор']").value(1));
        mvc.perform(get("/api/servicemen/statistics").param("rank", "Рядовой"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalServicemen").value(2))
                .andExpect(jsonPath("$.averageAge").value(25.0))
                .andExpect(jsonPath("$.byRank['Рядовой']").value(2))
                .andExpect(jsonPath("$.byRank['Ефрейтор']").doesNotExist());
    }

    @Test
    void partialUpdateChangesOnlySuppliedFieldsAndCanChangeTheRankAndBirthDate() throws Exception {
        createRank("Рядовой");
        createRank("Ефрейтор");
        long servicemanId = createServiceman("Рядовой", 25, "Москва");
        String birthDate = LocalDate.now().minusYears(31).toString();

        patchServiceman(servicemanId, """
                {"position":"Связист","rank":"Ефрейтор","birthDate":"%s"}
                """.formatted(birthDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(servicemanId));
        mvc.perform(get("/api/servicemen/{id}", servicemanId))
                .andExpect(jsonPath("$.position").value("Связист"))
                .andExpect(jsonPath("$.rank.name").value("Ефрейтор"))
                .andExpect(jsonPath("$.birthDate").value(birthDate))
                .andExpect(jsonPath("$.lastName").value("Иванов"))
                .andExpect(jsonPath("$.city").value("Москва"))
                .andExpect(jsonPath("$.postalCode").value("101000"));
    }

    @Test
    void invalidPartialUpdatesAreRejectedWithoutPersistingOtherFields() throws Exception {
        createRank("Рядовой");
        long servicemanId = createServiceman("Рядовой", 25, "Москва");
        String[] invalidPatches = {
                "{\"position\":\"Связист\",\"unknownField\":\"значение\"}",
                "{\"position\":\"Связист\",\"lastName\":null}",
                "{\"position\":\"Связист\",\"firstName\":42}",
                "{\"position\":\"Связист\",\"birthDate\":\"не дата\"}",
                "{\"position\":\"Связист\",\"city\":\" \"}"
        };
        for (String invalidPatch : invalidPatches) {
            patchServiceman(servicemanId, invalidPatch).andExpect(status().isBadRequest());
            mvc.perform(get("/api/servicemen/{id}", servicemanId))
                    .andExpect(jsonPath("$.position").value("Стрелок"))
                    .andExpect(jsonPath("$.lastName").value("Иванов"))
                    .andExpect(jsonPath("$.firstName").value("Иван"))
                    .andExpect(jsonPath("$.city").value("Москва"));
        }
    }

    @Test
    void missingRankDuringPartialUpdateRollsBackEarlierFieldChanges() throws Exception {
        createRank("Рядовой");
        long servicemanId = createServiceman("Рядовой", 25, "Москва");
        patchServiceman(servicemanId, """
                {"position":"Связист","city":"Тула","rank":"Неизвестное звание"}
                """)
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/servicemen/{id}", servicemanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value("Стрелок"))
                .andExpect(jsonPath("$.city").value("Москва"))
                .andExpect(jsonPath("$.rank.name").value("Рядовой"));
    }

    private long createRank(String name) throws Exception {
        String response = mvc.perform(post("/api/ranks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        return JsonPath.parse(response).read("$.id", Long.class);
    }

    private long createServiceman(String rank, int age, String city) throws Exception {
        String response = mvc.perform(post("/api/servicemen").contentType(MediaType.APPLICATION_JSON)
                        .content(servicemanJson(rank, age, city)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        return JsonPath.parse(response).read("$.id", Long.class);
    }

    private ResultActions patchServiceman(long id, String content) throws Exception {
        return mvc.perform(patch("/api/servicemen/{id}", id)
                .contentType(MediaType.APPLICATION_JSON).content(content));
    }

    private String replaceField(String json, String field, String value) {
        return json.replaceFirst("\"" + field + "\"\\s*:\\s*\"[^\"]*\"", "\"" + field + "\":" + value);
    }

    private String servicemanJson(String rank, int age, String city) {
        return """
                {
                  "lastName": "Иванов",
                  "firstName": "Иван",
                  "patronymic": "Иванович",
                  "nationality": "русский",
                  "birthDate": "%s",
                  "position": "Стрелок",
                  "postalCode": "101000",
                  "country": "Россия",
                  "region": "Московская область",
                  "district": "Центральный",
                  "city": "%s",
                  "street": "Лесная",
                  "house": "10",
                  "apartment": "5",
                  "rank": "%s"
                }
                """.formatted(LocalDate.now().minusYears(age), city, rank);
    }
}
