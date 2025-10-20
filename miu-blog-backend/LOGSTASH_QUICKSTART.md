# Logstash Integration Quick Start Guide

## Overview

This guide will help you set up and use the Logstash integration for centralized logging in the MIU Blog Backend application.

## Prerequisites

- Docker and Docker Compose installed
- Java 24+ and Gradle
- Ports 5001, 5601, 9200 available

## Quick Setup

### 1. Start the ELK Stack

```bash
# Start Elasticsearch, Logstash, and Kibana
docker-compose up -d elasticsearch logstash kibana

# Check if services are running
docker-compose ps
```

### 2. Verify Logstash is Ready

```bash
# Check Logstash logs
docker-compose logs logstash

# You should see: "Successfully started Logstash API endpoint"
```

### 3. Start the Application

```bash
# Start the Spring Boot application
./gradlew bootRun
```

### 4. Access Kibana

1. Open http://localhost:5601 in your browser
2. Go to "Stack Management" > "Index Patterns"
3. Create index pattern: `spring-boot-logs-*`
4. Select `@timestamp` as the time field
5. Go to "Discover" to view logs

## Testing the Integration

### 1. Generate Some Logs

Make API calls to your application:

```bash
# Register a user
curl -X POST http://localhost:8092/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8092/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Get tags
curl http://localhost:8092/api/tags
```

### 2. Check Logs in Kibana

1. Go to Kibana Discover
2. Select the `spring-boot-logs-*` index pattern
3. You should see logs appearing in real-time
4. Use the search bar to filter logs

## Log Structure

Each log entry in Elasticsearch contains:

```json
{
  "@timestamp": "2024-01-15T10:30:45.123Z",
  "level": "INFO",
  "logger_name": "edu.miu.blog.app.controller.UserController",
  "message": "User registration request for username: testuser",
  "thread_name": "http-nio-8092-exec-1",
  "service": "miu-blog-backend",
  "environment": "default",
  "mdc": {}
}
```

## Useful Kibana Queries

### Find All Errors
```
level:ERROR
```

### Find Authentication Logs
```
logger_name:*security* OR message:*login* OR message:*auth*
```

### Find Article-Related Logs
```
logger_name:*Article* OR message:*article*
```

### Find Logs from Specific Time Range
```
@timestamp:[2024-01-15T10:00:00 TO 2024-01-15T11:00:00]
```

## Troubleshooting

### No Logs Appearing in Kibana

1. **Check Logstash Connection**:
   ```bash
   # Check if Logstash is receiving logs
   docker-compose logs logstash | grep "tcp"
   ```

2. **Verify Port Mapping**:
   ```bash
   # Check if port 5001 is accessible
   telnet localhost 5001
   ```

3. **Check Application Logs**:
   ```bash
   # Look for Logstash connection errors
   tail -f logs/miu-blog-backend.log | grep -i logstash
   ```

### Connection Issues

1. **Restart Logstash**:
   ```bash
   docker-compose restart logstash
   ```

2. **Check Elasticsearch Health**:
   ```bash
   curl http://localhost:9200/_cluster/health
   ```

3. **Verify Index Creation**:
   ```bash
   curl http://localhost:9200/_cat/indices
   ```

## Performance Considerations

### Logstash Configuration

The current configuration includes:
- **Keep-alive**: 5 minutes
- **Connection timeout**: 1 second
- **Reconnection delay**: 1 second

### Elasticsearch Index Management

- **Index Pattern**: `spring-boot-logs-YYYY.MM.dd`
- **Retention**: Configure in Kibana Index Lifecycle Management
- **Shard Size**: Default settings (adjust based on log volume)

## Advanced Configuration

### Custom Fields

Add custom fields to logs by modifying the LogstashEncoder:

```xml
<customFields>{"service":"miu-blog-backend","environment":"${spring.profiles.active:default}","version":"1.0.0"}</customFields>
```

### Filtering Logs

Modify `logstash/pipeline/pipeline.conf` to add filters:

```conf
filter {
  if [logger_name] =~ /security/ {
    mutate {
      add_field => { "log_category" => "security" }
    }
  }
}
```

### Index Templates

Create index templates in Elasticsearch for better field mapping:

```bash
curl -X PUT "localhost:9200/_index_template/spring-boot-logs" \
  -H 'Content-Type: application/json' \
  -d '{
    "index_patterns": ["spring-boot-logs-*"],
    "template": {
      "mappings": {
        "properties": {
          "@timestamp": { "type": "date" },
          "level": { "type": "keyword" },
          "logger_name": { "type": "text" },
          "message": { "type": "text" },
          "service": { "type": "keyword" },
          "environment": { "type": "keyword" }
        }
      }
    }
  }'
```

## Monitoring

### Health Checks

1. **Logstash Health**:
   ```bash
   curl http://localhost:9600/_node/stats
   ```

2. **Elasticsearch Health**:
   ```bash
   curl http://localhost:9200/_cluster/health
   ```

3. **Kibana Status**:
   ```bash
   curl http://localhost:5601/api/status
   ```

### Metrics to Monitor

- Log ingestion rate
- Elasticsearch cluster health
- Logstash pipeline performance
- Index size and growth
- Search performance

## Cleanup

To stop and remove all ELK Stack containers:

```bash
docker-compose down -v
```

This will remove all containers and volumes (including stored logs).
