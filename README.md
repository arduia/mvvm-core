<h1 align="center">MVVM Core</h1>  
<p align="center">
 Base Elements for MVVM (Model-View-ViewModel) Architecture.
</p> 
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
    val hello get() = _hello.asLiveData()
 
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        _hello.setValue("Hello World from MVVM Core")
    } 
} 
```

## Dependencies(Optional)
```gradle

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding true
    }
    
    dependencies {
    
    def fragment_ktx_version = "1.2.5"
    implementation "androidx.fragment:fragment-ktx:$fragment_ktx_version"
    
    }

```

# License
```xml
      Copyright (C) 2020  Aung Ye Htet
  
      This program is free software: you can redistribute it and/or modify
      it under the terms of the GNU General Public License as published by
      the Free Software Foundation, either version 3 of the License, or
      (at your option) any later version.
  
      This program is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU General Public License for more details.
  
      You should have received a copy of the GNU General Public License
      along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
