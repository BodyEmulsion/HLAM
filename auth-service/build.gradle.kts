plugins {
	id("application")
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
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-zuul:2.2.10.RELEASE")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign:3.1.3")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.test {
	useJUnitPlatform()
}

application {
	mainClass.set("io.pelt.hlam.auth.AuthService")
}