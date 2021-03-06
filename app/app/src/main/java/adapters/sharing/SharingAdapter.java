/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package adapters.sharing;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import model.daoModels.Version;

/**
 * Created by PJ Fechner
 * Adapter for Versions
 */
public class SharingAdapter implements ExpandableListAdapter {

//    private final static String TAG = "CollapseVersionAdapter";

//    private SharingAdapterListener listener;
    private Fragment parentFragment;

    private List<SharingLanguageViewModel> models;
    private List<List<Boolean>> selectionList = new ArrayList<>();

    //region setup

    public SharingAdapter(Fragment fragment, List<SharingLanguageViewModel> models) {
        this.parentFragment = fragment;
        this.models = models;
        seedSelectionList();
    }

    private void seedSelectionList(){

        for( SharingLanguageViewModel model : models) {
            List<Boolean> selections = new ArrayList<>();
            for(int i = 0; i < model.getVersions().size(); i++ ) {
                selections.add(i, false);
            }
            selectionList.add(selections);
        }
    }

    @Override
    public SharingLanguageViewModel getGroup(int groupPosition) {
        return models.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return models.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return getCombinedGroupId(groupPosition);
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return groupId;
    }

    //endregion

    //region adapter methods

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        SharingAdapterTitleViewGroup holder;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_version_group, parent, false);
            holder = new SharingAdapterTitleViewGroup(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (SharingAdapterTitleViewGroup) convertView.getTag();
        }
        SharingLanguageViewModel model = getGroup(groupPosition);
        holder.updateWithModel(model);
        holder.setExpanded(isExpanded);
        return convertView;
    }

//    @Override
//    public void onGroupCollapsed(int groupPosition) {
//        super.onGroupCollapsed(groupPosition);
//    }
//
//    @Override
//    public void onGroupExpanded(int groupPosition) {
//        super.onGroupExpanded(groupPosition);
//    }

    @Override
    public Version getChild(int groupPosition, int childPosition) {
        return models.get(groupPosition).getVersions().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public long getCombinedChildId(long groupPosition, long childPosition) {
        return (groupPosition * 1000) + childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return models.get(groupPosition).getVersions().size();
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

        SharingAdapterVersionViewGroup holder;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_share_version_selection, parent, false);
            holder = new SharingAdapterVersionViewGroup(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (SharingAdapterVersionViewGroup) convertView.getTag();
        }

        holder.updateWithVersion(getChild(groupPosition, childPosition), groupPosition, childPosition, new SharingAdapterVersionViewGroup.SharingAdapterVersionViewGroupListener() {

            @Override
            public void clicked(SharingAdapterVersionViewGroup viewGroup) {
                selectionList.get(viewGroup.section).set(viewGroup.row, viewGroup.isChecked);
            }
        });

        holder.setChecked(selectionList.get(groupPosition).get(childPosition));
        return convertView;
    }

    public List<Version> getSelectedVersions() {

        List<Version> versions = new ArrayList<>();
        for(int i = 0; i < selectionList.size(); i++) {
            List<Boolean> list = selectionList.get(i);
            for(int j = 0; j < list.size(); j++){
                if(list.get(j)) {
                    versions.add(models.get(i).getVersions().get(j));
                }
            }
        }
        return versions;
    }

    //endregion

    //region helpers

    private Context getContext(){
        return this.parentFragment.getActivity();
    }

    //endregion

    //region group methods

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    //endregion

//    public interface SharingAdapterListener{
//        void versionChosen(Version version);
//    }
}

