package com.rmit.sept.monday15302.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmit.sept.monday15302.model.*;
import com.rmit.sept.monday15302.services.*;
import com.rmit.sept.monday15302.utils.Response.SessionReturn;
import com.rmit.sept.monday15302.utils.Utility;
import com.rmit.sept.monday15302.web.BookingController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService service;

    @MockBean
    private WorkerDetailsService workerDetailsService;

    @MockBean
    private AdminDetailsService adminDetailsService;

    @MockBean
    private MapValidationErrorService mapValidationErrorService;

    @MockBean
    private WorkingHoursService workingHoursService;

    @MockBean
    private SessionService sessionService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void givenPastBookingsForCustomer_whenGetPastBookingsForCustomer_thenReturnJsonArray()
            throws Exception {

        String customerId = "c1";

        Booking booking1 = new Booking();
        booking1.setStatus(BookingStatus.PAST_BOOKING);

        Booking booking2 = new Booking();
        booking2.setStatus(BookingStatus.CANCELLED_BOOKING);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        given(service.getAllPastBookingsByCustomerId(customerId)).willReturn(bookings);

        mvc.perform(get("/historybookings/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.PAST_BOOKING.toString())))
                .andExpect(jsonPath("$[1].status", is(BookingStatus.CANCELLED_BOOKING.toString())));
    }

    @Test
    public void givenNewBookingsForCustomer_whenGetNewBookingsForCustomer_thenReturnJsonArray()
            throws Exception {

        String customerId = "c1";

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.NEW_BOOKING);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        given(service.getAllNewBookingsByCustomerId(customerId)).willReturn(bookings);

        mvc.perform(get("/newbookings/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.NEW_BOOKING.toString())));
    }

    @Test
    public void givenWorkersWithService_whenGetWorkersByService_thenReturnJsonArray()
            throws Exception {
        String workerId1 = "w1";
        String workerId2 = "w2";
        String adminId = "a1";
        String service = "Haircut";

        WorkerDetails worker1 = new WorkerDetails();
        worker1.setId(workerId1);

        WorkerDetails worker2 = new WorkerDetails();
        worker2.setId(workerId2);

        List<String> adminList = Arrays.asList(adminId);

        List<WorkerDetails> workers = new ArrayList<>();
        workers.add(worker1);
        workers.add(worker2);

        given(adminDetailsService.getAdminIdByService(service)).willReturn(adminList);
        given(workerDetailsService.getWorkerForAdmin(adminList)).willReturn(workers);

        mvc.perform(get("/makebooking/byservice/{service}", service)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(workerId1)))
                .andExpect(jsonPath("$[1].id", is(workerId2)));
    }

    @Test
    public void fetchAvailableSessionsByWorkerAndService() throws Exception {
        String service = "Haircut";
        String workerId = "w1";
        SessionReturn session1 = new SessionReturn("2020-09-12",
                "08:00:00", "09:00:00");
        List<SessionReturn> sessions = Arrays.asList(session1);
        given(sessionService.getAvailableSession(workerId, service)).willReturn(sessions);
        mvc.perform(get("/makebooking/sessions/{workerId}/{service}", workerId, service)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date", is(Utility.getDateAsString(session1.getDate()))))
                .andExpect(jsonPath("$[0].startTime", is(Utility.getTimeAsString(session1.getStartTime()))));
    }

    @Test
    public void saveBooking_itShouldReturnStatusOk() throws Exception {
        User user1 = new User("customer", "*", UserType.CUSTOMER);
        user1.setId("c1");
        User user2 = new User("admin", "*", UserType.ADMIN);
        user1.setId("a1");
        User user3 = new User("worker", "*", UserType.WORKER);
        user1.setId("w1");
        AdminDetails admin = new AdminDetails("Haircut", "Business", user2);
        admin.setId(user2.getId());
        CustomerDetails customer = new CustomerDetails(user1, "John", "Smith",
                "Melbourne", "0123456789", "john@mail.com");
        customer.setId(user1.getId());
        WorkerDetails worker = new WorkerDetails(user3, "Julia", "Baker",
                admin, "0123445556");
        worker.setId(user3.getId());
        Booking booking = new Booking(customer, worker, BookingStatus.NEW_BOOKING,
                "2021-09-02", "8:00:00", "9:00:00", "Haircut");
        booking.setId("b1");
        given(service.saveOrUpdateBooking(Mockito.any(Booking.class))).willReturn(booking);

        String jsonString = objectMapper.writeValueAsString(booking);

        mvc.perform(post("/makebooking/create")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"));
    }
}
