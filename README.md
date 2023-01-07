## Introduction
This is a simple TicTacToe game written in Scala 3 using cats-effect 3. 
This simple application demonstrate how we can use effect system to write pure functional code.
## Local development
1. Run `sbt assembly` to build the project.
2. Go to the deployments/local directory.
3. Execute the following `sudo docker-compose run --rm tictactoe`

## Further Improvements
1. Experiment with capability traits for Console and Random. Check how we can use them to write tests.
2. Experiment with ZIO and see how we can use it to write the same application.
3. Property based testing using ScalaCheck. Can we generate inputs for the game?
4. Add support to even build the Jar using docker - try multi-stage docker build.
5. GraalVM native image support. Experiment with it and any noticable performance improvements.
