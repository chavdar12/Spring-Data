dependencies {
    implementation(platform(libs.spring.boot.bom))
    runtimeOnly(libs.mysql)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.h2)
    testRuntimeOnly(libs.junit.launcher)
}
