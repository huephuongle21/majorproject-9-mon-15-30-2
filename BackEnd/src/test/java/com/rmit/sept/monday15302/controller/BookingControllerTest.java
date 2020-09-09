package com.rmit.sept.monday15302.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmit.sept.monday15302.model.*;
import com.rmit.sept.monday15302.services.*;
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
import java.util.Date;
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
    public void givenWorkersWithService_fetchServiceByWorker() throws Exception {
        String workerId = "w1";
        String service = "Haircut";
        String adminId = "a1";

        WorkerDetails worker = new WorkerDetails();
        worker.setId(workerId);

        given(workerDetailsService.getAdminIdByWorkerId(workerId)).willReturn(adminId);
        given(adminDetailsService.getServiceByAdminId(adminId)).willReturn(service);

        mvc.perform(get("/makebooking/byworker/{workerId}", workerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Haircut"));
    }

    @Test
    public void fetchWorkingHoursByWorkerIdAndDate() throws Exception {
        String adminId = "a1";
        String workerId = "w1";
        String date = "2020-09-04";

        WorkingHours workingHours = new WorkingHours();
        workingHours.setStartTime("8:00:00");
        workingHours.setEndTime("17:00:00");

        List<Date> hours = new ArrayList<>();
        hours.add(workingHours.getStartTime());
        hours.add(workingHours.getEndTime());

        given(workerDetailsService.getAdminIdByWorkerId(workerId)).willReturn(adminId);
        given(workingHoursService.getOpeningHours(adminId, date)).willReturn(hours);

        mvc.perform(get("/makebooking/openinghours/{workerId}/{date}", workerId, date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void fetchUnavailableSessionByWorkerIdAndDate() throws Exception {
        String workerId = "w1";
        String date = "2020-09-04";

        String bookingId1 = "b1";
        String bookingId2 = "b2";
        Booking booking1 = new Booking();
        booking1.setId(bookingId1);
        Booking booking2 = new Booking();
        booking2.setId(bookingId2);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        given(service.getUnavailableSessions(workerId, date)).willReturn(bookings);

        mvc.perform(get("/makebooking/unavailablesessions/{workerId}/{date}", workerId, date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingId1)))
                .andExpect(jsonPath("$[1].id", is(bookingId2)));
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
        WorkerDetails worker = new WorkerDetails(user3, "Julia", "Baker", admin);
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