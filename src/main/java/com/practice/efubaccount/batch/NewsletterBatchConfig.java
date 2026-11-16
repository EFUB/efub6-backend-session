package com.practice.efubaccount.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class NewsletterBatchConfig {

    @Bean
    public Job newsletterJob(JobRepository jobRepository, Step clearStep, Step sendStep) {
        return new JobBuilder("newsletterJob", jobRepository)
                .start(clearStep)
                .next(sendStep)
                .build();
    }

    @Bean
    public Step clearStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          JdbcTemplate jdbcTemplate) {
        return new StepBuilder("clearStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.update("DELETE FROM mail_log");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step sendStep(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager,
                         JdbcPagingItemReader<Subscriber> subscriberReader,
                         ItemProcessor<Subscriber, MailLog> newsletterProcessor,
                         JdbcBatchItemWriter<MailLog> mailLogWriter) {
        return new StepBuilder("sendStep", jobRepository)
                .<Subscriber, MailLog>chunk(100, transactionManager)
                .reader(subscriberReader)
                .processor(newsletterProcessor)
                .writer(mailLogWriter)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Subscriber> subscriberReader(DataSource dataSource) {
        return new JdbcPagingItemReaderBuilder<Subscriber>()
                .name("subscriberReader")
                .dataSource(dataSource)
                .selectClause("account_id, email, nickname")
                .fromClause("accounts")
                .whereClause("status = 'ACTIVE' AND email LIKE 'newsletter-user-%'")
                .sortKeys(Map.of("account_id", Order.ASCENDING))
                .pageSize(100)
                .rowMapper(new DataClassRowMapper<>(Subscriber.class))
                .build();
    }

    @Bean
    public ItemProcessor<Subscriber, MailLog> newsletterProcessor() {
        return subscriber -> {
            // 💥 메일 서버 장애 시뮬레이션
//             if (subscriber.nickname().equals("구독자557")) {
//                 throw new IllegalStateException("💥 메일 서버 장애 발생");
//             }

            String subject = "[EFUB] " + subscriber.nickname() + "님, 이번 주 소식이 도착했어요!";
            return MailLog.of(subscriber, subject);
        };
    }

    @Bean
    public JdbcBatchItemWriter<MailLog> mailLogWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<MailLog>()
                .dataSource(dataSource)
                .sql("INSERT INTO mail_log (account_id, email, subject, sent_at) "
                        + "VALUES (:accountId, :email, :subject, :sentAt)")
                .beanMapped()
                .build();
    }
}