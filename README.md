<h1 align="center">MVVM Core</h1></br>

<p align="center">
 Base Elements for MVVM (Model-View-ViewModel) Architecture.
</p>
</br>
<p align="center">
  <a href="https://www.gnu.org/licenses/gpl-3.0"><img alt="LICENSE" src="https://img.shields.io/badge/License-GPLv3-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen"/></a>
  <a href="https://jitpack.io/#arduia/mvvm-core"><img alt="Version" src="https://jitpack.io/v/arduia/mvvm-core.svg"/></a> 
</p> <br>
 
## Including in your project   
### Gradle
Add below codes to your **root** `build.gradle` file (not your module build.gradle file).
```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
And add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
	        implementation 'com.github.arduia:mvvm-core:0.0.1'
	}
```
## Usage

### Live Data Encapsulation  
Heare is a basic example of implementing encapsulated MutableLiveData for LiveData subscribers.
```kotlin
class MainViewModel: ViewModel(), LifecycleObserver{ 

    private val _hello = BaseLiveData<String>()
    val hello = _hello.asLiveData()
 
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        _hello.setValue("Hello World from MVVM Core")
    } 
} 
```

# License
```xml
  Copyright 2020 Aung Ye Htet

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
