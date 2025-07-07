# CircleCI Android Executor Error - Diagnosis and Fix

## Issue Description
The CircleCI workflow is failing with the error:
```
Error calling workflow: 'test_and_build'
Error calling job: 'test'
Cannot find a definition for executor named android/android
```

## Root Cause Analysis

After analyzing the CircleCI configuration in `.circleci/config.yml`, I found the issue:

1. **Orb Version**: The configuration uses `android: circleci/android@2.0`
2. **Executor Issue**: The jobs are trying to use `executor: android/android`
3. **Problem**: The `android/android` executor doesn't exist in version 2.0 of the CircleCI Android orb

## Current Configuration (Problematic)
```yaml
version: 2.1

orbs:
  android: circleci/android@2.0

jobs:
  test:
    executor: android/android  # ❌ This executor doesn't exist
    steps:
      - checkout
      # ... other steps
  
  build:
    executor: android/android  # ❌ This executor doesn't exist
    steps:
      - checkout
      # ... other steps
```

## Solution

In CircleCI Android orb version 2.0, the correct executor names are:
- `android/android-machine` - For machine executor (supports Android emulators)
- `android/android-docker` - For Docker executor (faster, no emulator support)

## Fixed Configuration

### Option 1: Using Machine Executor (Recommended for Android projects)
```yaml
version: 2.1

orbs:
  android: circleci/android@2.0

jobs:
  test:
    executor:
      name: android/android-machine
      resource-class: large
      tag: 2021.10.1
    steps:
      - checkout
      - run:
          name: Setup permissions
          command: chmod +x ./gradlew
      - android/restore-gradle-cache
      - run:
          name: Run Unit Tests - MVVM Module
          command: ./gradlew :mvvm:testDebugUnitTest
      # ... rest of steps

  build:
    executor:
      name: android/android-machine
      resource-class: large
      tag: 2021.10.1
    steps:
      - checkout
      - run:
          name: Setup permissions
          command: chmod +x ./gradlew
      - android/restore-gradle-cache
      - run:
          name: Build Debug APK
          command: ./gradlew :app:assembleDebug
      # ... rest of steps
```

### Option 2: Using Docker Executor (For simpler builds)
```yaml
version: 2.1

orbs:
  android: circleci/android@2.0

jobs:
  test:
    executor:
      name: android/android-docker
      tag: 2024.11.1
    steps:
      - checkout
      - run:
          name: Setup permissions
          command: chmod +x ./gradlew
      - android/restore-gradle-cache
      - run:
          name: Run Unit Tests - MVVM Module
          command: ./gradlew :mvvm:testDebugUnitTest
      # ... rest of steps

  build:
    executor:
      name: android/android-docker
      tag: 2024.11.1
    steps:
      - checkout
      - run:
          name: Setup permissions
          command: chmod +x ./gradlew
      - android/restore-gradle-cache
      - run:
          name: Build Debug APK
          command: ./gradlew :app:assembleDebug
      # ... rest of steps
```

## Specific Changes Needed

1. **Replace all instances of**:
   ```yaml
   executor: android/android
   ```
   
2. **With either**:
   ```yaml
   executor:
     name: android/android-machine
     resource-class: large
     tag: 2021.10.1
   ```
   
   **Or**:
   ```yaml
   executor:
     name: android/android-docker
     tag: 2024.11.1
   ```

## Recommendations

1. **Use `android/android-machine`** if you need:
   - Android emulator support
   - Better performance for complex builds
   - Support for UI testing

2. **Use `android/android-docker`** if you need:
   - Faster startup times
   - Simple unit testing
   - Basic building without emulators

3. **Consider upgrading the orb version** to a newer version (e.g., `circleci/android@2.4.0`) for better features and support.

## Additional Notes

- The `tag` parameter specifies the image version to use
- The `resource-class` parameter controls the computing resources (small, medium, large, xlarge)
- For Android projects with emulators, `large` or `xlarge` resource class is recommended

## Example Working Configuration

Based on your current setup, here's a complete working configuration:

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
      tag: 2021.10.1
    steps:
      - checkout
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
      - android/save-gradle-cache

  build:
    executor:
      name: android/android-machine
      resource-class: large
      tag: 2021.10.1
    steps:
      - checkout
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
      - android/save-gradle-cache
```

This configuration should resolve the executor error and allow your Android build pipeline to run successfully.