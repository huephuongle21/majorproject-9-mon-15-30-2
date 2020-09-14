package com.rmit.sept.monday15302;

import com.rmit.sept.monday15302.Repositories.UserRepository;
import com.rmit.sept.monday15302.Repositories.WorkerDetailsRepository;
import com.rmit.sept.monday15302.exception.UserException;
import com.rmit.sept.monday15302.exception.WorkerDetailsException;
import com.rmit.sept.monday15302.model.AdminDetails;
import com.rmit.sept.monday15302.model.User;
import com.rmit.sept.monday15302.model.UserType;
import com.rmit.sept.monday15302.model.WorkerDetails;
import com.rmit.sept.monday15302.services.UserService;
import com.rmit.sept.monday15302.services.WorkerDetailsService;
import com.rmit.sept.monday15302.utils.Request.EditWorker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkerDetailsServiceTest {
    @Autowired
    private WorkerDetailsService workerDetailsService;

    @MockBean
    private WorkerDetailsRepository workerDetailsRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private static final String workerId_1 = "w1";
    private static final String workerId_2 = "w2";
    private static final String adminId = "a1";
    private static WorkerDetails worker1;
    private static User user1;

    @Before
    public void setup() {
        User user = new User(adminId,"admin", "*", UserType.ADMIN);
        AdminDetails admin = new AdminDetails("Salon", "Massage", user);

        user1 = new User(workerId_1, "worker", "*", UserType.WORKER);
        worker1 = new WorkerDetails(user1, "John",
                "Smith", admin, "0123456789");
        worker1.setId(workerId_1);

        User user2 = new User(workerId_2, "worker1", "*", UserType.WORKER);
        WorkerDetails worker2 = new WorkerDetails(user2, "John",
                "Smith", admin, "0123456789");
        worker2.setId(workerId_2);

        List<WorkerDetails> workers = Arrays.asList(worker1, worker2);

        Mockito.when(workerDetailsRepository.findAll())
                .thenReturn(workers);

        Mockito.when(workerDetailsRepository.getWorkersByAdminId(adminId))
                .thenReturn(workers);

        Mockito.when(workerDetailsRepository.getWorkerById(workerId_1)).thenReturn(worker1);
        Mockito.when(userService.getUserById(workerId_1)).thenReturn(user1);
        Mockito.when(userService.getUserById(workerId_2)).thenReturn(user2);

        Mockito.when(userService.existsByUsername("123")).thenReturn(true);
    }

    @Test
    public void getAllWorkers_returnTrue_ifWorkersFound() {
        List<WorkerDetails> list = workerDetailsService.getAllWorkers();
        assert(list.size() == 2 && list.get(0).getId().equals(workerId_1)
                    && list.get(1).getId().equals(workerId_2));
    }

    @Test
    public void getWorkerById_returnWorker_ifWorkerFound() {
        EditWorker toCheck = workerDetailsService.getWorkerById(workerId_1);
        assert(toCheck.getId().equals(workerId_1));
    }

    @Test(expected = WorkerDetailsException.class)
    public void getWorkerById_throwException_ifWorkerNotFound()
            throws WorkerDetailsException {
        assert(workerDetailsService.getWorkerById("1234") == null);
    }

    @Test
    public void getWorkerByAdminIds_returnWorkers_IfWorkersFound() {
        List<String> adminIds = Arrays.asList(adminId);
        List<WorkerDetails> workerList = workerDetailsService.getWorkerByAdminIds(adminIds);
        assert(workerList.size() == 2);
    }

    @Test(expected = WorkerDetailsException.class)
    public void getWorkerByAdminIds_throwException_IfNoWorkersFound()
            throws WorkerDetailsException {
        List<String> adminIdList = Arrays.asList("a3", "a4");
        workerDetailsService.getWorkerByAdminIds(adminIdList);
    }

    @Test
    public void createWorker_returnTrue_ifWorkerAdded() {
        workerDetailsService.saveWorker(worker1, "worker");
        Mockito.verify(workerDetailsRepository,
                times(1)).save(worker1);
    }

    @Test
    public void testDeleteWorker() {
        Mockito.when(workerDetailsRepository.getWorkerById(workerId_1)).thenReturn(worker1);
        // when
        workerDetailsService.deleteWorker(workerId_1);
        // then
        Mockito.verify(workerDetailsRepository, times(1)).delete(worker1);
    }

    @Test
    public void getWorkerByAdminId_returnWorkers_IfWorkersFound() {
        List<EditWorker> workers = workerDetailsService.getWorkersByAdminId(adminId);
        assert(!workers.isEmpty());
    }

    @Test(expected = WorkerDetailsException.class)
    public void getWorkerByAdminId_throwException_IfNoWorkersFound()
            throws WorkerDetailsException {
        workerDetailsService.getWorkersByAdminId("hello123");
    }

    @Test(expected = WorkerDetailsException.class)
    public void updateWorker_throwException_IfWorkerNotFound()
            throws WorkerDetailsException {
        workerDetailsService.updateWorker(new EditWorker(), "345");
    }

    @Test(expected = UserException.class)
    public void updateWorker_throwException_IfNewUsernameExisted()
            throws UserException, NullPointerException {
        EditWorker worker = new EditWorker();
        worker.setUsername("123");

        workerDetailsService.updateWorker(worker, workerId_1);
    }

    @Test
    public void updateWorker_returnWorker_IfWorkerUpdated() {
        EditWorker worker = new EditWorker(workerId_1, user1.getUserName(), user1.getPassword(),
                worker1.getfName(), worker1.getlName(), worker1.getPhoneNumber());
        Mockito.when(workerDetailsRepository.save(worker1)).thenReturn(worker1);
        Mockito.when(userService.saveUser(user1)).thenReturn(user1);
        // when
        workerDetailsService.updateWorker(worker, workerId_1);
        // then
        Mockito.verify(workerDetailsRepository, times(1)).save(worker1);
    }
}