package de.polocloud.internalwrapper.utils.properties;

import java.io.File;

public class SpigotExtraProperties extends ServiceProperties {

    public SpigotExtraProperties(File file, int port) {
        super(file, "spigot.yml", port);

        setProperties(new String[]{
            "# This is the main configuration file for Spigot.\n" +
                "# As you can see, there's tons to configure. Some options may impact gameplay, so use\n" +
                "# with caution, and make sure you know what each option does before configuring.\n" +
                "# For a reference for any variable inside this file, check out the Spigot wiki at\n" +
                "# http://www.spigotmc.org/wiki/spigot-configuration/\n" +
                "#\n" +
                "# If you need help with the configuration or have any questions related to Spigot,\n" +
                "# join us at the IRC or drop by our forums and leave a post.\n" +
                "#\n" +
                "# IRC: #spigot @ irc.spi.gt ( http://www.spigotmc.org/pages/irc/ )\n" +
                "# Forums: http://www.spigotmc.org/\n" +
                "\n" +
                "config-version: 8\n" +
                "settings:\n" +
                "  debug: false\n" +
                "  save-user-cache-on-stop-only: false\n" +
                "  sample-count: 12\n" +
                "  player-shuffle: 0\n" +
                "  user-cache-size: 1000\n" +
                "  int-cache-limit: 1024\n" +
                "  late-bind: false\n" +
                "  bungeecord: true\n" +
                "  timeout-time: 60\n" +
                "  restart-on-crash: false\n" +
                "  restart-script: ./restart.sh\n" +
                "  attribute:\n" +
                "    maxHealth:\n" +
                "      max: 2048.0\n" +
                "    movementSpeed:\n" +
                "      max: 2048.0\n" +
                "    attackDamage:\n" +
                "      max: 2048.0\n" +
                "  netty-threads: 4\n" +
                "  filter-creative-items: true\n" +
                "  moved-too-quickly-threshold: 100.0\n" +
                "  moved-wrongly-threshold: 0.0625\n" +
                "messages:\n" +
                "  whitelist: You are not whitelisted on this service!\n" +
                "  unknown-command: Unknown command. Type \"/help\" for help.\n" +
                "  server-full: The service is full!\n" +
                "  outdated-client: Outdated client! Please use {0}\n" +
                "  outdated-server: Outdated service! I'm still on {0}\n" +
                "  restart: Server is restarting\n" +
                "commands:\n" +
                "  tab-complete: 0\n" +
                "  log: true\n" +
                "  replace-commands:\n" +
                "    - setblock\n" +
                "    - summon\n" +
                "    - testforblock\n" +
                "    - tellraw\n" +
                "  spam-exclusions:\n" +
                "    - /skill\n" +
                "  silent-commandblock-console: false\n" +
                "timings:\n" +
                "  enabled: true\n" +
                "  verbose: true\n" +
                "  server-name-privacy: false\n" +
                "  hidden-config-entries:\n" +
                "    - database\n" +
                "    - settings.bungeecord-addresses\n" +
                "  history-interval: 300\n" +
                "  history-length: 3600\n" +
                "stats:\n" +
                "  disable-saving: false\n" +
                "  forced-stats: {}\n" +
                "world-settings:\n" +
                "  default:\n" +
                "    verbose: true\n" +
                "    enable-zombie-pigmen-portal-spawns: true\n" +
                "    arrow-despawn-rate: 1200\n" +
                "    zombie-aggressive-towards-villager: true\n" +
                "    merge-radius:\n" +
                "      item: 2.5\n" +
                "      exp: 3.0\n" +
                "    item-despawn-rate: 6000\n" +
                "    chunks-per-tick: 650\n" +
                "    clear-tick-list: false\n" +
                "    dragon-death-sound-radius: 0\n" +
                "    seed-village: 10387312\n" +
                "    seed-feature: 14357617\n" +
                "    max-entity-collisions: 8\n" +
                "    view-distance: 10\n" +
                "    wither-spawn-sound-radius: 0\n" +
                "    hanging-tick-frequency: 100\n" +
                "    growth:\n" +
                "      cactus-modifier: 100\n" +
                "      cane-modifier: 100\n" +
                "      melon-modifier: 100\n" +
                "      mushroom-modifier: 100\n" +
                "      pumpkin-modifier: 100\n" +
                "      sapling-modifier: 100\n" +
                "      wheat-modifier: 100\n" +
                "      netherwart-modifier: 100\n" +
                "    anti-xray:\n" +
                "      enabled: true\n" +
                "      engine-mode: 1\n" +
                "      hide-blocks:\n" +
                "        - 14\n" +
                "        - 15\n" +
                "        - 16\n" +
                "        - 21\n" +
                "        - 48\n" +
                "        - 49\n" +
                "        - 54\n" +
                "        - 56\n" +
                "        - 73\n" +
                "        - 74\n" +
                "        - 82\n" +
                "        - 129\n" +
                "        - 130\n" +
                "      replace-blocks:\n" +
                "        - 1\n" +
                "        - 5\n" +
                "    mob-spawn-range: 4\n" +
                "    nerf-spawner-mobs: false\n" +
                "    entity-activation-range:\n" +
                "      animals: 32\n" +
                "      monsters: 32\n" +
                "      misc: 16\n" +
                "    hunger:\n" +
                "      walk-exhaustion: 0.2\n" +
                "      sprint-exhaustion: 0.8\n" +
                "      combat-exhaustion: 0.3\n" +
                "      regen-exhaustion: 3.0\n" +
                "    max-tick-time:\n" +
                "      tile: 50\n" +
                "      entity: 50\n" +
                "    max-bulk-chunks: 10\n" +
                "    entity-tracking-range:\n" +
                "      players: 48\n" +
                "      animals: 48\n" +
                "      monsters: 48\n" +
                "      misc: 32\n" +
                "      other: 64\n" +
                "    max-tnt-per-tick: 100\n" +
                "    save-structure-info: true\n" +
                "    ticks-per:\n" +
                "      hopper-transfer: 8\n" +
                "      hopper-check: 8\n" +
                "    hopper-amount: 1\n" +
                "    random-light-updates: false"
        });
        writeFile();
    }

}
