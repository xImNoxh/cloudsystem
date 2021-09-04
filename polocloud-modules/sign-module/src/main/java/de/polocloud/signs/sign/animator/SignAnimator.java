package de.polocloud.signs.sign.animator;

import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.scheduler.SchedulerFuture;
import de.polocloud.signs.bootstraps.PluginBootstrap;
import de.polocloud.signs.sign.base.IGameServerSign;
import de.polocloud.signs.sign.enumeration.SignState;
import de.polocloud.signs.sign.layout.Layout;
import de.polocloud.signs.sign.layout.converter.SignConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.stream.Collectors;

public class SignAnimator {

    private SchedulerFuture animateTask;

    public SignAnimator() {
        Scheduler.runtimeScheduler().async().schedule(this::startAnimation, () -> PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig() != null);
    }

    public void startAnimation(){
        //Caching to animate SignStates
        HashMap<SignState, Integer> ticks = new HashMap<>();

        for (SignState signState : PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig().getSignLayouts().keySet()) {
            Layout[] signLayout = PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig().getSignLayouts().get(signState);
            if(signLayout.length > 1){
                ticks.put(signState, 0);
            }
        }


        Scheduler.runtimeScheduler().async().schedule(() -> {
            //Checking if no Player is online
            if(Bukkit.getOnlinePlayers().size() == 0){
                return;
            }

            for (SignState signState : ticks.keySet()) {
                Layout[] signLayout = PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig().getSignLayouts().get(signState);

                //Checking if the last animation state was reached -> setting back to 0
                if(ticks.get(signState) >= (signLayout.length-1)){
                    ticks.replace(signState, 0);
                }else{
                    ticks.replace(signState, ticks.get(signState)+1);
                }

                Layout currentLayout = signLayout[ticks.get(signState)];
                //Getting all Signs of this GameServer
                for (IGameServerSign gameServerSign : PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns().stream().filter(sign -> sign.getSignState().equals(signState)).collect(Collectors.toList())) {
                    if(gameServerSign.getLocation().getWorld().getNearbyEntities(gameServerSign.getLocation(), 7, 7, 7).stream().noneMatch(en -> en instanceof Player)){
                        continue;
                    }
                    String[] content = currentLayout.getLines();
                    for (int i = 0; i < content.length; i++) {
                        gameServerSign.getSign().setLine(i, SignConverter.convertSignLayout(gameServerSign, content[i]));
                    }
                    gameServerSign.getSign().update();
                }
            }
        }, 0L, 15L);
    }

    public void stopAnimation(){
        if(this.animateTask != null){
            this.animateTask.setCancelled(true);
        }
    }

}
