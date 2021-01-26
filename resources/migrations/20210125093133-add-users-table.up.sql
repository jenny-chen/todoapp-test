CREATE TABLE if not exists users
  (id SERIAL PRIMARY KEY,
   email TEXT UNIQUE, 
   username TEXT UNIQUE,
   password TEXT);
--;;
CREATE TABLE if not exists todos
  (id SERIAL PRIMARY KEY, 
   user_id INTEGER references users (id), 
   task TEXT,
   time_created TIMESTAMP NOT NULL DEFAULT NOW(),
   time_last_updated TIMESTAMP,
   time_done TIMESTAMP,
   done BOOLEAN NOT NULL DEFAULT FALSE);
--;;
CREATE TABLE if not exists reports
  (id SERIAL PRIMARY KEY,
   user_id INTEGER references users (id),
   time_created TIMESTAMP NOT NULL DEFAULT NOW());
