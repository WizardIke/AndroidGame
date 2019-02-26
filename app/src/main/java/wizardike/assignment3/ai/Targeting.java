package wizardike.assignment3.ai;

import wizardike.assignment3.category.Category;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;

public class Targeting {
    public static class Target {
        public int entity;
        public float displacementX, displacementY;
        public float distance;
        public float directionX, directionY;
    }
    public static Target findClosestTarget(Vector2 positionToSearchFrom, Faction faction,
                                           int[] otherCreatures, int otherCreaturesCount, Level level) {
        float x = positionToSearchFrom.getX();
        float y = positionToSearchFrom.getY();
        float min = Float.MAX_VALUE;
        int target = Integer.MAX_VALUE;
        float distanceX = 0, distanceY = 0;
        for (int i = 0; i != otherCreaturesCount; ++i) {
            int otherCreature = otherCreatures[i];
            Integer otherCategory = level.getCategorySystem().getCategory(otherCreature);
            if(otherCategory == null) continue;
            Faction otherFaction = level.getFactionSystem().getFaction(otherCreature);
            if(Category.Creature == (int)otherCategory && faction.isEnemy(otherFaction)) {
                Vector2 enemyCreaturePosition = level.getPositionSystem().getPosition(otherCreature);
                distanceX = enemyCreaturePosition.getX() - x;
                distanceY = enemyCreaturePosition.getY() - y;
                float distance = distanceX * distanceX + distanceY * distanceY;
                if(distance < min) {
                    min = distance;
                    target = otherCreature;
                }
            }
        }
        if(target != Integer.MAX_VALUE) {
            Target ret = new Target();
            ret.entity = target;
            ret.displacementX = distanceX;
            ret.displacementY = distanceY;
            ret.distance = (float)Math.sqrt(min);
            ret.directionX = distanceX / ret.distance;
            ret.directionY = distanceY / ret.distance;
            return ret;
        } else {
            return null;
        }
    }
}
