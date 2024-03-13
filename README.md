# RESTful-Movie-Database

Welcome to my Movie Database Web Application project! In this project, I've developed a web application that integrates DynamoDB for storage, allowing users to query movie data through a REST API server and a simple React frontend. My goal was to create a user-friendly platform for exploring a truncated version of the IMDB movie dataset.

## Project Overview

My project consists of several components:

1. **Data Loader**: A Java application responsible for uploading movie data into DynamoDB.
2. **REST API Server**: Implemented in Java Spring, this server enables field-based queries on the DynamoDB table.
3. **React Frontend**: Provides a user interface for interacting with the movie database, allowing users to perform searches and view results.

## Setup

To get started with my project, follow these steps:

1. Clone the repository to your local machine.
2. Launch Docker Desktop and start the container using provided commands.
3. Follow the instructions in the repository's README file to configure DynamoDB, compile the code, and start the server.

## DynamoDB Integration

I've utilized Amazon DynamoDB, a NoSQL database service, for storing my movie data. DynamoDB offers fast and flexible data storage with seamless scalability. Understanding key concepts such as Primary Key and Item Storage was crucial for designing my database schema and implementing data loading functionality.

## RESTful API Development

My REST API server, built with Java Spark, adheres to the principles of Representational State Transfer (REST). It provides endpoints for querying movie data based on title, year range, and genres. I ensured that my API follows best practices for RESTful interactions, promoting a clear separation of concerns between client and server.

## What I Learned

Throughout this project, I gained valuable experience in:

- Working with DynamoDB and understanding its key concepts.
- Developing RESTful APIs using Java Spring and Java Spark frameworks.
- Integrating frontend and backend components to create a cohesive web application.
- Writing clean and maintainable code, following best practices and design patterns.

## Next Steps

While my project currently provides basic functionality for querying movie data, there are several potential enhancements I could explore in the future:

- Implementing advanced search features, such as full-text search or filtering by additional criteria.
- Enhancing the frontend with interactive visualizations or user customization options.
- Optimizing performance and scalability of my application, particularly in handling large datasets.
- Adding user authentication and authorization for secure access to the database.

## Conclusion

My Movie Database Web Application project demonstrates the power of integrating DynamoDB into a web application to provide users with seamless access to a vast collection of movie data. I'm excited about the possibilities for further development and improvement, and I hope you enjoy exploring the world of movies through my platform!
