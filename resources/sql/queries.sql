---------- USERS MANAGEMENT

-- :name create-user! :! :1
-- :doc creates a new user record
INSERT INTO users
(email, username, password)
VALUES (:email, :username, :password)
RETURNING *;

-- :name get-user-by-id :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users
WHERE id = :id;

-- :name get-user :? :1
-- :doc retrieves a user record given the username and password
SELECT * FROM users
WHERE username = :username and pass = :password;

-- :name get-emails :? :*
-- :doc retrieves all emails
SELECT email FROM users;

-- :name get-usernames :? :*
-- :doc retrieves all usernames
SELECT username FROM users;

-- :name update-user! :! :n
-- :doc updates an existing user record
UPDATE users
SET email = :email, username = :username, password = :password
WHERE id = :id

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id

---------- TODOS MANAGEMENT

-- :name create-todo! :! :1
-- :doc creates a new todo
INSERT INTO todos
(user_id, task)
VALUES (:user_id, :task)
RETURNING *;

-- :name get-todos :? :*
-- :doc get all todos associated with a user
SELECT * FROM todos
WHERE user_id = :user_id;

-- :name update-todo :? :*
-- :doc update todo with new task
UPDATE todos
SET task = :task, time_last_updated = NOW()
where id = :id
RETURNING *;

-- :name toggle-todo-status :! :*
-- :doc toggle the todo status
UPDATE todos
SET done = NOT done, time_done = NOW()
where id = :id
RETURNING *;

-- :name get-todo-status :? :*
-- :doc retrieve status of todo given its id
SELECT * FROM todos
WHERE id = :id;

---------- REPORTS MANAGEMENT

-- :name generate-report :! :n
-- :doc generate csv file of all todos associated with user
COPY (SELECT * FROM todos WHERE user_id = :user_id)
TO STDOUT (DELIMITER ',');

-- :name add-report :! :n
-- :doc add report to database
INSERT INTO reports
(user_id, time_created)
VALUES (:user_id, NOW())
RETURNING *;

-- :name get-reports :? :*
-- :doc get all reports associated with a user
SELECT * FROM reports
WHERE user_id = :user_id;

