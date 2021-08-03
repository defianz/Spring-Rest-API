package me.defian.demoinflearnrestapi.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {

        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 8, 03, 11, 36))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 4, 12, 00))
                .beginEventDateTime(LocalDateTime.of(2021, 8, 3, 12, 00))
                .endEventDateTime(LocalDateTime.of(2021, 8, 3, 14, 00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        event.setId(10);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
        ;
    }
}