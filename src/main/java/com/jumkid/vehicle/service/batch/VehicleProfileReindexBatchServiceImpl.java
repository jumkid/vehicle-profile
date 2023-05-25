package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.exception.BatchProcessException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

@Slf4j
@Service(value = "vehicleProfileReindexBatchService")
public class VehicleProfileReindexBatchServiceImpl implements OnDemandBatchService {

    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;
    private final JobCompletionNotificationListener jobCompletionNotificationListener;
    private final PlatformTransactionManager transactionManager;

    private final RepositoryItemReader<VehicleMasterEntity> reader;
    private final ItemWriter<VehicleSearch> writer;
    private final ItemProcessor<VehicleMasterEntity, VehicleSearch> processor;

    public VehicleProfileReindexBatchServiceImpl(JobRepository jobRepository, JobLauncher jobLauncher,
                                                 JobCompletionNotificationListener jobCompletionNotificationListener,
                                                 PlatformTransactionManager transactionManager,
                                                 RepositoryItemReader<VehicleMasterEntity> reader,
                                                 ItemWriter<VehicleSearch> writer,
                                                 ItemProcessor<VehicleMasterEntity, VehicleSearch> processor) {
        this.jobRepository = jobRepository;
        this.jobLauncher = jobLauncher;
        this.jobCompletionNotificationListener = jobCompletionNotificationListener;
        this.transactionManager = transactionManager;
        this.reader = reader;
        this.writer = writer;
        this.processor = processor;
    }

    @Override
    public int runJob() throws BatchProcessException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();

        ItemCountListener countListener = new ItemCountListener();

        try {
            Step step = new StepBuilder("vehicle-profile-reindex-step", jobRepository)
                    .<VehicleMasterEntity, VehicleSearch>chunk(500, transactionManager)
                    .processor(processor)
                    .reader(reader)
                    .writer(writer)
                    .listener(countListener)
                    .build();

            JobExecution jobExecution = jobLauncher.run(createJob(jobCompletionNotificationListener, step), jobParameters);
            LocalDateTime start = jobExecution.getCreateTime();
            LocalDateTime end = jobExecution.getEndTime();
            long timeSpent = (end != null) ? end.getLong(ChronoField.MILLI_OF_SECOND) - start.getLong(ChronoField.MILLI_OF_SECOND) : -1L;
            int total = countListener.getCounter();
            log.info("Vehicle Profile Reindex : total {} items,  start time [{}], end time [{}], time spent [{}]",
                    total, start, end, timeSpent);

            return total;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("failed to run batch job {}", e.getMessage());
            throw new BatchProcessException(e.getMessage());
        }
    }

    private Job createJob(JobCompletionNotificationListener listener, Step step) {
        return new JobBuilder("vehicle-profile-reindex-job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step)
                .build();
    }
}
