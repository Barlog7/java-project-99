import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	java
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	checkstyle
	id("application")
	jacoco
}


group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

//application { mainClass.set("hexlet.code.AppApplication") }
application {
	mainClass = "hexlet.code.AppApplication"
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.mindrot:jbcrypt:0.4")

	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter")
	compileOnly("org.projectlombok:lombok")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	annotationProcessor("org.projectlombok:lombok")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation(platform("org.junit:junit-bom:5.9.1"))
}

tasks.withType<Test> {
	useJUnitPlatform()
/*	testLogging {
		//exceptionFormat = TestExceptionFormat.FULL
		//events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
		// showStackTraces = true
		// showCauses = true
		showStandardStreams = true
	}*/
	testLogging {
		exceptionFormat = TestExceptionFormat.FULL
		events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
		// showStackTraces = true
		// showCauses = true
		showStandardStreams = true
	}
	finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
	dependsOn(tasks.test)// tests are required to run before generating the report
	reports {
		xml.required = true
	}
}