#!/usr/bin/env bash
set -euo pipefail
chmod +x integrate_oyvey.sh

#########################
# 1. Project Configuration
#########################
# Adjust these variables to match your mod’s coordinates
MOD_ID="yourmod"
GROUP_ID="com.yourmod"
VERSION="1.0.0"

# Branch name for backup
BACKUP_BRANCH="pre-oyvey-merge"

# Oyvey repo and temporary folder
OYVEY_REPO="https://github.com/mioclient/oyvey-ported.git"
OYVEY_TMP="oyvey-temp"

# Target package paths
MAIN_PKG_DIR="src/main/java/$(echo $GROUP_ID | tr . /)/oyvey"
CLIENT_PKG_DIR="src/client/java/$(echo $GROUP_ID | tr . /)/oyvey"

#########################
# 2. Backup Current State
#########################
echo "⏳ Creating backup branch ${BACKUP_BRANCH}"
git checkout -B "${BACKUP_BRANCH}"

#########################
# 3. Update gradle.properties
#########################
echo "⏳ Backing up gradle.properties → gradle.properties.bak"
cp gradle.properties gradle.properties.bak

cat > gradle.properties <<EOF
minecraft_version=1.21.7
yarn_mappings=1.21.7+build.3
loader_version=0.20.11
fabric_version=0.85.0
loom_version=1.3.83
java_version=17
EOF
echo "✅ Updated gradle.properties"

#########################
# 4. Update build.gradle
#########################
echo "⏳ Backing up build.gradle → build.gradle.bak"
cp build.gradle build.gradle.bak

cat > build.gradle <<EOF
buildscript {
    repositories { mavenCentral() }
    dependencies { classpath "net.fabricmc:fabric-loom:\$loom_version" }
}

plugins {
    id 'java'
    id 'fabric-loom' version "\$loom_version"
}

group = '${GROUP_ID}'
version = '${VERSION}'
archivesBaseName = '${MOD_ID}'

minecraft {
    version = project.minecraft_version
    mappings = project.yarn_mappings
    loader = project.loader_version
}

repositories {
    mavenCentral()
    maven { url = 'https://maven.fabricmc.net/' }
}

dependencies {
    minecraft "com.mojang:minecraft:\$minecraft_version"
    mappings "net.fabricmc:yarn:\$yarn_mappings@zip"
    modImplementation "net.fabricmc.fabric-api:\$fabric_version"
}

java {
    sourceCompatibility = JavaVersion.toVersion(java_version)
    targetCompatibility = JavaVersion.toVersion(java_version)
}
EOF
echo "✅ Updated build.gradle"

#########################
# 5. Clone & Copy Oyvey
#########################
echo "⏳ Cloning Oyvey-ported into ${OYVEY_TMP}"
rm -rf "${OYVEY_TMP}"
git clone "${OYVEY_REPO}" "${OYVEY_TMP}"

echo "⏳ Copying Java sources"
mkdir -p "${MAIN_PKG_DIR}" "${CLIENT_PKG_DIR}"
cp -R "${OYVEY_TMP}/src/main/java/com/oyvey/"* "${MAIN_PKG_DIR}/"
cp -R "${OYVEY_TMP}/src/client/java/com/oyvey/"* "${CLIENT_PKG_DIR}/"

echo "⏳ Copying mixin configs"
mkdir -p src/main/resources src/client/resources
cp "${OYVEY_TMP}/src/main/resources/modid.mixins.json" "src/main/resources/${MOD_ID}.mixins.json"
cp "${OYVEY_TMP}/src/client/resources/modid.client.mixins.json" "src/client/resources/${MOD_ID}.client.mixins.json"

#########################
# 6. Rename Packages & JSON IDs
#########################
echo "⏳ Renaming packages and mixin IDs"
# Java package renames
find src/main/java src/client/java -type f -name "*.java" \
  -exec sed -i '' -e "s/package com.oyvey/package ${GROUP_ID}.oyvey/g" \
                 -e "s/com.oyvey.mixin/${GROUP_ID}.oyvey.mixin/g" {} +

# Mixin JSON renames
sed -i '' -e "s/com.oyvey.mixin/${GROUP_ID}.oyvey.mixin/g" src/main/resources/${MOD_ID}.mixins.json
sed -i '' -e "s/com.oyvey.mixin/${GROUP_ID}.oyvey.mixin/g" src/client/resources/${MOD_ID}.client.mixins.json

echo "✅ Package and JSON IDs updated"

#########################
# 7. Cleanup
#########################
rm -rf "${OYVEY_TMP}"
echo "🎉 Integration complete! Review changes, then run:\n    ./gradlew --refresh-dependencies runClient"
