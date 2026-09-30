plugins {
    id("java-common-conventions")
    id("java-publishing-conventions")
    id("clocks-conventions")
}

description = "Extension of java.time.Clock to integrate with Joda-Time"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

clocks {
    javaModuleName.set("com.timgroup.clocks.joda")
}

dependencies {
    api("joda-time:joda-time:2.14.4")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
}
