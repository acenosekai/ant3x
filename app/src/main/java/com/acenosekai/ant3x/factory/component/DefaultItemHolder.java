package com.acenosekai.ant3x.factory.component;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acenosekai.ant3x.R;
import com.mikepenz.iconics.view.IconicsImageView;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class DefaultItemHolder extends RecyclerView.ViewHolder {
    private TextView listText;
    private TextView listDesc;
    private TextView listLength;
    private IconicsImageView listMenu;
    private IconicsImageView iconDefault;
    private ImageView cover;
    private View wrap;

    private View itemView;

    public DefaultItemHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.iconDefault = (IconicsImageView) itemView.findViewById(R.id.album_icon);
        this.listText = (TextView) itemView.findViewById(R.id.list_text);
        this.listDesc = (TextView) itemView.findViewById(R.id.list_text_desc);
        this.listLength = (TextView) itemView.findViewById(R.id.list_text_length);
        this.listMenu = (IconicsImageView) itemView.findViewById(R.id.menu_item);
        this.cover = (ImageView) itemView.findViewById(R.id.album_cover);
        this.wrap = itemView.findViewById(R.id.item_wrap);
    }

    public View getWrap() {
        return wrap;
    }

    public TextView getListLength() {
        return listLength;
    }

    public TextView getListText() {
        return listText;
    }

    public TextView getListDesc() {
        return listDesc;
    }

    public IconicsImageView getListMenu() {
        return listMenu;
    }

    public View getItemView() {
        return itemView;
    }

    public IconicsImageView getIconDefault() {
        return iconDefault;
    }

    public ImageView getCover() {
        return cover;
    }

}
