package wgProject01.ingameState.gameLogic.utils;

import wgProject01.ingameState.gameLogic.model.PositionComponent;
import wgProject01.ingameState.gameLogic.model.WalkingAiComponent;

import com.artemis.Entity;
import com.artemis.World;

public class EntityFactory {
	
	public static Entity createEnemy(World world, float x, float y) {
		Entity e = world.createEntity();
		
		PositionComponent position = new PositionComponent();
		position.x = x;
		position.y = y;
		e.addComponent(position);
		
		WalkingAiComponent walkingAiComponent = new WalkingAiComponent();
		e.addComponent(walkingAiComponent);
		
		e.addToWorld();
		
		return e;
	}
}
