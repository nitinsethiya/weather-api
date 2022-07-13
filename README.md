# weather-api
This app was created using pedestal-service app tempelate which uses jetty server impleted by pedestal and uses ring handler internally.

This is sample microservice exposing two endpoints to get the weather (temperature) of a city and history of requests for a specific city.

To run it locally, just run "lein run-dev" and it will start a server which listens to port 8080 or if had set a port at enviornment variable.

To make it use a simple auth, you have to pass a header "app-token" with a value "niseapp" in order for this app to work. Meanwhile this can be deployed to any cloud based service provider. I did my deployment in heroku.

To store data, it uses a personal cluster created at mongodb cluster and right now allows all IPs to fetch the data which will be removed at later stage.

Endpoints:

http://localhost:8080  "Default"

http://localhost:8080/about "about info"

http://localhost:8080/weather/{city}  "get weather for the city"

http://localhost:8080/weather-history/{city}  "get weather history for the city"

## Getting Started

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/) to see: `Hello World!`
3. Read your app's source code at src/weather_api/service.clj. Explore the docs of functions
   that define routes and responses.
4. Run your app's tests with `lein test`. Read the tests at test/weather_api/service_test.clj.
5. Learn more! See the [Links section below](#links).


## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).


## Developing your service

1. Start a new REPL: `lein repl`
2. Start your service in dev-mode: `(def dev-serv (run-dev))`
3. Connect your editor to the running REPL session.
   Re-evaluated code will be seen immediately in the service.

### [Docker](https://www.docker.com/) container support

1. Configure your service to accept incoming connections (edit service.clj and add  ::http/host "0.0.0.0" )
2. Build an uberjar of your service: `lein uberjar`
3. Build a Docker image: `sudo docker build -t weather-api .`
4. Run your Docker image: `docker run -p 8080:8080 weather-api`

### [OSv](http://osv.io/) unikernel support with [Capstan](http://osv.io/capstan/)

1. Build and run your image: `capstan run -f "8080:8080"`

Once the image it built, it's cached.  To delete the image and build a new one:

1. `capstan rmi weather-api; capstan build`


## Links
* [Other Pedestal examples](http://pedestal.io/samples)
