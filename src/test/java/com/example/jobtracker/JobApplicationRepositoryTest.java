package com.example.jobtracker;

import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static com.example.jobtracker.model.ApplicationStatus.APPLIED;
import static com.example.jobtracker.model.ApplicationStatus.REJECTED;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class JobApplicationRepositoryTest {
    @Autowired
    JobApplicationRepository repository;

    @Test
    public void saveAndFindById() {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setStatus(APPLIED);
        application.setCompany("Samsung");
        repository.save(application);
        Long id = application.getId();
        Optional<JobApplication> result = repository.findById(id);
        assertTrue(result.isPresent());
        assertEquals("Samsung", result.get().getCompany());
        assertEquals(id, result.get().getId());
        assertEquals("Junior Developer", result.get().getPosition());
        assertEquals(APPLIED, result.get().getStatus());
    }

    @Test
    public void findById_returnsEmpty_whenIdDoesNotExist() {
        Optional<JobApplication> result = repository.findById(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByCompany() {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setStatus(APPLIED);
        application.setCompany("Samsung");

        JobApplication application2 = new JobApplication();
        application2.setPosition("Junior Java Developer");
        application2.setStatus(APPLIED);
        application2.setCompany("Apple");

        JobApplication application3 = new JobApplication();
        application3.setPosition("Junior Software Developer");
        application3.setStatus(REJECTED);
        application3.setCompany("Adidas");

        repository.save(application);
        repository.save(application2);
        repository.save(application3);
        List<JobApplication> result = repository.findByCompany("Samsung");
        assertEquals(List.of(application), result);
    }

    @Test
    public void findByStatus() {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setStatus(APPLIED);
        application.setCompany("Samsung");

        JobApplication application2 = new JobApplication();
        application2.setPosition("Junior Java Developer");
        application2.setStatus(APPLIED);
        application2.setCompany("Apple");

        JobApplication application3 = new JobApplication();
        application3.setPosition("Junior Software Developer");
        application3.setStatus(REJECTED);
        application3.setCompany("Adidas");

        repository.save(application);
        repository.save(application2);
        repository.save(application3);
        List<JobApplication> result = repository.findByStatus(APPLIED);
        assertEquals(List.of(application, application2), result);
    }

    @Test
    public void findByCompanyAndStatus() {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setStatus(APPLIED);
        application.setCompany("Samsung");

        JobApplication application2 = new JobApplication();
        application2.setPosition("Junior Java Developer");
        application2.setStatus(APPLIED);
        application2.setCompany("Apple");

        JobApplication application3 = new JobApplication();
        application3.setPosition("Junior Software Developer");
        application3.setStatus(REJECTED);
        application3.setCompany("Samsung");

        repository.save(application);
        repository.save(application2);
        repository.save(application3);
        List<JobApplication> result = repository.findByCompanyAndStatus("Samsung", APPLIED);
        assertEquals(List.of(application), result);
    }

    @Test
    public void deleteById() {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setStatus(APPLIED);
        application.setCompany("Samsung");

        JobApplication application2 = new JobApplication();
        application2.setPosition("Junior Java Developer");
        application2.setStatus(APPLIED);
        application2.setCompany("Apple");

        JobApplication application3 = new JobApplication();
        application3.setPosition("Junior Software Developer");
        application3.setStatus(REJECTED);
        application3.setCompany("Samsung");

        repository.save(application);
        repository.save(application2);
        repository.save(application3);
        assertTrue(repository.existsById(application2.getId()));
        repository.deleteById(application2.getId());
        assertFalse(repository.existsById(application2.getId()));
    }

    @Test
    public void findByStatusOrderByCompanyAsc() {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setStatus(APPLIED);
        application.setCompany("Samsung");

        JobApplication application2 = new JobApplication();
        application2.setPosition("Junior Java Developer");
        application2.setStatus(APPLIED);
        application2.setCompany("Apple");

        JobApplication application3 = new JobApplication();
        application3.setPosition("Junior Software Developer");
        application3.setStatus(REJECTED);
        application3.setCompany("Samsung");

        repository.save(application);
        repository.save(application2);
        repository.save(application3);

        List<JobApplication> result = repository.findByStatusOrderByCompanyAsc(APPLIED);
        assertEquals(List.of(application2, application), result);
    }

    @Test
    public void findByStatus_withPagination() {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setStatus(APPLIED);
        application.setCompany("Samsung");

        JobApplication application2 = new JobApplication();
        application2.setPosition("Junior Java Developer");
        application2.setStatus(APPLIED);
        application2.setCompany("Apple");

        JobApplication application3 = new JobApplication();
        application3.setPosition("Junior Software Developer");
        application3.setStatus(REJECTED);
        application3.setCompany("Samsung");

        JobApplication application4 = new JobApplication();
        application4.setPosition("Java Developer");
        application4.setStatus(APPLIED);
        application4.setCompany("Audi");


        repository.save(application);
        repository.save(application2);
        repository.save(application3);
        repository.save(application4);

        Page<JobApplication> result = repository.findByStatus(APPLIED, PageRequest.of(0, 2));
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getNumberOfElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    public void findByStatus_withPaginationAndSorting() {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setStatus(APPLIED);
        application.setCompany("Samsung");

        JobApplication application2 = new JobApplication();
        application2.setPosition("Junior Java Developer");
        application2.setStatus(APPLIED);
        application2.setCompany("Apple");

        JobApplication application3 = new JobApplication();
        application3.setPosition("Junior Software Developer");
        application3.setStatus(REJECTED);
        application3.setCompany("Samsung");

        JobApplication application4 = new JobApplication();
        application4.setPosition("Java Developer");
        application4.setStatus(APPLIED);
        application4.setCompany("Audi");


        repository.save(application);
        repository.save(application2);
        repository.save(application3);
        repository.save(application4);

        Page<JobApplication> result = repository
                .findByStatus(APPLIED, PageRequest.of(0, 2,
                        Sort.by(Sort.Order.asc("company"))));
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getNumberOfElements());
        assertEquals(0, result.getNumber());
        assertEquals(List.of("Apple", "Audi"), result.get().map(JobApplication::getCompany).toList());
    }

    @Test
    public void findByStatus_secondPage() {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setStatus(APPLIED);
        application.setCompany("Samsung");

        JobApplication application2 = new JobApplication();
        application2.setPosition("Junior Java Developer");
        application2.setStatus(APPLIED);
        application2.setCompany("Apple");

        JobApplication application3 = new JobApplication();
        application3.setPosition("Junior Software Developer");
        application3.setStatus(REJECTED);
        application3.setCompany("Samsung");

        JobApplication application4 = new JobApplication();
        application4.setPosition("Java Developer");
        application4.setStatus(APPLIED);
        application4.setCompany("Audi");


        repository.save(application);
        repository.save(application2);
        repository.save(application3);
        repository.save(application4);

        Page<JobApplication> result = repository
                .findByStatus(APPLIED, PageRequest.of(1, 2,
                        Sort.by(Sort.Order.asc("company"))));
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getNumberOfElements());
        assertEquals(1, result.getNumber());
        assertEquals(List.of("Samsung"), result.get().map(JobApplication::getCompany).toList());
    }
}
