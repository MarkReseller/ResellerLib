package lv.reseller.netherwars.decoration.task;

import lv.reseller.netherwars.decoration.DecoratedGame;
import lv.reseller.netherwars.decoration.DecoratedMember;
import lv.reseller.netherwars.logic.Member;
import lv.reseller.netherwars.util.Chat;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class RespawnerTask extends GameTask {

    public RespawnerTask(DecoratedGame game) {
        super(game, 20, 20);
    }

    @Override
    protected void tick() {
        for(Member member : getGame().getMembers().values()) {
            DecoratedMember decoratedMember = (DecoratedMember) member;
            Player player = decoratedMember.getPlayer();
            if(decoratedMember.isRespawning()) {
                if(decoratedMember.getRespawnTime() != 0) {
                    decoratedMember.setRespawnTime(decoratedMember.getRespawnTime() - 1);
                    player.sendTitle(Title.builder().title(Chat.colorize("&c&l" + decoratedMember.getRespawnTime())).stay(2).build());
                } else {
                    decoratedMember.respawn();
                    player.resetTitle();
                }
            }
        }
    }
}
