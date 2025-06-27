package uz.pdp.backend.olxapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.payload.ExistedImageDTO;
import uz.pdp.backend.olxapp.payload.ProductNewImageDTO;
import uz.pdp.backend.olxapp.payload.ProductUpdateDTO;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API = "/api/close/v1/products/{id}";

    @BeforeEach
    void setUpSecurityContext() {
        // 🔐 Test foydalanuvchini qo‘shish
        User user = new User();
        user.setId(1L); // bazadagi mahsulot yaratuvchisining ID'si bo‘lishi kerak
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
    }

    @Test
    void updateProduct_shouldReturnAccepted() throws Exception {
        MockMultipartFile newImageFile = new MockMultipartFile(
                "productNewImages[0].file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "rasm mazmuni".getBytes()
        );

        mockMvc.perform(multipart("/api/close/v1/products/{id}", 1L)
                        .file(newImageFile)
                        .param("title", "Yangilangan nom")
                        .param("description", "Yangilangan tavsif new")
                        .param("price", "120000")
                        .param("active", "true")
                        .param("existedImages[0].imageId", "24")
                        .param("existedImages[0].main", "false")
                        .param("productNewImages[0].main", "true")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isAccepted());
    }


}
