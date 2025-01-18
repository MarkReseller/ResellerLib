package lv.reseller.netherwars.logic;

import lv.reseller.netherwars.logic.exceptions.GameException;

public abstract class Mexus {

    public static final int MAX_MEXUS_HEALTH = 10;

    final Team team;
    int health;

    protected Mexus(Team team) {
        this.team = team;
    }

    protected void checkIntact() {
        if(this.health == 0)
            throw new GameException("Mexus health is zero");
    }

    public Team getTeam() {
        return team;
    }

    public int getHealth() {
        return health;
    }

    public boolean isDestroyed() {
        return health == 0;
    }

    public void setHealth(int health) {
        this.health = Math.max(health, 0);
    }

    public void damage(int damage) {
        damage(damage, null);
    }

    public void damage(int damage, Member damager) {
        team.checkInitialized();
        checkIntact();
        boolean byMember = false;
        if(damager != null) {
            damager.checkAlive();
            damager.checkNoTeam(this.team);
            byMember = true;
        }
        setHealth(getHealth() - damage);
        if(isDestroyed()) {
            if(byMember)
                damager.stat.mexusesDestroyed++;
            onDestroy(damage, damager);
        } else {
            if(byMember)
                damager.stat.mexusHitCount++;
            onDamage(damage, damager);
        }
    }

    protected abstract void onDamage(int damage, Member damager);

    protected abstract void onDestroy(int damage, Member damager);

}
