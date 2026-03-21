package rs.lazar403.veloxauto.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import rs.lazar403.veloxauto.dto.customer.CustomerCreateRequest;
import rs.lazar403.veloxauto.dto.customer.CustomerResponse;
import rs.lazar403.veloxauto.dto.customer.CustomerUpdateRequest;
import rs.lazar403.veloxauto.enums.CustomerRole;
import rs.lazar403.veloxauto.service.CustomerService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Slice test - only loads the web layer (controller + validation), service is mocked
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private tools.jackson.databind.ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    // [======== CREATE ========]

    @Test
    void createCustomer_shouldReturn201WithBody() throws Exception {
        // arrange: mock service returns a response with id
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setFirstName("User");
        request.setEmail("user@test.com");
        request.setPassword("password123");

        CustomerResponse response = new CustomerResponse();
        response.setId(1L);
        response.setFirstName("User");
        response.setEmail("user@test.com");
        response.setRole(CustomerRole.CUSTOMER);
        response.setActive(true);

        when(customerService.createCustomer(any(CustomerCreateRequest.class))).thenReturn(response);

        // assert: POST should return 201 CREATED with the response body
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("User"))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void createCustomer_withBlankEmail_shouldReturn400() throws Exception {
        // arrange: missing required email triggers @Valid validation before reaching the actual service
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setFirstName("User");
        request.setPassword("password123");
        // email is null

        // assert: 400 Bad Request, service is never called
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCustomer_withBlankPassword_shouldReturn400() throws Exception {
        // arrange: missing required password
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setFirstName("User");
        request.setEmail("User@test.com");
        // password is null

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // [======== GET BY ID ========]

    @Test
    void getCustomerById_shouldReturn200() throws Exception {
        // arrange: service finds the customer
        CustomerResponse response = new CustomerResponse();
        response.setId(1L);
        response.setFirstName("User");

        when(customerService.getCustomerById(1L)).thenReturn(response);

        // assert: GET /api/customers/1 returns 200 with JSON body
        mockMvc.perform(get("/api/customers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("User"));
    }

    // [======== GET ALL ========]

    @Test
    void getAllCustomers_shouldReturn200WithList() throws Exception {
        // arrange: service returns a list of two customers
        CustomerResponse r1 = new CustomerResponse();
        r1.setId(1L);
        CustomerResponse r2 = new CustomerResponse();
        r2.setId(2L);

        when(customerService.getAllCustomers()).thenReturn(List.of(r1, r2));

        // assert: GET /api/customers returns 200 with array of size 2
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    // [======== UPDATE ========]

    @Test
    void updateCustomer_shouldReturn200() throws Exception {
        // arrange: service updates and returns the modified customer
        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setFirstName("Updated");

        CustomerResponse response = new CustomerResponse();
        response.setId(1L);
        response.setFirstName("Updated");

        when(customerService.updateCustomer(eq(1L), any(CustomerUpdateRequest.class))).thenReturn(response);

        // assert: PUT /api/customers/1 returns 200
        mockMvc.perform(put("/api/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    // [======== DEACTIVATE ========]

    @Test
    void deactivateCustomer_shouldReturn204() throws Exception {
        // arrange: service deactivates without returning a body
        doNothing().when(customerService).deactivateCustomer(1L);

        // assert: DELETE /api/customers/1 returns 204 No Content
        mockMvc.perform(delete("/api/customers/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
