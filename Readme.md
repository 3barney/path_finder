# RouteTest

RouteTest is a Spring boot application that calculates shortests distance between two countries, it levarages the jgrapht java Library for distance calculations.

## Installation

Clone the project from the repo https://github.com/3barney/path_finder

To start the application run:

### Local

```bash
./mvnw spring-boot:run
```

For tests do run

```bash
```./mvnw test```
```

### Docker
Or build docker image and start container:

```bash
./mvnw package
docker build -t routing_test .
docker run -p 9090:9090 --rm routing_test
```

## Usage
Service provides a single endpoint

```bash
http://localhost:9090/route/KEN/UGA

{
  "route": [
    "KEN",
    "UGA"
  ]
}
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)