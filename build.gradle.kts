plugins {
	java
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	checkstyle
	application
	jacoco
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

application { mainClass.set("hexlet.code.Application") }

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
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
	testLogging {
		//exceptionFormat = TestExceptionFormat.FULL
		//events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
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