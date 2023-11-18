### Tecnologias : 
- Spring Boot
- WebFlux
- JWT
- AWS -SDK
- r2dbc

### Backend refactor login
Para poder garantizar el buen funcionamiento, como primer paso correr el siguiente comando en la linea de comandos del proyecto donde se encuentre hubicado : 
```sh
mvn clean install compile package -DskipTests -e -X
```

### Conexion a la base de datos especificando un esquema en Mysql : 
Es importante tener instalado la dependencia para poder usar el R2DBC : 
```sh
    <dependency>
		<groupId>org.postgresql</groupId>
		<artifactId>postgresql</artifactId>
		<scope>runtime</scope>
	</dependency>
	<dependency>
		<groupId>org.postgresql</groupId>
		<artifactId>r2dbc-postgresql</artifactId>
		<scope>runtime</scope>
	</dependency>
```
**Application.properties** :
```sh
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/<NOMBRE_DB>?currentSchema=<ESQUEMA>
spring.r2dbc.username=<USUARIO_DB>
spring.r2dbc.password=<CONTRASEÑA_DB>
```

### Usuario de AWS :
Para poder usar los servicios de SNS, SES con el SDK de AWS es importante tener una llave de acceso y clave de secreta que debe ser descargada en AWS sobre el serivio de Amazon IAM de su cuenta y proporciar en el siguiente codigo :  
```sh
aws.llave.acceso=<LLAVE_ACCESO>
aws.llave.secreta=<LLAVE_SECRETA>
```

### Endpoints :
| Endpoint | Descripcion |
| ------ | ------ |
| /api/v1/registrar | Registrar un usuario en la tabla de usuario |
| /api/v1/login | Iniciar sesion |
| /api/v1/recuperar | Elegir el medio de envio para realiar la recuperacion de contraseña |
| /api/v1/codigo | Genera el código para la verificacion de autenticacion |
| /api/v1/cambiar | Asignar la nueva contraseña una vez se cumpla las condiciones establecidas |

### Configuracion para correr en Docker : 

**dockerfile** : 

```sh
FROM openjdk:17-jdk-alpine
EXPOSE 8080
COPY /target/masiva-0.0.1-SNAPSHOT.jar login-app-refactor.jar
CMD ["java", "-jar", "login-app-refactor.jar"]
```

**docker-compose.yml** : 
```sh
version: '2.18.1'

services:
   postgres:
      image: postgres:12
      container_name: postgres
      
      environment:
         POSTGRES_DB: <NOMBRE_DB>
         POSTGRES_USER: <USUARIO_DB>
         POSTGRES_PASSWORD: <CONTRASEÑA_DB>
      ports:
         - "5432:5432"
      volumes:
         - ./scripts:/docker-entrypoint-initdb.d

   spring-boot-app:
      build:
         context: ./ # Ruta al directorio que contiene el Dockerfile
      image: spring-boot-app
      container_name: spring-boot-app
      ports:
         - "8080:8080"
      depends_on:
         - postgres
      environment:
         SPRING_DATASOURCE_URL: r2dbc:postgresql://postgres:5432/<NOMBRE_DB>?currentSchema=<ESQUEMA>
         SPRING_DATASOURCE_USERNAME: <USUARIO_DB>
         SPRING_DATASOURCE_PASSWORD: <CONTRASEÑA_DB>
         SPRING_JWT_SECRET_KEY: 9aaf7b6e2c692f9e3a36c9d67ec7c609d42c50b93cc14b79f8ff83536c46f31c
         SPRING_HOST_CLIENT: http://localhost:1000
```






