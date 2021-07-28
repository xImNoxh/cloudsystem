package de.polocloud.tablist.cache;

import de.polocloud.tablist.config.Tab;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CloudPlayerTabCache extends ConcurrentHashMap<UUID, Tab> {
}
