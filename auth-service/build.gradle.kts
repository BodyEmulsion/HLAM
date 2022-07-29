plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("java")
}
java.sourceCompatibility = JavaVersion.VERSION_11
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2021.0.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    implementation("org.postgresql:postgresql:42.4.0")
    implementation("com.auth0:java-jwt:4.0.0")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.7.1")
    implementation("org.springframework.boot:spring-boot-starter-validation:2.7.2")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.2")
    implementation("org.springframework.restdocs:spring-restdocs-mockmvc:2.0.6.RELEASE")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.test {
    useJUnitPlatform()
}

val snippetsPath = "/build/generated-snippets"


tasks {
    test {
        outputs.dir(snippetsPath)
    }
    asciidoctor {
        inputs.dir(snippetsPath)
        sourceDir("/src/docs/asciidocs")
        setOutputDir("/src/docs/")
        dependsOn(test)
    }
}