# Illusion

---

Illusion is a rewrite of [FibLib](https://github.com/Haven-King/FibLib). It allows you to lie to clients about BlockStates being
sent to clients based off player predicates. Take this example...

> Prevent a player from finding Iron Ore while they are sneaking.

```java
var illusion = Illusion.create()
        .map(Illusion.from(Blocks.IRON_ORE), Illusion.to(Blocks.STONE))
        .modifyDrops(true)
        .modifyProperties(true)
        .when(Entity::isSneaking)
        .build();

IllusionRegistry.register(illusion);
```

This Illusion will remap Iron Ore to Stone on clients when they are sneaking.
Iron Ore will still exist on the server, but any interactions will act as if the player
interacted with stone - including block breaking speed, block drops, and more.

### For Developers

Illusion is available on the Draylar maven:

```groovy
maven {  
   name = "maven.draylar.dev"  
   url "https://maven.draylar.dev/releases"  
}
```

```groovy
// For Fabric developers:
modImplementation "dev.draylar:illusion-fabric:${project.illusion_version}"

// For Forge developers:
modImplementation "dev.draylar:illusion-forge:${project.illusion_version}"
```
