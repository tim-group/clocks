plugins {
    id("java-common-conventions")
    id("java-publishing-conventions")
    id("clocks-conventions")
}

description = "JSR310 clock implementations for use in testing"

clocks {
    javaModuleName.set("com.timgroup.clocks.testing")
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-core:3.0")
    testImplementation("org.hamcrest:hamcrest-library:3.0")
}
