To build and run the server open a shell and move to the root of the project.
Then run the command "gradlew build bootRun"

After the server is up, you can access the following endpoint:
* http://localhost:8080/stores/all To see ALL the stores without an order
* http://localhost:8080/stores/all?sort=city To see all the stores ordered (ASC) by city. Note that some cities start with a '"', these will be displayed at the top.
* http://localhost:8080/stores/all?sort=opendate To see all the stores ordered (DESC) by opening date
* http://localhost:8080/stores/{id} To see the selected store

Into the code you might see the same thing implemented in teo different ways in two different places. I have done it on purpose to show both ways to do it.
