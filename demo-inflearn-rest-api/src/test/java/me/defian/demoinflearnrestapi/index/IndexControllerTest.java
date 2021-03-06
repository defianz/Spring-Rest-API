package me.defian.demoinflearnrestapi.index;


import me.defian.demoinflearnrestapi.common.BaseControllerTest;
import me.defian.demoinflearnrestapi.common.RestDocsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//

public class IndexControllerTest extends BaseControllerTest {

//    @Autowired
//    MockMvc mockMvc;

    @Test
    public void index() throws Exception {

        this.mockMvc.perform(get("/api/")
        ).andDo(print())

        .andExpect(status().isOk())
        .andExpect(jsonPath("_links.events").exists())
        ;
    }
}
