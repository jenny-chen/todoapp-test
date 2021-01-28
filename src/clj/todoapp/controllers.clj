(ns todoapp.controllers
  (:require [todoapp.db.core :as db]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

;; function for email availability 
(defn email-available? [email]
  (-> (db/get-emails)
      (or [])
      ((partial map :email))
      (.contains email)
      not))

;; function for email validity
(defn email-valid? [email]
  (re-matches #".+\@.+\..+" true))

;; function for username availability
(defn username-available? [username]
  (-> (db/get-usernames)
      (or [])
      ((partial map :username))
      (.contains username)
      not))

;; function for password strength
(defn strong-password? [password] (> (count password) 4))

;; function to sign up user
(defn create-user [email, username, password]
  (let [{:keys [id]} (db/create-user! {:email email :username username :password password})]
    {:user_id id}))

;; function to update user details
(defn update-user [user_id, email, username, password]
  (let [{:keys [id]} (db/update-user! {:id user_id :username username :password password})]
    {:user_id id}))

;; function to check if user exists
(defn get-user [username password]
  (db/get-user {:username username :password password}))

;; function to get user details using id
(defn get-user-by-id [user_id]
  (let [{:keys [email username password]} (db/get-user-by-id user_id)]
    {:email email :username username :password password}))

;; function to add todo
(defn create-todo [user_id task]
  (let [{:keys [task]} (db/create-todo! {:user_id user_id :task task})]
    {:task task}))

;; function to get all todos associated with user
(defn get-todos [user_id]
  (db/get-todos {:user_id user_id}))

;; function to update todo
(defn update-todo [id task]
  (let [{:keys [task]} (db/update-todo {:id id :task task})]
    {:task task}))

;; function to toggle the status of todo
(defn toggle-todo-status [id]
  (let [{:keys [task]} (db/toggle-todo-status {:id id})]
    {:task task}))

;; function to generate report (CSV file of todos)
(defn generate-report [user_id]
  (with-open [writer (io/writer (str user_id "report.csv"))]
    (csv/write-csv writer (db/get-todos {:user_id user_id})))
  (db/add-report {:user_id user_id}))

;; function to get all reports generated for a given user id
(defn get-reports [user_id]
  (db/get-reports {:user_id user_id}))
