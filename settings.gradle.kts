plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "Spring-Data"

include(
    "001-Database-Access-With-Jdbc",
    "002-Exersice-Database-Access-With-Jdbc",
    "003-Spring-Data-Intro",
    "004-Exercise-Spring-Data-Intro",
    "005-Spring-Data-Advanced-Quering",
    "006-Exersice-Spring-Data-Advanced-Quering",
    "007-Spring-Data-Auto-Mapping-Objects",
    "008-Exersice-Spring-Data-Auto-Mapping-Objects",
    "009-Json-Processing",
    "010-Exercise-Json-Processing",
    "011-Xml-Processing",
    "012-Exercise-Xml-Processing",
)
