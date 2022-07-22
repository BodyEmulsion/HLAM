plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("java")
}

java.sourceCompatibility = JavaVersion.VERSION_11
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2021.0.3"

dependencies {
	implementation("org.springframework.cloud:spring-cloud-starter-gateway")
	implementation("com.playtika.reactivefeign:feign-reactor-spring-cloud-starter:3.2.3")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("com.auth0:java-jwt:4.0.0")
	compileOnly("org.projectlombok:lombok:1.18.24")
	annotationProcessor("org.projectlombok:lombok:1.18.24")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.test {
	useJUnitPlatform()
}
