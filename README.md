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

