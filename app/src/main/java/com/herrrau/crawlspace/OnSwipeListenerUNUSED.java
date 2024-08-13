package com.herrrau.crawlspace;

import android.graphics.Color;
import android.view.MotionEvent;

public class OnSwipeListenerUNUSED extends OnSwipeListener {
    ActivityGame activityGame;

    public OnSwipeListenerUNUSED(ActivityGame activityGame) {
        super();
        this.activityGame = activityGame;
    }

    @Override
    public boolean onSwipe(Direction direction) {
        System.out.println("SWIPE");
        if (activityGame.state == 0) {
            System.out.println(" State: " + activityGame.state);
            if (direction == Direction.up) activityGame.localController.tryMoveNorth();
            if (direction == Direction.right) activityGame.localController.tryMoveEast();
            if (direction == Direction.down) activityGame.localController.tryMoveSouth();
            if (direction == Direction.left) activityGame.localController.tryMoveWest();
        } else {
            //showFootnote("State "+state+" fling x: " + velocityX + ", fling y: " + velocityY);
            if (activityGame.state == 3 || activityGame.state == 30 || activityGame.state == 31) {
                if (direction == Direction.right) {
                    activityGame.buttonFight0.setBackgroundColor(Color.DKGRAY);
                    activityGame.buttonFight1.setBackgroundColor(activityGame.colorHighlight);
                    activityGame.state = 31;
                }
                else if (direction == Direction.left) {
                    activityGame.buttonFight0.setBackgroundColor(activityGame.colorHighlight);
                    activityGame.buttonFight1.setBackgroundColor(Color.DKGRAY);
                    activityGame.state = 30;
                }
            } else if (activityGame.state == 2 || activityGame.state == 21 || activityGame.state == 20) {
                if (direction == Direction.right) {
                    activityGame.buttonReturn.setBackgroundColor(Color.DKGRAY);
                    activityGame.layoutObject.setBackgroundColor(activityGame.colorHighlight);
                    activityGame.state = 21;
                }
                else if (direction == Direction.left) {
                    activityGame.buttonReturn.setBackgroundColor(activityGame.colorHighlight);
                    activityGame.layoutObject.setBackgroundColor(activityGame.colorLayoutObject);
                    activityGame.state = 20;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        System.out.println("TAP");
        if (activityGame.state == 0) {
            activityGame.localController.tryDisplayStats();
        } else {
            activityGame.doFavouriteAction();
        }
        return true;
    }


}
