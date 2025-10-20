# Logging Implementation for MIU Blog Backend

This document describes the comprehensive logging implementation added to the MIU Blog Backend project.

## Overview

The logging implementation provides comprehensive logging throughout the application, including:

- **Controllers**: Request/response logging with user context
- **Services**: Business logic execution logging
- **Security**: Authentication and authorization logging
- **Error Handling**: Exception and error logging
- **Configuration**: Application startup and configuration logging

## Logging Configuration

### Dependencies Added

The following dependencies were added to `build.gradle`:

```gradle
// Logging
implementation 'org.springframework.boot:spring-boot-starter-logging'
implementation 'net.logstash.logback:logstash-logback-encoder:7.4'
```

## Logging Configuration

### Dependencies Added

The following dependencies were added to `build.gradle`:

```gradle
// Logging
implementation 'org.springframework.boot:spring-boot-starter-logging'
implementation("net.logstash.logback:logstash-logback-encoder:8.1")
```

### Configuration Files

1. **application.yml**: Basic logging configuration
2. **logback-spring.xml**: Advanced logging configuration with file appenders and Logstash integration
3. **docker-compose.yml**: ELK Stack (Elasticsearch, Logstash, Kibana) setup
4. **logstash/pipeline/pipeline.conf**: Logstash pipeline configuration
5. **logstash/config/logstash.yml**: Logstash configuration

### Log Levels

- **Application Code**: DEBUG level for detailed business logic
- **Spring Security**: DEBUG level for authentication flows
- **Spring Web**: DEBUG level for HTTP request/response handling
- **Hibernate**: DEBUG level for SQL queries and parameters
- **Root Logger**: INFO level for general application events

## Logging Features

### 1. Request/Response Logging

All controllers now log:
- Incoming requests with parameters
- User context (when available)
- Response data and status
- Processing time and results

### 2. Business Logic Logging

Services log:
- Method entry with parameters
- Key business operations
- Data processing steps
- Success/failure outcomes

### 3. Security Logging

Security components log:
- JWT token validation
- Authentication attempts
- Authorization decisions
- Security configuration

### 4. Error Logging

Error handling logs:
- Exception details with stack traces
- User context during errors
- Error categorization and severity
- Recovery attempts

### 5. Configuration Logging

Configuration components log:
- Application startup
- Configuration loading
- CORS setup
- Security configuration

## Log Output

### Console Output

Logs are displayed in the console with the format:
```
2024-01-15 10:30:45.123 [http-nio-8092-exec-1] INFO  edu.miu.blog.app.controller.ArticleController - Creating new article with title: My First Article
```

### File Output

Logs are written to files in the `logs/` directory:
- `miu-blog-backend.log`: All application logs
- `miu-blog-backend-error.log`: Error logs only

### Log Rotation

- Files are rotated daily
- Maximum file size: 10MB
- Retention: 30 days
- Compression: Enabled

## Logstash Integration

### Overview

The application is configured to send logs to Logstash via TCP socket connection, which then forwards them to Elasticsearch for indexing and analysis through Kibana.

### Configuration

#### Logstash Appender

The Logstash appender is configured in `logback-spring.xml`:

```xml
<!-- Logstash Appender -->
<appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <destination>localhost:5001</destination> <!-- Logstash is exposed on localhost:5001 -->
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        <includeContext>true</includeContext>
        <includeMdc>true</includeMdc>
        <customFields>{"service":"miu-blog-backend","environment":"${spring.profiles.active:default}"}</customFields>
    </encoder>
    <keepAliveDuration>5 minutes</keepAliveDuration>
    <connectionTimeout>1000</connectionTimeout>
    <reconnectionDelay>1000</reconnectionDelay>
</appender>
```

#### Key Features

- **TCP Socket Connection**: Reliable delivery to Logstash
- **JSON Format**: Structured logging for better analysis
- **Custom Fields**: Service name and environment tagging
- **Connection Management**: Automatic reconnection and keep-alive
- **Context Inclusion**: Full logging context and MDC data

#### Logstash Pipeline

The Logstash pipeline (`logstash/pipeline/pipeline.conf`) is configured to:

1. **Receive**: TCP input on port 5000 (mapped to 5001 on host)
2. **Process**: JSON lines codec for structured data
3. **Output**: Forward to Elasticsearch with daily index rotation

```conf
input {
  tcp {
    port => 5000
    codec => json_lines
  }
}
filter {
  # No specific filters are needed for structured JSON logs from logback-spring-encoder
}
output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "spring-boot-logs-%{+YYYY.MM.dd}"
  }
  stdout { codec => rubydebug } # Optional: for debugging Logstash output in its console logs
}
```

### ELK Stack Setup

The complete ELK Stack is configured in `docker-compose.yml`:

- **Elasticsearch**: Port 9200 (data storage and indexing)
- **Logstash**: Port 5001 (log processing and forwarding)
- **Kibana**: Port 5601 (log visualization and analysis)

### Usage

1. **Start the ELK Stack**:
   ```bash
   docker-compose up -d elasticsearch logstash kibana
   ```

2. **Start the Application**:
   ```bash
   ./gradlew bootRun
   ```

3. **Access Kibana**:
   - Open http://localhost:5601
   - Create index pattern: `spring-boot-logs-*`
   - Explore logs in the Discover section

### Log Format in Elasticsearch

Logs are stored in Elasticsearch with the following structure:

```json
{
  "@timestamp": "2024-01-15T10:30:45.123Z",
  "level": "INFO",
  "logger_name": "edu.miu.blog.app.controller.ArticleController",
  "message": "Creating new article with title: My First Article",
  "thread_name": "http-nio-8092-exec-1",
  "service": "miu-blog-backend",
  "environment": "dev",
  "mdc": {
    "userId": "123",
    "requestId": "req-456"
  }
}
```

### Benefits

1. **Centralized Logging**: All logs in one place
2. **Real-time Analysis**: Live log monitoring
3. **Advanced Search**: Full-text search across all logs
4. **Visualization**: Charts and dashboards for log analysis
5. **Alerting**: Set up alerts for specific log patterns
6. **Scalability**: Handle large volumes of logs efficiently

## Log Levels by Component

### Controllers
- **INFO**: Request processing start/end
- **DEBUG**: User context and detailed parameters
- **WARN**: Validation failures and business rule violations

### Services
- **INFO**: Major business operations
- **DEBUG**: Detailed processing steps
- **WARN**: Business rule violations
- **ERROR**: Service failures

### Security
- **INFO**: Authentication success/failure
- **DEBUG**: Token validation details
- **WARN**: Security violations

### Error Handling
- **ERROR**: Unhandled exceptions with stack traces
- **WARN**: Business exceptions
- **DEBUG**: Validation errors

## Performance Considerations

### Async Logging

File logging uses async appenders to minimize performance impact:
- Queue size: 512
- Non-blocking writes
- Automatic flushing

### Log Level Management

- Production: INFO level for application code
- Development: DEBUG level for detailed troubleshooting
- Profile-based configuration

## Monitoring and Alerting

### Key Metrics to Monitor

1. **Error Rate**: Monitor ERROR level logs
2. **Authentication Failures**: Security-related WARN logs
3. **Performance**: Request processing times
4. **Business Logic**: Service operation success rates

## Monitoring and Alerting

### Key Metrics to Monitor

1. **Error Rate**: Monitor ERROR level logs
2. **Authentication Failures**: Security-related WARN logs
3. **Performance**: Request processing times
4. **Business Logic**: Service operation success rates
5. **Logstash Connection**: TCP connection health
6. **Elasticsearch Indexing**: Log ingestion rate

### Log Analysis Tools

#### ELK Stack (Recommended)
- **Elasticsearch**: Log storage and search
- **Logstash**: Log processing and forwarding
- **Kibana**: Log visualization and dashboards

#### Alternative Tools
- Splunk
- CloudWatch (AWS)
- Application Insights (Azure)
- Grafana + Loki

### Kibana Dashboards

Create dashboards in Kibana to monitor:

1. **Application Health Dashboard**:
   - Error rate over time
   - Response time percentiles
   - Request volume by endpoint

2. **Security Dashboard**:
   - Authentication attempts
   - Failed login attempts
   - JWT token validation failures

3. **Business Metrics Dashboard**:
   - Article creation rate
   - User registration trends
   - Comment activity

### Alerting Setup

Configure alerts in Kibana for:

- Error rate > 5% in 5 minutes
- Authentication failure rate > 10% in 1 minute
- No logs received for 2 minutes (service down)
- High response time (> 2 seconds) for 1 minute

## Best Practices

### 1. Log Message Format

```java
// Good: Structured logging with context
log.info("User {} created article with slug: {}", user.getUsername(), article.getSlug());

// Avoid: String concatenation
log.info("User " + user.getUsername() + " created article");
```

### 2. Log Level Usage

- **ERROR**: System errors, exceptions
- **WARN**: Business rule violations, recoverable errors
- **INFO**: Important business events
- **DEBUG**: Detailed processing information
- **TRACE**: Very detailed execution flow

### 3. Sensitive Data

Never log:
- Passwords
- Credit card numbers
- Personal identification numbers
- JWT tokens (log only validation status)

### 4. Performance

- Use parameterized logging
- Avoid expensive operations in log statements
- Use appropriate log levels in production

## Troubleshooting

### Common Issues

1. **Missing Logs**: Check log level configuration
2. **Performance Impact**: Verify async appender configuration
3. **Disk Space**: Monitor log file rotation settings
4. **Security**: Ensure sensitive data is not logged

### Debug Mode

To enable debug mode for troubleshooting:

```yaml
logging:
  level:
    edu.miu.blog.app: DEBUG
    root: DEBUG
```

## Future Enhancements

1. **Structured Logging**: Implement JSON logging format
2. **Correlation IDs**: Add request tracing
3. **Metrics Integration**: Connect with monitoring systems
4. **Log Aggregation**: Centralized logging solution
5. **Real-time Alerts**: Automated error notifications

## Conclusion

This comprehensive logging implementation provides:

- Complete visibility into application behavior
- Detailed error tracking and debugging capabilities
- Performance monitoring and optimization insights
- Security audit trail
- Business operation tracking

The logging system is designed to be production-ready with proper performance considerations and security best practices.
