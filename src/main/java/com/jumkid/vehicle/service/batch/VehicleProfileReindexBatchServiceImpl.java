package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.exception.BatchProcessException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "vehicleProfileReindexBatchService")
public class VehicleProfileReindexBatchServiceImpl implements OnDemandBatchService {

    private final JobBuilderFactory jobBuilderFactory;
    private final JobLauncher jobLauncher;
    private final JobCompletionNotificationListener jobCompletionNotificationListener;
    private final StepBuilderFactory stepBuilderFactory;

    private final RepositoryItemReader<VehicleMasterEntity> reader;
    private final ItemWriter<VehicleSearch> writer;
    private final ItemProcessor processor;

    public VehicleProfileReindexBatchServiceImpl(JobBuilderFactory jobBuilderFactory,
                                                 JobLauncher jobLauncher,
                                                 JobCompletionNotificationListener jobCompletionNotificationListener,
                                                 StepBuilderFactory stepBuilderFactory,
                                                 RepositoryItemReader<VehicleMasterEntity> reader,
                                                 ItemWriter<VehicleSearch> writer,
                                                 ItemProcessor processor) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.jobLauncher = jobLauncher;
        this.jobCompletionNotificationListener = jobCompletionNotificationListener;
        this.stepBuilderFactory = stepBuilderFactory;
        this.reader = reader;
        this.writer = writer;
        this.processor = processor;
    }

    @Override
    public void runJob() throws BatchProcessException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        try {
            Step step = this.stepBuilderFactory.get("vehicle-profile-reindex-step")
                    .chunk(3)
                    .processor(processor)
                    .reader(reader)
                    .writer(writer)
                    .build();

            jobLauncher.run(createJob(jobCompletionNotificationListener, step), jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("failed to run batch job {}", e.getMessage());
        }
    }

    private Job createJob(JobCompletionNotificationListener listener, Step step) {
        return this.jobBuilderFactory.get("vehicle-profile-reindex-job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step)
                .build();
    }
}
