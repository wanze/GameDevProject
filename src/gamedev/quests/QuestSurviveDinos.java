package gamedev.quests;

import gamedev.game.ResourcesManager;
import gamedev.objects.Box;
import gamedev.scenes.GameMapScene;
import android.widget.Toast;

public class QuestSurviveDinos extends Quest {

	protected Box box1;
	protected Box box2;

	public QuestSurviveDinos(GameMapScene map) {
		super(map);
		this.title = "Survive and find something to make the portal work";
		this.description = "Damn, I need to go down here, allthought it looks dangerous...";
		this.box1 = new Box(77 * 32, 47 * 32, map) {
			public void setOpened(boolean opened) {
				if (opened && !this.opened) {
					ResourcesManager.getInstance().activity
							.toastOnUIThread(
									"A key... could be one of the keys for the portal!",
									Toast.LENGTH_LONG);
				}
				super.setOpened(opened);
			}
		};
		this.box2 = new Box(102 * 32, 47 * 32, map) {
			public void setOpened(boolean opened) {
				if (opened && !this.opened) {
					ResourcesManager.getInstance().activity
							.toastOnUIThread(
									"A key... could be one of the keys for the portal!",
									Toast.LENGTH_LONG);
				}
				super.setOpened(opened);
			}
		};
		map.attachChild(box1);
		map.attachChild(box2);
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getStatus() {
		return "I didn't find both keys yet...";
	}

	@Override
	public String statusForQuestScene() {
		int count = 0;
		if (box1.isOpened()) {
			count++;
		}
		if (box2.isOpened()) {
			count++;
		}
		return Integer.toString(count) + "/2" + " keys";
	}

	@Override
	public boolean isCompleted() {
		return (box1.isOpened() && box2.isOpened());
	}

	public void setActive(boolean bool) {
		super.setActive(bool);
		ResourcesManager.getInstance().activity.toastOnUIThread(
				"Damn those dinosaurs are big! But I need to go down here...",
				Toast.LENGTH_LONG);
		ResourcesManager.getInstance().activity.toastOnUIThread(
				"I really should avoid fighting against those beasts!",
				Toast.LENGTH_LONG);
	}

}
