package com.secreto.viewHolders;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.image.ImageCacheManager;
import com.secreto.model.User;
import com.secreto.utils.NetworkImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kshitiz Nandan on 8/29/2017.
 */

public class SearchUserViewHolder extends BaseViewHolder {
    @BindView(R.id.rLFindUser)
    RelativeLayout rLFindUser;
    @BindView(R.id.iv_profileImg)
    NetworkImageView imageProfile;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_userName_userEmail)
    TextView tvUserNameUserEmail;

    public SearchUserViewHolder(View itemView, View.OnClickListener onClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        rLFindUser.setOnClickListener(onClickListener);
    }

    @Override
    public void onBindView(Object object, int position) {
        User user = (User) object;
        rLFindUser.setTag(user);
        if (!TextUtils.isEmpty(user.getProfile_pic())) {
            imageProfile.setImageUrl(user.getProfile_pic(), ImageCacheManager.getInstance().getImageLoader());
        } else {
            imageProfile.setDefaultImageResId(R.drawable.default_user);
        }
        if (!TextUtils.isEmpty(user.getName())) {
            tvName.setText(user.getName());
        } else {
            tvName.setText("");
        }
        if (!TextUtils.isEmpty(user.getEmail())) {
            tvUserNameUserEmail.setText(user.getEmail());
        } else {
            tvUserNameUserEmail.setText("");
        }
    }
}
