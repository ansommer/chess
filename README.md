

# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

# Sequence Diagram
Editable - https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YOlAowADWMACy2apIDjBNeLMoYJQw0Bgb3s0zAPluRggajQAHJmMcwCB4jB4uwuP9gaDwWJhDUKsVzPVAk4nDBhuM5upgPZ5vUxgBRKDeOowABCwA4MFuAEdUjkxld0BxMLj-hVWGjuCzxpMoNM5ucDmMNqptlBdotlmsxkcTmdNQLMGxOFKYOLcaJ6rcsjlKAAKDI2v5QDK8tRgACUIuqojNlVk8iUKnU9RpYAAqgM7bL5SgvQHFMo1Ko-UYdLUAGLvTmRygJmA6SwwGM-a63B4wXOwTZIMDIktzGDABDljhFlAAD2yYA0CaDybN5p9KnqVYT3pEKkHlVFLIULbubfaD3Q9I7KmwBAKE5q0-gyEJMGJwXJY0pqmptOWjOZ9QbhmbraL8nu6ENQswfaT6j3FpHMDQHwEAQHdfXFL9g1UWoQHLP4q2jAYE3jbR+x-cVjFqBQOBzRDtFAqdwJQ78oJgu4-gUHw6ztYBKPiJDPyIyDUwwrDOQoutxz-QxSnFY10RZR1u0oDJVCArA+NNHiZ2qSh6hlAZYyWOktWWGi63aCBXzQJTlkuHcxXFAkMHqcISVPe8dP2bU1PiDStMsvSP08bw-H8aB2BpGIMzgelpDgBQYAAGQgLJCiM5gh2oWTGlaDpuh6Ax1HyNByXvNYVTVDgrhuO5HheJKPi+KAfmdAFYExORsUhaE4RgBEkRRSUMSQEFKtufSoHxA9jKPEkyVGM9k0vBZryZaB6nZTkeT5MB304DrUwk6L5KmH5FW1DKdkVK4ltgSKqEtGAEBC947WC0LXRmr18O4-1GOTUNfng+9kMDYjmPTLNOErXD5ALIt7wYt6mKkqpJxQUdfuAG7Uy4+okRQEB7i+LYdgQ1a5nS-RVR2L0corABJNAqGbJAcIxwxARRnH1VMLjfxkllw2mWjoCQAAvFAODXDct0KWc93CoknAARlPc9hrpG9xpgd4SeQTl7zKmBNvVObhRhwjgYesN2PiajaPoiCB3Q9MAHFfhgPXx2NtD9sOmy7PQTXeKallzveESxKNN2Gai6UJgUtaYGUpVHc0t8Q90w0BdBoWYFMpxzKDhUo6s1TaKd7S08c+bnN8AIvBQdAYjiRIi5L87fCwcLYcZ+oGmkelAvpdp6XixK3gKYZw60hbXZNaLe+d3a6-B+pjvsauzpC6vLvdL16a1xNIPqYfCltlNTfqOB4kRx46zkGA6wfTOI7QWWU3lsmy1ymAYAAOQgZhzppTkiaBleTft-9AOAl3pL+1HCzOsbNObc3XCgTcyV+6GW6mAYWYsBoSxpCNMYABNXwQJTgICLFPCABgXAwEwT4JstxYTMBQDQC+k836yzQGsSwWCQDsFqpPAElF1af1QlvH+EMmxnz7kvO62sQxGBQNwcitEDbqXPq9L+aFKgYWkBI34p9ZF903n7A6v8gIgSXgPfi9Qq6US9ggcSvtQazjklcWOcCSgIITmZUYgo85eALv4W4nJ-DYHeI8QK3YYBmx+BoWu+1ooNDNq3eK9gfg90Ec7WOlRdprwSYUUefCJ7dmCZSM62SQnzxyIvYct0ZD3TEevbh71t7wD3kjY+qJmAnwERoku2QYDX2FPjR4j9n5BRCnQj+WirElPqH-fRJTtHRWZjZMBXMeZQL5rAyo8diRIIpENVBdImjcH0IYdh7wXBcOGZklptlz6axEQoqCR18m5PXvInhH1agBJyE2YCMBYmUlMFUkGpyckoHzMgHIACJSD3dnctQZiLHgqmQHL5agRoNHGAigm0gRoi3CMEQI2pNjxFrCgKsikDTamSKAe4RK1okuWAih+PwDQXBgJ0WxjNBbwJMs4lFISkVcrmGijFWKcXLDxQSylCpqVjDJUjMVtJ05jFpfS-YjLmVmDcS5AIHAADsbgnAoFJP4ekwQ4A+QAGzwFgoYAFMAijwLHjUBusUui9ARfE1pKVRgKrmNlcseVXjvEcEVEq-xAQVTBBCDgUJVAULqnIBqqJYWhuxB1LqDjhaknFpsq8DIxoskmlyFAbp+SuOFEksFRizlZ36iMGlPw6XiqVT7WFpzSJyBQACu0cALUAsKZ6S5ZTRE3MqcMpR6Zan7waUfZp69L4dNJl0n198n4vwGVzGAQzym8MATo-h4zQXWMrCA+IcyIG8xgXYlZ7LerrMGlSLZywdl3AMEdEK9CjnFt+d-LdDs0l9s3vDLtPw7SerjB+xRaZaidrIpan4MAax1k+bW0soHN1g23aGH4aLQUpPNVB7tahvYZPCfCjD6L6iYuxSy-2bLU1OKTgNVFpGYDkcCO+-OrlLASOOpsUuLVEgceAhAbjAApCA7wgkwf8FK+4NqHF2oiU0cMTqegusPVnck2AEDAA41AOAEBjpQDWAx71d98pvEKt8OYpUQ0tSxOGyN0b6rInjfxbBrUw04kZimw8xJ03IMzWg6WuaOT5sLbNd9pbsPTtPBprTlBdP6b2AAdRYATNuPRWSBQUHAAA0kqBjAqKPWoyV+-8AArUTaB20ifeHh0LxTwapj-RWi5w7wNjvqYfJpe9mtaRnZ02+FZenLvsIMjeG7tGHV3QYrd0zD3HoWdA7c579w0bWRm29WaH17OfY4Q5xzxsjPHj1xJkzl48MemAdtDHHnVJHZmbMQJsCIyQAAM1QJyK1hYEN8ukMhib-4AWcRKRUOG7Xkaae0-F6AdoYuQ709APGC6rXzifDAZcfc931ytguDklh0erkgYt-mrK46XuPOti8d6xjI5x22NHK5tKseEf265d5sBaD+FdkjN2QZ3atthH6tATklf4YDvC02y1SnqNVtA0LG0ucO-amAIxKO7lJzRxO5JWPuNcl4LTPGEhgD10WRAdxYDAGwBpwgeQCjWrCTNlkjdm6t3br0Yw-dklu1qPLySIv6gcFUX8O0KjJEoB7fV7djWN3w12VAO0POTZ3ZD2o95CBiwDFUIZkJTZvrr17Ad05U3TuGKlzIQPYf8PmKK5YojNjlkrcPJrlxqrhRAA
Presentation
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YOlAowADWMACy2apIDjBNeLMoYJQw0Bgb3s0zAPluRggajQAHJmMcwCB4jB4uwuP9gaDwWJhDUKsVzPVAk4nDBhuM5upgPZ5vUxgBRKDeOowABCwA4MFuAEdUjkxld0BxMLj-hVWGjuCzxpMoNM5ucDmMNqptlBdotlmsxkcTmdNQLMGxOFKYOLcaJ6rcsjlKAAKDI2v5QDK8tRgACUIuqojNlVk8iUKnU9RpYAAqgM7bL5SgvQHFMo1Ko-UYdLUAGLvTmRygJmA6SwwGM-a63B4wXOwTZIMDIktzGDABDljhFlAAD2yYA0CaDybN5p9KnqVYT3pEKkHlVFLIULbubfaD3Q9I7KmwBAKE5q0-gyEJMGJwXJY0pqmptOWjOZ9QbhmbraL8nu6ENQswfaT6j3FpHMDQHwEAQHdfXFL9g1UWoQHLP4q2jAYE3jbR+x-cVjFqBQOBzRDtFAqdwJQ78oJgu4-gUHw6ztYBKPiJDPyIyDUwwrDOQoutxz-QxSnFY10RZR1u0oDJVCArA+NNHiZ2qSh6hlAZYyWOktWWGi63aCBXzQJTlkuHcxXFAkMHqcISVPe8dP2bU1PiDStMsvSP08bw-H8aB2BpGIMzgelpDgBQYAAGQgLJCiM5gh2oWTGlaDpuh6Ax1HyNByXvNYVTVDgrhuO5HheJKPi+KAfmdAFYExORsUhaE4RgBEkRRSUMSQEFKtufSoHxA9jKPEkyVGM9k0vBZryZaB6nZTkeT5MB304DrUwk6L5KmH5FW1DKdkVK4ltgSKqEtGAEBC947WC0LXRmr18O4-1GOTUNfng+9kMDYjmPTLNOErXD5ALIt7wYt6mKkqpJxQUdfuAG7Uy4+okRQEB7i+LYdgQ1a5nS-RVR2L0corABJNAqGbJAcIxwxARRnH1VMLjfxkllw2mWjoCQAAvFAODXDct0KWc93CoknAARlPc9hrpG9xpgd4SeQTl7zKmBNvVObhRhwjgYesN2PiajaPoiCB3Q9MAHFfhgPXx2NtD9sOmy7PQTXeKallzveESxKNN2Gai6UJgUtaYGUpVHc0t8Q90w0BdBoWYFMpxzKDhUo6s1TaKd7S08c+bnN8AIvBQdAYjiRIi5L87fCwcLYcZ+oGmkelAvpdp6XixK3gKYZw60hbXZNaLe+d3a6-B+pjvsauzpC6vLvdL16a1xNIPqYfCltlNTfqOB4kRx46zkGA6wfTOI7QWWU3lsmy1ymAYAAOQgZhzppTkiaBleTft-9AOAl3pL+1HCzOsbNObc3XCgTcyV+6GW6mAYWYsBoSxpCNMYABNXwQJTgICLFPCABgXAwEwT4JstxYTMBQDQC+k836yzQGsSwWCQDsFqpPAElF1af1QlvH+EMmxnz7kvO62sQxGBQNwcitEDbqXPq9L+aFKgYWkBI34p9ZF903n7A6v8gIgSXgPfi9Qq6US9ggcSvtQazjklcWOcCSgIITmZUYgo85eALv4W4nJ-DYHeI8QK3YYBmx+BoWu+1ooNDNq3eK9gfg90Ec7WOlRdprwSYUUefCJ7dmCZSM62SQnzxyIvYct0ZD3TEevbh71t7wD3kjY+qJmAnwERoku2QYDX2FPjR4j9n5BRCnQj+WirElPqH-fRJTtHRWZjZMBXMeZQL5rAyo8diRIIpENVBdImjcH0IYdh7wXBcOGZklptlz6axEQoqCR18m5PXvInhH1agBJyE2YCMBYmUlMFUkGpyckoHzMgHIACJSD3dnctQZiLHgqmQHL5agRoNHGAigm0gRoi3CMEQI2pNjxFrCgKsikDTamSKAe4RK1okuWAih+PwDQXBgJ0WxjNBbwJMs4lFISkVcrmGijFWKcXLDxQSylCpqVjDJUjMVtJ05jFpfS-YjLmVmDcS5AIHAADsbgnAoFJP4ekwQ4A+QAGzwFgoYAFMAijwLHjUBusUui9ARfE1pKVRgKrmNlcseVXjvEcEVEq-xAQVTBBCDgUJVAULqnIBqqJYWhuxB1LqDjhaknFpsq8DIxoskmlyFAbp+SuOFEksFRizlZ36iMGlPw6XiqVT7WFpzSJyBQACu0cALUAsKZ6S5ZTRE3MqcMpR6Zan7waUfZp69L4dNJl0n198n4vwGVzGAQzym8MATo-h4zQXWMrCA+IcyIG8xgXYlZ7LerrMGlSLZywdl3AMEdEK9CjnFt+d-LdDs0l9s3vDLtPw7SerjB+xRaZaidrIpan4MAax1k+bW0soHN1g23aGH4aLQUpPNVB7tahvYZPCfCjD6L6iYuxSy-2bLU1OKTgNVFpGYDkcCO+-OrlLASOOpsUuLVEgceAhAbjAApCA7wgkwf8FK+4NqHF2oiU0cMTqegusPVnck2AEDAA41AOAEBjpQDWAx71d98pvEKt8OYpUQ0tSxOGyN0b6rInjfxbBrUw04kZimw8xJ03IMzWg6WuaOT5sLbNd9pbsPTtPBprTlBdP6b2AAdRYATNuPRWSBQUHAAA0kqBjAqKPWoyV+-8AArUTaB20ifeHh0LxTwapj-RWi5w7wNjvqYfJpe9mtaRnZ02+FZenLvsIMjeG7tGHV3QYrd0zD3HoWdA7c579w0bWRm29WaH17OfY4Q5xzxsjPHj1xJkzl48MemAdtDHHnVJHZmbMQJsCIyQAAM1QJyK1hYEN8ukMhib-4AWcRKRUOG7Xkaae0-F6AdoYuQ709APGC6rXzifDAZcfc931ytguDklh0erkgYt-mrK46XuPOti8d6xjI5x22NHK5tKseEf265d5sBaD+FdkjN2QZ3atthH6tATklf4YDvC02y1SnqNVtA0LG0ucO-amAIxKO7lJzRxO5JWPuNcl4LTPGEhgD10WRAdxYDAGwBpwgeQCjWrCTNlkjdm6t3br0Yw-dklu1qPLySIv6gcFUX8O0KjJEoB7fV7djWN3w12VAO0POTZ3ZD2o95CBiwDFUIZkJTZvrr17Ad05U3TuGKlzIQPYf8PmKK5YojNjlkrcPJrlxqrhRAA



```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
