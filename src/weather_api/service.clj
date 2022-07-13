(ns weather-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor.helpers :refer [definterceptor defhandler]]
            [ring.util.response :as ring-resp]
            [monger.core :as mg]
            [clojure.string :as str]
            [monger.collection :as mc]
            [cheshire.core :as json]
            [clj-http.client :as client]))

(def open-weather-url "https://api.openweathermap.org/data/2.5/weather")
(def open-weather-appid "d0e239f15d5f56006808fd1cf63f968c")

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn home-page
  [request]
  (ring-resp/response "Simple weather api!"))

(defn get-db
  ([]
   (get-db "mongodb+srv://nise:nise%40123@cluster0.vtdda.mongodb.net/holidaypirate?retryWrites=true&w=majority"))
  ([conn-string]
   (let [uri conn-string
         {:keys [conn db]} (mg/connect-via-uri uri)]
     {:conn conn :db db})))

(defn get-weather-history [request]
  (let [{:keys [db]} (get-db)
        city (get-in request [:path-params :city])]
    (ring-resp/response
     (json/generate-string
      (mc/find-maps db "weather-history" {:city (str/lower-case city)})))))

(defn get-city-weather
  [request]
  (let [city (get-in request [:path-params :city])
        report (-> open-weather-url
                   (client/get
                    {:query-params {:appid open-weather-appid
                                    :q city
                                    :units "metric"}})
                   :body
                   (json/parse-string true))
        output {:temperature (get-in report [:main :temp])
                :datetime (java.util.Date.)
                :city (str/lower-case (:name report))}
        {:keys [db]} (get-db)]
    (try
      ;;FIXME wrong to insert data in db in get request but assuming the requirements
      (mc/insert db "weather-history" (assoc output :_id (:datetime output)))
      (catch Exception ex ()))
    (ring-resp/response  (json/generate-string output))))


;;;;token check interceptor
(defhandler token-check [request]
  (let [token (get-in request [:headers "app-token"])]
    (if (not (= token "niseapp"))
      (assoc (ring-resp/response {:body "access denied"}) :status 403))))


;; Terse/Vector-based routes
(def routes
  `[[["/" {:get home-page}
      ^:interceptors [(body-params/body-params) http/html-body token-check]
      ["/about" {:get about-page}]
      #_["/weather" {:get weather-handler}]
      ["/weather/:city" {:get get-city-weather}]
      ["/weather-history/:city" {:get get-weather-history}]]]])


;; Consumed by weather-api.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port (Integer. (or (System/getenv "PORT") 8080))
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false
                                        ;; Alternatively, You can specify you're own Jetty HTTPConfiguration
                                        ;; via the `:io.pedestal.http.jetty/http-configuration` container option.
                                        ;:io.pedestal.http.jetty/http-configuration (org.eclipse.jetty.server.HttpConfiguration.)
                                        }})


(comment
  (require '[clojure.pprint :as pp :refer [pprint]])
  (def j (-> open-weather-url
             (client/get
              {:query-params {:appid open-weather-appid
                              :q "London"
                              :units "metric"}})
             :body
             (json/parse-string true)))
  )
