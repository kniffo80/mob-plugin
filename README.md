# Nukkit Mob Plugin
Development: **[kniffo80](https://github.com/kniffo80)**
             **[matt404](https://github.com/matt404)**

MobPlugin is a plugin that implements the mob entities for MCPE including movement, aggression etc.

## Notice
#### Welcome Github issue!
This plug-in is in development. Therefore, It is possible to function abnormally.

[![Discord](https://discordapp.com/api/guilds/331619998059593738/widget.png)](https://scarsz.me/discord)

# Credits
Credits go to Team-SW! They have a nice plugin already made. I used it and adapt it for 0.16.

# Plugin Example configuration
Place this plugin jar file to your Nukkit's home directory "${NUKKIT_HOME}/plugin".
#### Example:
  /usr/share/nukkit/plugins/MobPlugin-0.0.2-SNAPSHOT.jar

Then you have to create a folder in plugin folder with the name of the plugin and place the config.yml there ("${NUKKIT_HOME}/plugin/MobPlugin").
#### Example:
  /usr/share/nukkit/plugins/MobPlugin/config.yml

When Nukkit Server starts up and the plugin is activated, the config.yml is read and evaluated by the plugin.

## config.yml example

The following configuration sets mobs AI enabled and the auto spawn task will be triggered all 300 ticks.
It's configured to spawn only wolfes:

```yaml
entities:
  mob-ai: true
  auto-spawn-tick: 300
  worlds-spawn-disabled: 

max-spawns:
  bat: 0
  blaze: 0
  cave-spider: 0
  chicken: 0
  cow: 0
  creeper: 0
  donkey: 0
  enderman: 0
  ghast: 0
  horse: 0
  iron-golem: 0
  mooshroom: 0
  mule: 0
  ocelot: 0
  pig: 0
  pig-zombie: 0
  rabbit: 0
  silverfish: 0
  sheep: 0
  skeleton: 0
  skeleton-horse: 0
  snow-golem: 0
  spider: 0
  squid: 0
  witch: 0
  wolf: 1
  zombie: 0
  zombie-horse: 0
  zombie-villager: 0
```
