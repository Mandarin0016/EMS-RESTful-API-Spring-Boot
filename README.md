# Education Management System RESTful API

Education Management System (**EMS**) is
a [learning content management system](https://en.wikipedia.org/wiki/Learning_management_system). It is a software
application that provides a framework that handles some of the key aspects of the learning process. **EMS** is a _platform_ that allows you to create, manage, host, and track digital learning content. The main objective of **EMS** is
to _enhance the learning process_ by saving customers time.

## Installation

- Running the **EMS** _RESTful API_ requires JDK 17. For best performance use Liberica JDK 17,
  download from [here](https://bell-sw.com/pages/downloads/#).
- Apache Maven 3.8.6+, download from [here](https://maven.apache.org/download.cgi).
- Data management is operated by using a relational database management system. **MySQL Community Server**, _version_ **8.0.30** required, downloaded from [here](https://dev.mysql.com/downloads/mysql/).
- Depending on the features you want to use, you may need some third-party software, such
  as [DataGrip](https://www.jetbrains.com/datagrip/download/#section=windows) (payed)
  or [MySQL Workbench](https://dev.mysql.com/downloads/workbench/) (free) for data modeling, SQL development, and
  comprehensive administration for the system data.

## Configuration

- Datasource location, port and name should be changed from ```application.yaml```:

```yaml 
spring.datasource.url: jdbc:mysql://localhost:{port}/{database_name}?createDatabaseIfNotExist=true
```

- MySQL server credentials should be changed from ```application.yaml```:

```yaml 
spring.datasource.username: {username}
spring.datasource.password: {password}
```

- Education Management System is using **SMTP** communication service, provided by Google Workspace, before proceeding
  with the configuration steps bellow, the SMTP server must be configured successfully. Follow the Google support
  article to configure the SMTP server
  from [here](https://support.google.com/a/answer/176600?hl=en#:~:text=Set%20up%20the%20app%20or,re%20using%20TLS%2C%20enter%20587.)
  and a useful GeeksforGeeks article from [here](https://www.geeksforgeeks.org/spring-boot-sending-email-via-smtp/).
  Once the SMTP communication setup finished, place your mail application credentials in ```application.yaml```:

```yaml
environments.mail-properties.test.username: { LDAP_address }
environments.mail-properties.test.password: { application_password }
```

- Default admin account information can be changed from ```application.yaml```:

```yaml
environments.default-headmaster-account.credentials: { email }, { password }
environments.default-headmaster-account.details: { first-name }, { last-name }, { gender }
```

- **JSON** Web Token secret can be configured from ```application.yaml``` or creating system path variable with name _
  JWT_SECRET_:

```yaml
jwt.secret: ${JWT_SECRET:{default_secret}}
```

- Initializing test data can be done by running the **HTTP** collection file
  from ```src/test/resources/InitializeDatabaseCollectionRequest.http```. Before initializing the test data, Java DSL
  configuration for the [SecurityConfig.java](src/main/java/org/modis/EmsApplication/config/SecurityConfig.java) must be
  changed. Follow the steps bellow:

1. Change the value for ```environments.init-data.value``` to **true** within ```application.yaml``` file.
2. Run the application
3. Run <u>**InitializeDatabaseCollectionRequest.http**</u>
4. Stop the application
5. Reverse the changes by setting ```environments.init-data.value``` to **false**.
6. Run the application again.

## Request & Response Examples

### Open Endpoints

Open endpoints require no Authentication.

- [POST /api/auth/login](#post-apiauthlogin)
- [POST /api/auth/resetPassword](#post-apiauthresetpassword)

### Endpoints that require Authentication

Closed endpoints require a valid Token to be included in the header of the request. A Token can be acquired from the
Login view above.

- [POST /api/users](#post-apiusers)
- [GET /api/students/[id]](#get-apistudentsid)
- [POST /api/homework](#post-apihomework)
- [PUT /api/users/[id]](#put-apiusersid)
- [DELETE /api/exams/[id]](#delete-apiexamsid)

### POST /api/auth/login

Example: http://example.edu/api/auth/login <br/>
Request body:

```yaml
{
  "email": "viktorostrov07@gmail.com",
  "password": "admin123"
}
```

Response body:

```yaml
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2aWt0b3Jvc3Ryb3YwN0BnbWFpbC5jb20iLCJleHAiOjE2NjQ4ODk5MTYsImlhdCI6MTY2NDg4NjMxNn0._xMMM6bboqeR-vH0tBfAcZuEdwHMQYZEkovO4UQM5ypkIhLxO0qcUowo-cRc4tflh9jUx5owLeiZKbG_5gHk1w",
  "userEmail": "viktorostrov07@gmail.com"
}
```

### POST /api/auth/resetPassword

Example: http://example.edu/api/auth/resetPassword <br/>
Request body:

```yaml
{
  "email": "viktorostrov07@gmail.com",
  "oldPassword": "wuMzs$5j+9M71p",
  "newPassword": "admin123"
}
```

Response body:

```yaml
{
  "id": 18,
  "firstName": "Viktor",
  "lastName": "Aleksandrov",
  "email": "viktorostrov07@gmail.com",
  "gender": "MALE",
  "role": "HEADMASTER",
  "accountStatus": "ACTIVE"
}
```

### POST /api/users

Example: http://example.edu/api/users <br/>
Request body:

```yaml
{
  "firstName": "Tea",
  "lastName": "Ivanova",
  "email": "teaIvanova01@gmail.com",
  "gender": "FEMALE",
  "role": "STUDENT"
}
```

Response body:

```yaml
{
  "id": 19,
  "firstName": "Tea",
  "lastName": "Ivanova",
  "email": "teaIvanova01@gmail.com",
  "gender": "FEMALE",
  "role": "STUDENT",
  "accountStatus": "PASSWORD_RESET",
  "created_on": "2022-10-04T15:44:43.8594501",
  "last_modified_on": "2022-10-04T15:44:43.8594501"
}
```

### GET /api/students/[id]

Example: http://example.edu/api/students/19 <br/>
Response body:

```yaml
{
  "id": 19,
  "firstName": "Tea",
  "lastName": "Ivanova",
  "email": "teaIvanova01@gmail.com",
  "gender": "FEMALE",
  "role": "STUDENT",
  "accountStatus": "PASSWORD_RESET",
  "created_on": "2022-10-04T15:44:43.8594501",
  "last_modified_on": "2022-10-04T15:44:43.8594501",
  "schoolYear": null,
  "parentsIds": [ ],
  "certificates": [ ],
  "subjects": [ ],
  "grades": { },
  "homework": [ ],
  "competitions": [ ],
  "exams": [ ],
  "timetable": null
}
```

### POST /api/homework

Example: http://example.edu/api/homework <br/>
Request body:

```yaml
{
  "description": "What is Delegation?",
  "subject": "InformationTechnology"
}
```

Response body:

```yaml
{
  "id": 6,
  "description": "What is Delegation?",
  "subject": "InformationTechnology"
}
```

### PUT /api/users/[id]

Example: http://example.edu/api/users/19 <br/>
Request body:

```yaml
{
  "id": 19,
  "firstName": "Dea",
  "lastName": "Smith",
  "email": "dea.smith@gmail.com",
  "gender": "FEMALE"
}
```

Response body:

```yaml
{
  "id": 19,
  "firstName": "Dea",
  "lastName": "Smith",
  "email": "dea.smith@gmail.com",
  "gender": "FEMALE",
  "role": "STUDENT",
  "accountStatus": "PASSWORD_RESET",
  "created_on": "2022-10-04T15:44:43.8594501",
  "last_modified_on": "2022-10-04T16:24:35.684095"
}
```

### DELETE /api/exams/[id]

Example: http://example.edu/api/exams/2 <br/>
Response body:

```yaml
{
  "id": 2,
  "subject": "Literature",
  "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
  "submittedStudentAnswers": {},
  "results": {},
  "created_on": "2022-10-04T15:23:46.618528",
  "last_modified_on": "2022-10-04T15:23:46.727428"
}
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change in the following GitHub repository: https://github.com/Mandarin0016/EMS-RESTful-API-Spring-Boot.git

## License

[Apache License 2.0](LICENSE.md)
