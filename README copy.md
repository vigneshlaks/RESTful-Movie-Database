[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/101Mukwn)
# Homework 1: Basic Client-Server Web App with DynamoDB Integration

In this assignment, you will develop a web application integrating DynamoDB for storage, consisting of a Java loader for
data upload, a REST API server, and a simple React frontend for querying data. You will work with a truncated version of
the [IMDB movie dataset](https://developer.imdb.com/non-commercial-datasets/) (10k rows selected) to store and query
movie data, which is available as a TSV file in the `data` directory.

## Setup

Once you accept the GitHub Classroom assignment, you will have a private repository with the
name `homework-1-ms1-{myid}`. This repository contains the starter code for the assignment. **Change `{myid}` to your
PennKey** in GitHub (Settings -> General -> Repository name).

### Cloning Your Assignment

Click on the green "Code" button and copy the SSH link provided. In the terminal, navigate to your `nets2120` directory
from HW0 and execute `git clone <ssh link> homework-1-ms1-{myid}`. Replace `<ssh link>` with the previously copied SSH
link. This action creates a `homework-1-ms1-{myid}` folder within your `nets2120` directory containing the assignment's
starter code.

### Launching Docker

1. Launch the Docker Desktop application on your machine.
2. Activate the Docker container by either executing `docker start nets2120` from the terminal or clicking the "Start"
   button from the Docker Desktop Containers page.
3. Execute `docker exec -it nets2120 bash` to open a Bash shell within your Docker container.

**Reminder**: When you are done, don't forget to stop the Docker container by executing `docker stop nets2120` from the
terminal or clicking the "Stop" button from the Docker Desktop Containers page. Also, you should frequently commit and
push your changes to your GitHub repository.

## DynamoDB Basics

In this assignment, you'll be using Amazon DynamoDB, a NoSQL database service, to store data. Here are some key
concepts:

- **Primary Key**: Essential for DynamoDB tables, it uniquely identifies each item. There are two types:
    - **Partition Key (Simple Primary Key)**: A single attribute that uniquely identifies an item. DynamoDB uses this
      key to distribute data across partitions.
    - **Composite Primary Key (Partition Key and Sort Key)**: Two attributes where the first is the partition key and
      the second is the sort key, allowing multiple items with the same partition key but different sort keys.
- **Item Storage**: Items are stored in a JSON-like format, with specific data types for keys and values (e.g.,
  String (S) or Number (N)).

For this homework, `tconst` from the dataset will serve as the partition key.

**Note**: It's important to be aware that Amazon provides several versions of the AWS SDK for DynamoDB, each with
significant differences. For this assignment, we specifically utilize the DynamoDBv2 libraries. You'll recognize this in
the code by the package imports prefixed with `com.amazonaws.services.dynamodbv2.*`. Be cautious when searching for
examples or documentation online; if you encounter code samples using `software.amazon.*`, be aware that these refer to
a different version of the SDK and may not be applicable to our project.

### Developing with DynamoDB Local

Begin with [DynamoDB Local](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html), a
local emulation of the DynamoDB service. When using DynamoDB Local, your loader will load data into a table stored on
your computer. It's an efficient way to test your application without incurring AWS charges. Use it to develop and test
your loader and server before transitioning to the AWS DynamoDB service.

#### Initial Setup:

- Ensure [`Config.java`](src/main/java/edu/upenn/cis/nets2120/config/Config.java) is correctly configured:
  - `DYNAMODB_URL` should point to `http://localhost:9000`
  - `LOCAL_DB` should be `true`

#### Launching DynamoDB Local:

1. In the docker terminal, navigate to the `dynamodb_local_latest` folder.
2. Execute: `java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -port 9000`.
3. Open a new terminal tab and start another docker terminal. Your local DynamoDB instance is now running.

#### Resetting the Local Database:

- To clear all tables, delete the `shared-local-instance.db` file in the `dynamodb_local_latest` folder.

### Transitioning to AWS DynamoDB

After confirming your loader works locally, proceed to connect it to AWS DynamoDB for broader testing.

#### Connecting to AWS:

1. [Launch AWS Learner's Lab](https://awsacademy.instructure.com/courses/66654/modules/items/5910588) by clicking on "
   Start Lab. Wait for a minute for the lab to start.
2. When the status turns green on the top left, click on "AWS Details," then "Show" next to "AWS CLI." Copy the AWS CLI
   configuration text.
3. In Docker, run:
    * `mkdir ~/.aws`
    * `touch ~/.aws/credentials`
    * `echo "[copied config]" > ~/.aws/credentials`

**Note**: Each session lasts for 4 hours max, and credentials must be re-entered each session in Learner's Lab.

#### AWS Configuration:

- Adjust [`Config.java`](src/main/java/edu/upenn/cis/nets2120/config/Config.java) for AWS:
  - `DYNAMODB_URL` should point to `https://dynamodb.us-east-1.amazonaws.com`
  - `LOCAL_DB` should be `false`

#### DynamoDB Table Setup:

1. In Learner's Lab, access the AWS console via the "AWS" button on the top left.
2. Go to the DynamoDB section and create a new table with `tconst` as the primary key.
3. In advanced settings, configure read and write autoscaling bounds between 1 and 100.

#### Cleaning Up:

When you are done with a table, make sure to delete it to avoid incurring charges. Also, remember to click on "End Lab"
when you're done with an AWS session.

## Overview of REST APIs

REST (Representational State Transfer) APIs are frameworks that govern the creation and interaction with web services,
enabling software applications to communicate across the internet through HTTP.

### Core Principles of REST APIs

- **Statelessness**: Each client request must carry all necessary information for its execution, implying that the
  server does not retain any client session information between requests.
- **Client-Server Architecture**: This principle decouples the user interface concerns (handled by the client) from the
  data storage concerns (managed by the server), promoting the separation of responsibilities.
- **Layered System**: In a REST API, the client interacts with the network without knowing whether it is communicating
  directly with the end server or through intermediaries.

For this assignment, you will leverage Java Spark, an efficient micro-framework for developing web applications. The
server you develop will run on port `4567` and be accessible locally through docker container's port forwarding.

## Milestone 1: DynamoDB Loader

**Deadline: Wednesday, 02/07/2024 @11:59 PM**

### Objective

Create a loader to upload the (truncated) IMDB movie dataset into DynamoDB and develop a simple REST server in Java
Spring for field-based table queries.

### Code Structure

#### Loader

- [`IndexMovies.java`](src/main/java/edu/upenn/cis/nets2120/hw1/IndexMovies.java): Implements a handler for initializing
  DynamoDB tables and converting TSV rows to DynamoDB items.
- [`LoadData.java`](src/main/java/edu/upenn/cis/nets2120/hw1/LoadData.java): Manages setup and connects the handler to
  the TSV reader.
- [`Parser.java`](src/main/java/edu/upenn/cis/nets2120/hw1/files/Parser.java): Parses TSV files for data extraction.

#### Server

- [`RestServer.java`](src/main/java/edu/upenn/cis/nets2120/hw1/RestServer.java): Java application using Spark framework
  to enable RESTful interactions with DynamoDB.

#### Testing

- [`SampleLoaderTest.java`](src/test/java/SampleLoaderTest.java): Contains JUnit tests for DynamoDB querying.

#### Configuration

- [`Config.java`](src/main/java/edu/upenn/cis/nets2120/config/Config.java): Configures local and remote DynamoDB
  connections. Ensure `IS_AUTOGRADER` is `false` to avoid high throughput costs.

### Running the Code

All commands should be run inside the Docker container shell and from the root directory of the project.

- Compile: `mvn compile`
- Launch Loader: `mvn exec:java@loader`
- Start Server: `mvn exec:java@server`
- Run Tests: `mvn test`
- Clean Project: `mvn clean`

If your environment is configured correctly, the starter code should compile without errors. The loader should start and
shut down without actually loading anything. The server should start on port `4567`, and when you navigate
to `localhost:4567` in your local browser, and you should be greeted with a "Hello world!" message.

### Implementing the Loader

Your DynamoDB table should adhere to the following schema:

* `tconst (string)`: Alphanumeric unique identifier of the title, serving as the partition key.
* `titleType (string)`: The format/type of the title (e.g., movie, short, tvseries, tvepisode, video, etc.).
* `primaryTitle (string)`: The commonly recognized title or the one used for promotional materials at release.
* `originalTitle (string)`: The title in its original language.
* `isAdult (boolean)`: Indicator of adult content (0 for non-adult titles; 1 for adult titles).
* `startYear (N)`: The release year of the title, or the start year for TV series.
* `endYear (N)`: The end year for TV series; represented as `\N` for all other title types.
* `runtimeMinutes (N)`: The primary runtime of the title, in minutes.
* `genres (string array)`: Up to three genres associated with the title.

To implement the loader, you should complete the `accept()` function
in [IndexMovies.java](src/main/java/edu/upenn/cis/nets2120/hw1/IndexMovies.java). This function is crucial for loading
data into DynamoDB. It receives two parameters:

- A string array containing column names.
- A string array containing corresponding column values, parsed from the [`title.basics.tsv`](data/title.basics.tsv)
  file.

Your task is to transform this data into a
DynamoDB [`Item`]((https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/dynamodbv2/document/Item.html))
that aligns with the above schema. Then, use the `moviesTable` object to upload the items into your DynamoDB table.

**Hint**: the `moviesTable` object has a method
called [`putItem`](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/JavaDocumentAPIItemCRUD.html) that
accepts an `Item` object.

### Implementing the Server

In the [`RestServer.java`](src/main/java/edu/upenn/cis/nets2120/hw1/RestServer.java) file, you will develop three REST
API endpoints to facilitate queries based on title (`primaryTitle`), start/end year range, and genres against a DynamoDB
table. The response should be in JSON format, encapsulating a list of entries where each entry includes all stored
fields: `primaryTitle`, `titleType`, `tconst`, `originalTitle`, `genres`, `startYear`, `endYear`, and `isAdult`.

#### API Endpoint Specifications:

- **Title Query** (`/query/title/{value}`): Performs a case-sensitive search by title using substring matching. For
  instance, a query for "casa" will not match "Casablanca", but "Casa" will.
- **Year Range Query** (`/query/years?start={startYear}&end={endYear}`): Filters entries based on the start and
  optionally the end year. If both parameters are provided, entries with startYear >= start and endYear <= end are
  returned. If only the start year is specified, entries with startYear equal to the given year are returned.
- **Genres Query** (`/query/genres?value={genre1,genre2}`): Returns entries that match *all* specified genres. For
  example, if the query is `/query/genres?value=Animation,Musical`, then an entry with
  genres `["Animation", "Musical", "Short"]` will match the query, while one with only `["Animation"]` will not.

**Hint**: Given that the table does not index these fields, you will need to employ a scan operation to execute these
queries. For guidance on setting up scan operations, refer to
the [AWS Documentation on Scan Operations](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/ScanJavaDocumentAPI.html).

**Sample Output**: The expected output for a sample genres query (`/query/genres?value=Animation,Musical`) can be found
in the [`sampleQueryOutcome.json`](sampleQueryOutcome.json) file.

### Testing Your Application

#### Loader Testing with JUnit

We have included a set of sample JUnit 4 tests in [SampleLoaderTest.java](src/test/java/SampleLoaderTest.java). Note
that
while these tests cover fundamental scenarios, they are not exhaustive. Writing JUnit tests is an excellent way to test
your loader functionality, and it's also the way that our autograder will assess your loader. Using this tool is highly
recommended but completely optional.

To run the tests, execute the `mvn test` command in the Docker container shell. This command triggers Maven to run Java
tests located in the `src/test/java` directory of your project.

##### About JUnit

JUnit is a popular testing framework in Java. A unit test typically aims to test a small "unit" of your code in
isolation from the rest of the program. This helps ensure that each part of your code works correctly as intended. In
our case, we will be adapting these unit tests to validate the DynamoDB table populated by your loader. This involves
querying the database by field and checking the results against expected values. JUnit's annotations and assertions
provide a structured way to create these tests and verify the outcomes. For this class, we are using JUnit 4.

Each function marked with `@Test` is an individual unit test. Within each unit test, you can include several assertions
of the data returned from querying the DynamoDB table. Common assertions include:

- `assertEquals(expected, actual)`: Checks if two values are equal.
- `assertTrue(condition)`: Checks if a given condition is true.
- `assertFalse(condition)`: Checks if a given condition is false.
- `assertNotNull(object)`: Checks if an object is not null.

If any of these assertions fail, the unit test fails. When you run `mvn test`, any failed unit tests will show up and
the exact assertion that failed will also be identified.

If you are looking for a more comprehensive guide, we recommend you check out
the [CIS 1210 JUnit 4 Testing Guide](https://www.cis.upenn.edu/~cis1210/current/testing_guide.html).

#### Server Testing

**HTML Form**: An HTML form is provided for basic testing of API endpoints by title, year, and genre. This form allows
for quick, manual verification of your RESTful services.

**Postman Recommendation**: For more comprehensive testing of your REST API, consider
using [Postman](https://www.postman.com/). Postman is a powerful tool for executing and automating tests of API
endpoints, allowing you to simulate client requests and inspect responses in detail.

### Extra Credit: Implementing Batch Loading (+5 points)

As an extra credit feature, we ask you to enhance your data loader by integrating batch loading functionality. This
optimization allows for uploading multiple entries in a single HTTP request, which is significantly more efficient than
the current approach of uploading items one by one.

#### Task Overview

Implement batch loading using
the [`batchWriteItem`](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/batch-operation-document-api-java.html)
method, setting the batch size to **25 items**. This method enables you to send up to 25 item put or delete requests in
a single call, reducing the number of HTTP requests needed to upload your data.

#### Requirements

- **Batch Operation**: Modify your loader to accumulate data entries and perform batch uploads using
  the `batchWriteItem` method. Each batch should consist of up to 25 items.
- **Autograder Compatibility**: The autograder will verify the use of `batchWriteItem()` instead of `table.putItem()`.
  Failing to use the batch method will result in not receiving extra credit.

#### Hints for Implementation

1. **Buffering Entries**: The existing `accept()` function in `IndexMovies.java` processes entries individually.
   Consider implementing a mechanism to buffer these entries until you have enough (25) to execute a batch upload.
2. **Handling Remaining Entries**: After processing all dataset entries, there might be items left in your buffer that
   do not fill an entire batch. Ensure these remaining entries are uploaded to DynamoDB to avoid data loss.
3. **Codebase Adjustments**: You might need to explore and possibly adjust parts of the codebase not explicitly marked
   as TODOs to implement efficient batch processing fully.

## Notes of Caution

- Do not modify functions not marked as `TODO` (except when implementing the extra credit). Feel free, however, to write
  your own helper functions within the code.
- Always set `IS_AUTOGRADER = false` in [`Config.java`](src/main/java/edu/upenn/cis/nets2120/config/Config.java). This
  will create a table with extremely high read/write throughput and may burn through your AWS credits.

## Submission

Submit via [Gradescope](https://www.gradescope.com/courses/677230) by uploading from GitHub. You may need to link your
GitHub account if prompted.

- Don't forget to fill out the [reflection questions](feedback.md) before you submit; this file is graded for
  completion.
