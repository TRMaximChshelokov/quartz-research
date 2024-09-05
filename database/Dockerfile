# Use the official PostgreSQL image from the Docker Hub
FROM postgres:latest

# Set environment variables for PostgreSQL
ENV POSTGRES_USER=quartz_user
ENV POSTGRES_PASSWORD=quartz_password
ENV POSTGRES_DB=quartz_db

# Copy the SQL script to initialize Quartz tables into the Docker container
COPY init_quartz_tables.sql /docker-entrypoint-initdb.d/

# Expose the default PostgreSQL port
EXPOSE 5432