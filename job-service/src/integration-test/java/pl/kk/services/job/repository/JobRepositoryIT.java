package pl.kk.services.job.repository;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import pl.kk.services.job.model.Job;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JobRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JobRepository jobRepository;


    @Test
    public void whenFindByCode_thenReturnJob() {
        Job job = new Job();

        job.setCode("TEST_CODE");
        job.setServiceName("test-service");
        job.setUrlSuffix("test-url");

        entityManager.persist(job);
        entityManager.flush();

        Job found = jobRepository.findAll().get(0);

        assertThat(found.getCode())
                .isEqualTo(job.getCode());


    }
}
