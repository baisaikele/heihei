package com.heihei.cell;

import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.UIUtils;
import com.facebook.fresco.FrescoImageHelper;
import com.heihei.fragment.live.widget.AvatarImageView;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.User;
import com.wmlives.heihei.R;

public class UserCell extends LinearLayout implements ListCell, OnClickListener {

    // ----------------R.layout.cell_user-------------Start
    private ImageView btn_follow;
    private AvatarImageView iv_avatar;
    private TextView tv_nickname;
    private TextView tv_level;

    public void autoLoad_cell_user() {
        btn_follow = (ImageView) findViewById(R.id.btn_follow);
        iv_avatar = (AvatarImageView) findViewById(R.id.iv_avatar);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_level = (TextView) findViewById(R.id.tv_level);
    }

    // ----------------R.layout.cell_user-------------End

    public UserCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        autoLoad_cell_user();
        btn_follow.setOnClickListener(this);
    }

    private User user;

    @Override
    public void setData(Object data, int position, BaseAdapter mAdapter) {
        user = (User) data;
        iv_avatar.setUser(user);
        tv_nickname.setText(user.nickname);
        tv_level.setText("LV." + user.level);
        refreshFollowState();
    }

    /**
     * 刷新关注状态
     */
    private void refreshFollowState() {

        if (user.uid.equals(UserMgr.getInstance().getUid())) {
            btn_follow.setVisibility(View.GONE);
        } else {
            btn_follow.setVisibility(View.VISIBLE);
            if (user.isFollowed) {
                btn_follow.setImageResource(R.drawable.hh_userlist_following);
            } else {
                btn_follow.setImageResource(R.drawable.hh_userlist_unfollow_44);
            }
        }

    }

    private UserPresent mUserPresent;

    @Override
    public void onClick(View v) {

        if (mUserPresent == null) {
            mUserPresent = new UserPresent();
        }

        if (!user.isFollowed) {

            mUserPresent.followUser(user.uid, null,new JSONResponse() {

                @Override
                public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
                    if (errCode == ErrorCode.ERROR_OK) {
                        user.isFollowed = true;
                        refreshFollowState();
                    } else {
                        UIUtils.showToast(msg);
                    }

                }
            });

        } else {
            mUserPresent.unfollowUser(user.uid, new JSONResponse() {

                @Override
                public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
                    if (errCode == ErrorCode.ERROR_OK) {
                        user.isFollowed = false;
                        refreshFollowState();
                    } else {
                        UIUtils.showToast(msg);
                    }
                }
            });
        }
    }
}
