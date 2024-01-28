# Simple java 17 lambda

- handler:
  - br.com.rafaellino.handler.LambdaHandler::handleRequest

- Currently using API gateway as a trigger

- Comparison:
  - 17.0.8-graalce
    - cold start: 
      - REPORT RequestId: - Duration: 17280.16 ms	Billed Duration: 17281 ms Memory Size: 256 MB Max Memory Used: 169 MB Init Duration: 844.10 ms
    - warm start: |average ms 248.54/ max of 256 MB usage/ in 6 req|
      - REPORT RequestId: - Duration: 284.19 ms Billed Duration: 285 ms Memory Size: 256 MB Max Memory Used: 170 MB
