package de.polocloud.signs.scheduler;

import de.polocloud.signs.SignService;
import de.polocloud.signs.bootstrap.SignBootstrap;
import de.polocloud.signs.config.protection.SignProtection;
import de.polocloud.signs.signs.IGameServerSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class SignProtectionRunnable {

    private final SignProtection signProtection;
    private BukkitTask task;

    public SignProtectionRunnable() {
        this.signProtection = SignService.getInstance().getSignConfig().getSignProtection();
        if(signProtection.isUse()) run();
    }

    public void run(){
        task = Bukkit.getScheduler().runTaskTimer(SignBootstrap.getInstance(), () -> {
            for (IGameServerSign sign : SignService.getInstance().getCache()) {
                Location location = sign.getLocation();
                for (Entity nearbyEntity : location.getWorld().getNearbyEntities(location, signProtection.getDistance(),
                    signProtection.getDistance(), signProtection.getDistance())) {
                    if(nearbyEntity instanceof Player && nearbyEntity.hasPermission(signProtection.getPermission()))
                    nearbyEntity.setVelocity(nearbyEntity.getLocation().toVector().subtract(sign.getLocation().toVector())
                        .normalize().multiply(this.signProtection.getMultiply()).setY(0.2));
                }
            }
        }, 0, signProtection.getScanInterval());
    }

    public SignProtection getSignProtection() {
        return signProtection;
    }

    public BukkitTask getTask() {
        return task;
    }
}
