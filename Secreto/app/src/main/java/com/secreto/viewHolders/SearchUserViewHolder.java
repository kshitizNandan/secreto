package com.secreto.viewHolders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.common.Common;
import com.secreto.image.ImageCacheManager;
import com.secreto.model.User;
import com.secreto.utils.NetworkImageView;
import com.secreto.widgets.CircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kshitiz Nandan on 8/29/2017.
 */

public class SearchUserViewHolder extends BaseViewHolder {
    private final Context context;
    private final int imgSize;
    @BindView(R.id.rLFindUser)
    RelativeLayout rLFindUser;
    @BindView(R.id.iv_profileImg)
    ImageView iv_profileImg;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_userName_userEmail)
    TextView tvUserNameUserEmail;

    public SearchUserViewHolder(View itemView, View.OnClickListener onClickListener) {
        super(itemView);
        this.context = itemView.getContext();
        imgSize = Common.dipToPixel(context, 60);
        ButterKnife.bind(this, itemView);
        rLFindUser.setOnClickListener(onClickListener);
    }

    @Override
    public void onBindView(Object object, int position) {
        User user = (User) object;
        rLFindUser.setTag(user);
        if (!TextUtils.isEmpty(user.getProfile_pic())) {
            Picasso.with(context).load(user.getProfile_pic()).resize(imgSize, imgSize).transform(new CircleTransform()).into(iv_profileImg);
        } else {
            iv_profileImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.default_user));
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
