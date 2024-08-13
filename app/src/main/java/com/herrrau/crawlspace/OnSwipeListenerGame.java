package com.herrrau.crawlspace;

import android.view.MotionEvent;

public class OnSwipeListenerGame extends OnSwipeListener {
    ActivityGame activityGame;

    public OnSwipeListenerGame(ActivityGame activityGame) {
        super();
        this.activityGame = activityGame;
    }

        @Override
    public boolean onSwipe(OnSwipeListener.Direction direction) {
        if (activityGame.state == 0) {
            //System.out.println(" State: "+activityGame.state);
            if (direction == Direction.up) activityGame.localController.tryMoveNorth();
            if (direction == Direction.right) activityGame.localController.tryMoveEast();
            if (direction == Direction.down) activityGame.localController.tryMoveSouth();
            if (direction == Direction.left) activityGame.localController.tryMoveWest();
        } else {
            System.out.println("On Swipe Swiping when not in game mode, i.e. displaying something.");
            System.out.println(" State: "+activityGame.state);
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        activityGame.localController.tryDisplayStats();
        return true;
    }


}
