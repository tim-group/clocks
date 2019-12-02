description = "Extension of java.time.Clock to integrate with Joda-Time"
extra["javaModuleName"] = "com.timgroup.clocks.joda"

dependencies {
    api("joda-time:joda-time:2.10.4")
    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest-core:1.3")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
}
