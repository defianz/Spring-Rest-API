package me.defian.demoinflearnrestapi.events;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("Inflearn Spring REST API")
                .description( "REST API devleopment with Srping")
                .build();
        assertTrue(event != null);
    }

    @Test
    public void javaBean(){
        // Given
        Event event = new Event();
        String name = "Event";
        String description = "pring";

        // When
        event.setName(name);
        event.setDescription("S" + description);

        // Then
        assertTrue(event.getName().equals(name));
        assertTrue(event.getDescription().equals("S" + description));
    }

}