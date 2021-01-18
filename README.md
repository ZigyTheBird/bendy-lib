# bendy-lib
FabricMC library

setup with gradle:

```groovy
repositories{
  (...)
	jcenter()
}
(...)
dependencies {
  (...)
	modImplementation "com.kosmx.bendylib:bendy-lib:${project.bendylib_version}"
	include "com.kosmx.bendylib:bendy-lib:${project.bendylib_version}"

}
```

designed to be able to swap and bend cuboids.

The api provides a way to spaw a cuboid with priorities, to be multi-mod compatible

to swap, you have to create a class from MutableModelPart, and implement the methods.

You don't have to use existing bendebleCuboid objects, you can create your own, BUT it's highly recommend (it's a lot's of work to code a bendable stuff)

The test mod (an older and modified version of [Emotecraft](https://github.com/kosmx/emotes))

Sorry for this not finished documentation...
You can find me on the Fabric discord server and on the Emotecraft discord server