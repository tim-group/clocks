description = "Extension of java.time.Clock to integrate with Joda-Time"
extra["javaModuleName"] = "com.timgroup.clocks.joda"

dependencies {
    api("joda-time:joda-time:2.12.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
}
