# CircleCI Java 17 Build Error - Diagnosis and Fix

## Issue Description
The CircleCI build is failing with the error:
```
FAILURE: Build failed with an exception.

* Where:
Build file '/home/circleci/project/app/build.gradle' line: 2

* What went wrong:
An exception occurred applying plugin request [id: 'com.android.application']
> Failed to apply plugin 'com.android.internal.application'.
   > Android Gradle plugin requires Java 17 to run. You are currently using Java 11.
      Your current JDK is located in /usr/lib/jvm/java-11-openjdk-amd64
```

## Root Cause Analysis

After analyzing the project configuration, I found the issue:

1. **Android Gradle Plugin Version**: The project uses AGP 8.3.2 which requires Java 17
2. **Project Configuration**: The app is correctly configured to use Java 17
3. **CircleCI Image**: Using old Android machine image tag `2021.10.1` which only has Java 11
4. **Java Requirements**: AGP 8.3.2+ requires Java 17 or higher

### Current Project Configuration (Correct)
```gradle
// build.gradle (root)
classpath "com.android.tools.build:gradle:8.3.2"

// app/build.gradle
compileOptions {
    sourceCompatibility JavaVersion.VERSION_17
    targetCompatibility JavaVersion.VERSION_17
}

kotlinOptions {
    jvmTarget = "17"
}
```

### Current CircleCI Configuration (Problematic)
```yaml
executor:
  name: android/android-machine
  resource-class: large
  tag: 2021.10.1  # ❌ This tag only has Java 11
```

## Solution Options

### Option 1: Update to Latest Android Machine Image (Recommended)
Update your CircleCI configuration to use a newer Android machine image tag that includes Java 17.

```yaml
jobs:
  test:
    executor:
      name: android/android-machine
      resource-class: large
      tag: 2024.11.1  # ✅ Latest tag with Java 17
    steps:
      - checkout
      - run:
          name: Verify Java version
          command: java -version
      # ... rest of your steps
```

### Option 2: Set JAVA_HOME Environment Variable
If the newer image has multiple Java versions installed, explicitly set Java 17 as default.

```yaml
jobs:
  test:
    executor:
      name: android/android-machine
      resource-class: large
      tag: 2024.11.1
    environment:
      JAVA_HOME: /usr/lib/jvm/java-17-openjdk-amd64
    steps:
      - checkout
      - run:
          name: Verify Java version
          command: |
            echo "JAVA_HOME: $JAVA_HOME"
            java -version
      # ... rest of your steps
```

### Option 3: Install Java 17 Runtime (If Needed)
As a fallback, install Java 17 if it's not available in the image.

```yaml
jobs:
  test:
    executor:
      name: android/android-machine
      resource-class: large
      tag: 2024.11.1
    steps:
      - checkout
      - run:
          name: Install Java 17 (if needed)
          command: |
            sudo apt-get update
            sudo apt-get install -y openjdk-17-jdk
            echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> $BASH_ENV
            echo 'export PATH=$JAVA_HOME/bin:$PATH' >> $BASH_ENV
      - run:
          name: Verify Java version
          command: java -version
      # ... rest of your steps
```

### Option 4: Update gradle.properties (Alternative)
Add Java 17 configuration to gradle.properties file.

```properties
# gradle.properties
org.gradle.java.home=/usr/lib/jvm/java-17-openjdk-amd64
```

## Recommended Solution

**I recommend Option 1** - updating to the latest Android machine image tag. This provides:
- ✅ Built-in Java 17 support
- ✅ Latest Android SDK and tools
- ✅ Better security with updated packages
- ✅ Faster builds with optimizations
- ✅ Long-term compatibility

## Implementation

### Step 1: Update CircleCI Configuration
Replace the old tag in your `.circleci/config.yml`:

```yaml
# Before
executor:
  name: android/android-machine
  resource-class: large
  tag: 2021.10.1

# After
executor:
  name: android/android-machine
  resource-class: large
  tag: 2024.11.1
```

### Step 2: Verify Java Version
Add a verification step to ensure Java 17 is being used:

```yaml
- run:
    name: Verify Java version
    command: |
      echo "Java version:"
      java -version
      echo "JAVA_HOME: $JAVA_HOME"
      echo "Gradle version:"
      ./gradlew --version
```

### Step 3: Test the Build
Commit and push the changes to test that the build now works with Java 17.

## Complete Fixed Configuration

Here's your complete updated CircleCI configuration:

```yaml
version: 2.1

orbs:
  android: circleci/android@2.0

workflows:
  test_and_build:
    jobs:
      - test
      - build:
          requires:
            - test

jobs:
  test:
    executor:
      name: android/android-machine
      resource-class: large
      tag: 2024.11.1  # ✅ Updated to latest tag with Java 17
    steps:
      - checkout
      - run:
          name: Verify Java version
          command: |
            echo "Java version:"
            java -version
            echo "JAVA_HOME: $JAVA_HOME"
      - run:
          name: Setup permissions
          command: chmod +x ./gradlew
      - android/restore-gradle-cache
      - run:
          name: Run Unit Tests - MVVM Module
          command: ./gradlew :mvvm:testDebugUnitTest
      - run:
          name: Run Unit Tests - App Module
          command: ./gradlew :app:testDebugUnitTest
      - run:
          name: Generate Test Reports
          command: |
            ./gradlew :mvvm:jacocoTestReport || true
            ./gradlew :app:jacocoTestReport || true
      - store_test_results:
          path: app/build/test-results
      - store_test_results:
          path: mvvm/build/test-results
      - store_artifacts:
          path: app/build/reports
          destination: app-reports
      - store_artifacts:
          path: mvvm/build/reports
          destination: mvvm-reports
      - run:
          name: Save test results summary
          command: |
            echo "=== Unit Test Summary ===" > test-summary.txt
            echo "MVVM Module Tests:" >> test-summary.txt
            find mvvm/build/test-results -name "*.xml" -exec grep -l "testcase" {} \; | wc -l >> test-summary.txt
            echo "App Module Tests:" >> test-summary.txt
            find app/build/test-results -name "*.xml" -exec grep -l "testcase" {} \; | wc -l >> test-summary.txt
            echo "Total Test Files Created:" >> test-summary.txt
            find . -name "*Test.kt" -not -path "./.gradle/*" | wc -l >> test-summary.txt
            cat test-summary.txt
      - store_artifacts:
          path: test-summary.txt
          destination: test-summary.txt
      - android/save-gradle-cache

  build:
    executor:
      name: android/android-machine
      resource-class: large
      tag: 2024.11.1  # ✅ Updated to latest tag with Java 17
    steps:
      - checkout
      - run:
          name: Verify Java version
          command: |
            echo "Java version:"
            java -version
      - run:
          name: Setup permissions
          command: chmod +x ./gradlew
      - android/restore-gradle-cache
      - android/restore-build-cache
      - run:
          name: Build Debug APK
          command: ./gradlew :app:assembleDebug
      - run:
          name: Build MVVM Library
          command: ./gradlew :mvvm:assembleDebug
      - store_artifacts:
          path: app/build/outputs/apk/debug/
          destination: debug-apk
      - store_artifacts:
          path: mvvm/build/outputs/aar/
          destination: mvvm-library
      - run:
          name: Build summary
          command: |
            echo "=== Build Summary ===" > build-summary.txt
            echo "APK Files:" >> build-summary.txt
            find app/build/outputs -name "*.apk" | head -10 >> build-summary.txt
            echo "AAR Files:" >> build-summary.txt
            find mvvm/build/outputs -name "*.aar" | head -10 >> build-summary.txt
            echo "Build completed successfully!" >> build-summary.txt
            cat build-summary.txt
      - store_artifacts:
          path: build-summary.txt
          destination: build-summary.txt
      - android/save-gradle-cache
```

## Additional Notes

### Android Gradle Plugin and Java Compatibility
- **AGP 8.0+**: Requires Java 17 (minimum)
- **AGP 7.4+**: Requires Java 11 (minimum)
- **AGP 7.0-7.3**: Supports Java 8-11

### CircleCI Android Machine Image Tags
- **2024.11.1**: Latest with Java 17, Android SDK 35
- **2024.08.1**: Recent with Java 17 support
- **2021.10.1**: Old tag with Java 11 only (problematic)

### Troubleshooting
If you still encounter Java issues after updating:

1. **Check available Java versions**:
   ```bash
   ls /usr/lib/jvm/
   ```

2. **Switch Java version manually**:
   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
   export PATH=$JAVA_HOME/bin:$PATH
   ```

3. **Verify Gradle is using correct Java**:
   ```bash
   ./gradlew --version
   ```

This fix should resolve the Java 17 requirement issue and allow your Android build to proceed successfully.