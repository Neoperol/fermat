package com.bitdubai.reference_wallet.crypto_broker_wallet.common.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitdubai.fermat_android_api.ui.expandableRecicler.ExpandableRecyclerAdapter;
import com.bitdubai.reference_wallet.crypto_broker_wallet.R;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.holders.GrouperViewHolder;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.holders.OpenNegotiationViewHolder;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.models.GrouperItemData;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.models.OpenNegotiationsItemData;

import java.util.List;


public class OpenNegotiationsExpandableAdapter
        extends ExpandableRecyclerAdapter<GrouperViewHolder, OpenNegotiationViewHolder, GrouperItemData, OpenNegotiationsItemData> {

    private LayoutInflater mInflater;

    /**
     * Public primary constructor.
     *
     * @param context        the activity context where the RecyclerView is going to be displayed
     * @param parentItemList the list of parent items to be displayed in the RecyclerView
     */
    public OpenNegotiationsExpandableAdapter(Context context, List<GrouperItemData> parentItemList) {
        super(parentItemList);
        mInflater = LayoutInflater.from(context);
    }

    /**
     * OnCreateViewHolder implementation for parent items. The desired ParentViewHolder should
     * be inflated here
     *
     * @param parent for inflating the View
     * @return the user's custom parent ViewHolder that must extend ParentViewHolder
     */
    @Override
    public GrouperViewHolder onCreateParentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.cbp_list_item_parent, parent, false);
        return new GrouperViewHolder(view);
    }

    /**
     * OnCreateViewHolder implementation for child items. The desired ChildViewHolder should
     * be inflated here
     *
     * @param parent for inflating the View
     * @return the user's custom parent ViewHolder that must extend ParentViewHolder
     */
    @Override
    public OpenNegotiationViewHolder onCreateChildViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.cbp_list_item_child, parent, false);
        return new OpenNegotiationViewHolder(view);
    }

    /**
     * OnBindViewHolder implementation for parent items. Any data or view modifications of the
     * parent view should be performed here.
     *
     * @param parentViewHolder the ViewHolder of the parent item created in OnCreateParentViewHolder
     * @param position         the position in the RecyclerView of the item
     */
    @Override
    public void onBindParentViewHolder(GrouperViewHolder parentViewHolder, int position, GrouperItemData parentListItem) {
        parentViewHolder.bind(parentListItem.getParentNumber(), parentListItem.getParentText());
    }

    /**
     * OnBindViewHolder implementation for child items. Any data or view modifications of the
     * child view should be performed here.
     *
     * @param childViewHolder the ViewHolder of the child item created in OnCreateChildViewHolder
     * @param position        the position in the RecyclerView of the item
     */
    @Override
    public void onBindChildViewHolder(OpenNegotiationViewHolder childViewHolder, int position, OpenNegotiationsItemData childListItem) {
        childViewHolder.bind(childListItem.getChildText());
    }
}