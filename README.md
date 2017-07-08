# DefinitelyScala phaser.io tutorial using Akka HTTP with Scala.js

This is a simple example application showing how you can integrate an Akka HTTP project with a Scala.js project using [DefinitelyScala](https://definitelyscala.com/) [phaser.io](https://github.com/DefinitelyScala/scala-js-phaser) facade.

It implements the tutorial [Making your first Phaser game](http://phaser.io/tutorials/making-your-first-phaser-game) from [phaser.io official website](http://phaser.io/tutorials/making-your-first-phaser-game)

The application contains three directories:
* `server` Akka HTTP application (server side)
* `client` Scala.js application (client side)
* `shared` Scala code that you want to share between the server and the client

## Run the application
```shell
$ sbt ~re-start
$ open http://0.0.0.0:8081
```

## Control the dude
```
→ move right
← move left
SPACE jump
```

## Screenshot
![Alt text](screenshots/screenshot.png?raw=true "tutorial screenshot")

## Features

The application uses the [sbt-web-scalajs](https://github.com/vmunier/sbt-web-scalajs) sbt plugin and the [scalajs-scripts](https://github.com/vmunier/scalajs-scripts) library.

- `compile`, `run`, `re-start` trigger the Scala.js fastOptJS command
- `~compile`, `~run`, `~re-start` continuous compilation is also available
- Production archives (e.g. using `assembly`, `universal:packageBin`) contain the optimised javascript
- Source maps
  - Open your browser dev tool to set breakpoints or to see the guilty line of code when an exception is thrown
  - Source Maps is _disabled in production_ by default to prevent your users from seeing the source files. But it can easily be enabled in production too by setting `emitSourceMaps in fullOptJS := true` in the Scala.js projects.

## IDE integration

### IntelliJ

In IntelliJ, open Project wizard, select `Import Project`, choose the root folder and click `OK`.
Select `Import project from external model` option, choose `SBT project` and click `Next`. Select additional import options and click `Finish`.
Make sure you use the IntelliJ Scala Plugin v1.3.3 or higher. There are known issues with prior versions of the plugin.

## Classpath during development

The assets (js files, sourcemaps, etc.) are added to the classpath during development thanks to the following lines:
```
WebKeys.packagePrefix in Assets := "public/",
managedClasspath in Runtime += (packageBin in Assets).value
```

Note that `packageBin in Assets` also executes any tasks appended to `pipelineStages`, e.g. `gzip`.
You may want to avoid executing tasks under `pipelineStages` during development, because it could take long to execute.

In that case, in order to still have access to the assets under `WebKeys.packagePrefix in Assets` during development, you can use the following code instead:
```
lazy val server = (project in file("server")).settings(
...
WebKeys.packagePrefix in Assets := "public/",
WebKeys.exportedMappings in Assets ++= (for ((file, path) <- (mappings in Assets).value)
  yield file -> ((WebKeys.packagePrefix in Assets).value + path)),
...
)
```
