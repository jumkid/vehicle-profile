package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.exception.BatchProcessException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service(value = "vehicleProfileReindexBatchService")
public class VehicleProfileReindexBatchServiceImpl implements OnDemandBatchService {

    private final JobBuilderFactory jobBuilderFactory;
    private final JobLauncher jobLauncher;
    private final JobCompletionNotificationListener jobCompletionNotificationListener;
    private final StepBuilderFactory stepBuilderFactory;

    private final RepositoryItemReader<VehicleMasterEntity> reader;
    private final ItemWriter<VehicleSearch> writer;
    private final ItemProcessor<VehicleMasterEntity, VehicleSearch> processor;

    public VehicleProfileReindexBatchServiceImpl(JobBuilderFactory jobBuilderFactory,
                                                 JobLauncher jobLauncher,
                                                 JobCompletionNotificationListener jobCompletionNotificationListener,
                                                 StepBuilderFactory stepBuilderFactory,
                                                 RepositoryItemReader<VehicleMasterEntity> reader,
                                                 ItemWriter<VehicleSearch> writer,
                                                 ItemProcessor<VehicleMasterEntity, VehicleSearch> processor) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.jobLauncher = jobLauncher;
        this.jobCompletionNotificationListener = jobCompletionNotificationListener;
        this.stepBuilderFactory = stepBuilderFactory;
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
            Step step = this.stepBuilderFactory.get("vehicle-profile-reindex-step")
                    .<VehicleMasterEntity, VehicleSearch>chunk(500)
                    .processor(processor)
                    .reader(reader)
                    .writer(writer)
                    .listener(countListener)
                    .build();

            JobExecution jobExecution = jobLauncher.run(createJob(jobCompletionNotificationListener, step), jobParameters);
            Date start = jobExecution.getCreateTime();
            Date end = jobExecution.getEndTime();
            long timeSpent = (end != null) ? end.getTime() - start.getTime() : -1L;
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
        return this.jobBuilderFactory.get("vehicle-profile-reindex-job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step)
                .build();
    }
}
