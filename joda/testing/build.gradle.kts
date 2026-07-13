plugins {
    id("java-common-conventions")
    id("java-publishing-conventions")
    id("clocks-conventions")
}

description = "Joda-Clock implementations for use in testing"

clocks {
    javaModuleName.set("com.timgroup.clocks.joda.testing")
}

dependencies {
    api("junit:junit:4.13.2")
    api(project(":joda"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
}
