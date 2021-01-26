-- Run this script as a superuser

CREATE ROLE todoapp LOGIN PASSWORD 'todoapp' SUPERUSER;

CREATE DATABASE todoapp
 WITH OWNER = todoapp
      TEMPLATE template0
      ENCODING = 'UTF8'
      TABLESPACE = pg_default
      LC_COLLATE = 'en_US.UTF-8'
      LC_CTYPE = 'en_US.UTF-8'
      CONNECTION LIMIT = -1;

