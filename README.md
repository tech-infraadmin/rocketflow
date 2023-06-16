# RocketFlowTasks

To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.tech-infraadmin:rocketflow:v2.1.33'
	}


Step 3. Initialize

    RocketFlyer.initialize("8yMDA1Lzjg4NTE1MjE2NzUJSQU5OTB9.cZ0nZBfKurDFXma06-s", this) // SDK init token

Step 4. Start skip if in case use dashboard fragment

        RocketFlyer.start("dsrsd-839fsdfa-44e5446-958a-45",true) //ProcessId 

Step 4. Use Dashboard fragment in app 

        TaskDashBoardFragment


