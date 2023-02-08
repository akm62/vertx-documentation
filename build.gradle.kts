import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"

  //swagger-vertx plugin
//  id("org.detoeuf.swagger-codegen") version "1.7.4"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.2.7"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "com.example.swagger3.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-mysql-client")
  implementation("io.vertx:vertx-web-api-contract")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")

  //for swagger
  implementation("org.json:json:20220924")
  implementation("io.swagger.core.v3:swagger-annotations:2.2.8")
  implementation("io.swagger.core.v3:swagger-core:2.2.8")
  implementation("io.swagger.core.v3:swagger-jaxrs2:2.2.8")
  implementation("io.swagger.core.v3:swagger-models:2.2.8")
  implementation("io.vertx:vertx-core:4.3.7")
  implementation("com.google.guava:guava:31.1-jre")
  implementation("org.slf4j:slf4j-api:2.0.6")
  testImplementation("org.slf4j:slf4j-simple:2.0.6")

  //for dependency management
  implementation("io.vertx:vertx-stack-depchain:4.3.7")

}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
