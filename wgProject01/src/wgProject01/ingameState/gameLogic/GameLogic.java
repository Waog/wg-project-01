package wgProject01.ingameState.gameLogic;

import wgProject01.ingameState.gameLogic.control.SimpleWalkingAiSystem;
import wgProject01.ingameState.gameLogic.utils.EntityFactory;

import com.artemis.World;

public class GameLogic {

	private final static float MAX_SECONDS_PER_UPDATE = 1.0f / 20.0f;

	private World world;

	public void doInit() {
		world = new World();

		world.setSystem(new SimpleWalkingAiSystem());

		EntityFactory.createEnemy(world, 0, 0);

		world.initialize();
	}

	public void doCleanup() {
		world.deleteSystem(world.getSystem(SimpleWalkingAiSystem.class));
	}

	public void doUpdate(float secondsDelta) {
		float leftDeltaToProcess = secondsDelta;

		while (leftDeltaToProcess > 0) {
			float curDeltaToProcess = Math.min(leftDeltaToProcess,
					MAX_SECONDS_PER_UPDATE);
			leftDeltaToProcess -= curDeltaToProcess;

			world.setDelta(curDeltaToProcess);
			world.process();
		}
	}
}
