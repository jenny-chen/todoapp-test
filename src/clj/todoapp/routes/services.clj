(ns todoapp.routes.services
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.coercion.schema :as schema]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [schema.core :as s]
    [todoapp.controllers :as c]
    [todoapp.middleware.formats :as formats]
    [ring.util.http-response :refer :all]
    [clojure.java.io :as io]))

(defn service-routes []
  ["/api"
   {:coercion schema/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 coercion/coerce-exceptions-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]

   ["/ping"
    {:get (constantly (ok {:message "pong"}))}]
   
    ["/email-available"
     {:get {:summary "Check availability of email"
            :parameters {:query {:email s/Str}}
            :responses {200 {:body {:available s/Bool}}}
            :handler (fn [{{{:keys [email]} :query} :parameters}]
                       {:status 200
                        :body {:available (c/email-available? email)}})}}]

    ["/email-valid"
     {:get {:summary "Check validity of email"
            :parameters {:query {:email s/Str}}
            :responses {200 {:body {:available s/Bool}}}
            :handler (fn [{{{:keys [email]} :query} :parameters}]
                       {:status 200
                        :body {:valid (c/email-valid? email)}})}}]

    ["/username-available"
     {:get {:summary "Check availability of username"
            :parameters {:query {:username s/Str}}
            :responses {200 {:body {:available s/Bool}}}
            :handler (fn [{{{:keys [username]} :query} :parameters}]
                       {:status 200
                        :body {:available (c/username-available? username)}})}}]

    ["/strong-password"
     {:post {:summary "Check password strength"
             :parameters {:body {:password s/Str}}
             :responses {200 {:body {:strong s/Bool}}}
             :handler (fn [{{{:keys [password]} :body} :parameters}]
                        {:status 200
                         :body {:strong (c/strong-password? password)}})}}]

    ["/user"
      ["/"
       {:post {:summary "Create a new user"
               :parameters {:body {:email s/Str :username s/Str :password s/Str}}
               :responses {200 {:body {:user_id s/Int}}}
               :handler (fn [{{{:keys [email username password]} :body} :parameters}]
                          {:status 200
                           :body (c/create-user email username password)})}
        :put {:summary "Update an existing user"
              :parameters {200 {:body {:user_id s/Int}}}
              :handler (fn [{{{:keys [id email username password]} :body} :parameters}]
                         {:status 200
                          :body (c/update-user id email username password)})}
        :get {:summary "Get user details"
              :parameters {200 {:body {:email s/Str :username s/Str :password s/Str}}}
              :handler (fn [{{{:keys [id]} :body} :parameters}]
                         {:status 200
                          :body (c/get-user-by-id id)})}}]
      ["/exists"
       {:get {:summary "Check if user exists"
               :parameters {:query {:username s/Str :password s/Str}}
               :responses {200 {:body {:user_id (s/maybe s/Int)}}}
               :handler (fn [{{{:keys [username password]} :query} :parameters}]
                          {:status 200
                           :body {:user_id (:id (c/get-user username password))}})}}]

      ;; todos

      ["/:user_id/todo/:task"
       {:post {:summary "Create todo task associated with the given user-id"
               :parameters {:path {:user_id s/Int :task s/Str}}
               :responses {200 {:body {:task s/Str}}}
               :handler (fn [{{{:keys [user_id task]} :path} :parameters}]
                          {:status 200
                           :body {:task (c/create-todo user_id task)}})}

        :put {:summary "Update existing todo task"
               :parameters {:path {:user_id s/Int :task s/Str}}
               :responses {200 {:body {:task s/Str}}}
               :handler (fn [{{{:keys [user_id task]} :path} :parameters}]
                          {:status 200
                           :body {:task (c/update-todo user_id task)}})}}]

      ["/:user_id/toggle-todo-status"
       {:post {:summary "Toggle done status of todo"
               :parameters {:path {:user_id s/Int}}
               :responses {200 {:body {:task s/Str}}}
               :handler (fn [{{{:keys [user_id]} :path} :parameters}]
                          {:status 200
                           :body {:task (c/toggle-todo-status user_id)}})}}]
      ;; reports
      ["/:user_id/report"
       {:post {:summary "Generate csv of todos associated with user-id"
               :parameters {:path {:user_id s/Int}}
               :responses {200 {:body {:task s/Str}}}
               :handler (fn [{{{:keys [user_id]} :path} :parameters}]
                          {:status 200
                           :body {:task (c/generate-report user_id)}})}
        :get {:summary "Get all reports generated for the user"
              :parameters {:path {:user_id s/Int}}
              :responses {200 {:body {:task s/Str}}}
              :handler (fn [{{{:keys [user_id]} :path} :parameters}]
                         {:status 200
                          :body {:task (c/get-reports user_id)}})}}]
    ]
])
