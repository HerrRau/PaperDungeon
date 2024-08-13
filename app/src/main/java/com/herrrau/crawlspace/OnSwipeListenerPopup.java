package com.herrrau.crawlspace;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeListenerPopup extends OnSwipeListener implements View.OnTouchListener
{
    ActivityGame activityGame;
    public OnSwipeListenerPopup(ActivityGame activityGame) {
        super();
        this.activityGame = activityGame;
    }

    @Override public boolean onTouch(View v, MotionEvent event){
        System.out.println("OSLAG-P");
        return true;
    }


//    @Override public boolean onSwipe(Direction direction){
//        System.out.println("OSLAG-P On Swipe...");
//        if (direction==Direction.right)  activityGame.localController.tryMoveEast();
//        if (direction==Direction.left)  activityGame.localController.tryMoveWest();
//        return false;
//    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        //System.out.println("OSLAGP");
        activityGame.doFavouriteAction();
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        //showFootnote("State "+state+" fling x: " + velocityX + ", fling y: " + velocityY);
        if (activityGame.state == 3 || activityGame.state == 30 || activityGame.state == 31) {
            if (velocityX > 0) {
                activityGame.buttonFight0.setBackgroundColor(Color.DKGRAY);
                activityGame.buttonFight1.setBackgroundColor(activityGame.colorHighlight);
                activityGame.state = 31;
            } else {
                activityGame.buttonFight0.setBackgroundColor(activityGame.colorHighlight);
                activityGame.buttonFight1.setBackgroundColor(Color.DKGRAY);
                activityGame.state = 30;
            }
        } else if (activityGame.state == 2 || activityGame.state == 21 || activityGame.state == 20) {
            if (velocityX > 0) {
                activityGame.buttonReturn.setBackgroundColor(Color.DKGRAY);
                activityGame.layoutObject.setBackgroundColor(activityGame.colorHighlight);
                activityGame.state = 21;
            } else {
                activityGame.buttonReturn.setBackgroundColor(activityGame.colorHighlight);
                activityGame.layoutObject.setBackgroundColor(activityGame.colorLayoutObject);
                activityGame.state = 20;
            }
        }
        return true;
    }

}
