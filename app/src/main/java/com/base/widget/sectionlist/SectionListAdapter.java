package com.base.widget.sectionlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.base.widget.sectionlist.PinnedHeaderListView.PinnedHeaderAdapter;
import com.base.widget.sectionlist.SectionAdapter.SectionObj;
import com.wmlives.heihei.R;

/**
 * Adapter for sections.
 */
public class SectionListAdapter extends BaseAdapter implements OnItemClickListener, PinnedHeaderAdapter,
        SectionIndexer, OnScrollListener {

    private boolean FOLD_MODE = false;// 折叠模式
    private List<Integer> fold_section_list = null;

    private SectionIndexer mIndexer;
    private String[] mSections;
    private int[] mCounts;
    private int mSectionCounts = 0;

    private final DataSetObserver dataSetObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            updateTotalCount();
        }

        @Override
        public void onInvalidated() {
            updateTotalCount();
        };
    };

    private SectionAdapter linkedAdapter;
    private final Map<String, View> currentViewSections = new HashMap<String, View>();
    private int viewTypeCount;
    protected final LayoutInflater inflater;

    private View transparentSectionView;

    private OnItemClickListener linkedListener;

    public SectionListAdapter(final LayoutInflater inflater, final SectionAdapter linkedAdapter) {
        this.linkedAdapter = linkedAdapter;
        this.inflater = inflater;
        linkedAdapter.registerDataSetObserver(dataSetObserver);
        updateTotalCount();
        mIndexer = new SLSectionIndexer(mSections, mCounts);

        fold_section_list = new ArrayList<Integer>();
    }

    private boolean isTheSame(final String previousSection, final String newSection) {
        if (previousSection == null) {
            return newSection == null;
        } else {
            return previousSection.equals(newSection);
        }
    }

    private void fillSections() {
        mSections = new String[mSectionCounts];
        mCounts = new int[mSectionCounts];
        final int count = linkedAdapter.getCount();
        String currentSection = "";
        // int newSectionIndex = 0;
        // int newSectionCounts = 0;
        // String previousSection = null;
        // for (int i = 0; i < count; i++)
        // {
        // newSectionCounts++;
        // currentSection = linkedAdapter.getItem(i).section;
        // if (!isTheSame(previousSection, currentSection))
        // {
        // mSections[newSectionIndex] = currentSection;
        // previousSection = currentSection;
        // if (newSectionIndex == 1)
        // {
        // mCounts[0] = newSectionCounts - 1;
        // } else if (newSectionIndex != 0)
        // {
        // mCounts[newSectionIndex - 1] = newSectionCounts;
        // }
        // if (i != 0)
        // {
        // newSectionCounts = 0;
        // }
        // newSectionIndex++;
        // } else if (i == count - 1)
        // {
        // mCounts[newSectionIndex - 1] = newSectionCounts + 1;
        // }
        // }
        for (int i = 0; i < count; i++) {

            Object obj = linkedAdapter.getItem(i);
            // TODO 如果有人想加的类型 新的调用 也许需要改这里
            if (obj instanceof SectionObj) {
                currentSection = ((SectionObj) obj).section;
            }
            // 通过a找到mCount的index,计数加1
            for (int j = 0; j < mSections.length; j++) {
                if (mSections[j] != null && mSections[j].equals(currentSection)) {
                    mCounts[j] += 1;
                    break;
                } else if (mSections[j] == null) {
                    mSections[j] = currentSection;
                    mCounts[j] += 1;
                    break;
                }
            }
        }
    }

    private synchronized void updateTotalCount() {
        String currentSection = null;
        viewTypeCount = linkedAdapter.getViewTypeCount() + 1;
        final int count = linkedAdapter.getCount();
        for (int i = 0; i < count; i++) {
            Object obj = linkedAdapter.getItem(i);
            String section = "";

            if (obj instanceof SectionObj) {
                section = ((SectionObj) obj).section;
            }
            if (!isTheSame(currentSection, section)) {
                mSectionCounts++;
                currentSection = section;
            }
        }
        fillSections();
    }

    @Override
    public synchronized int getCount() {
        return linkedAdapter.getCount();
    }

    @Override
    public synchronized Object getItem(final int position) {
        final int linkedItemPosition = getLinkedPosition(position);
        return linkedAdapter.getItem(linkedItemPosition);
    }

    // public synchronized boolean isSection(final int position) {
    // return sectionPositions.containsKey(position);
    // }

    public synchronized String getSectionName(final int position) {
        String[] sections = getStringSections();
        if (position <= -1 || position >= sections.length) {
            return "";
        }
        return sections[position];
    }

    @Override
    public long getItemId(final int position) {
        return linkedAdapter.getItemId(getLinkedPosition(position));
    }

    protected Integer getLinkedPosition(final int position) {
        return position;
    }

    @Override
    public int getItemViewType(final int position) {
        return linkedAdapter.getItemViewType(getLinkedPosition(position));
    }

    protected synchronized void replaceSectionViewsInMaps(final String section, final View theView) {
        if (currentViewSections.containsKey(theView)) {
            currentViewSections.remove(theView);
        }
        currentViewSections.put(section, theView);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = linkedAdapter.getView(position, convertView, parent);
        try {
            final Object currentItem = linkedAdapter.getItem(position);
            if (currentItem != null) {// to set every item's text
                final TextView header = (TextView) view.findViewById(R.id.index).findViewById(R.id.index_title);
                final int section = getSectionForPosition(position);
                String title = null;
                if (section < 0) {
                    title = "";
                } else {
                    title = (String) mIndexer.getSections()[section];
                }
                header.setText(title);
                if (getPositionForSection(section) == position) {
                    view.findViewById(R.id.index).setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    // if ("热门".equals(title))
                    // {
                    // header.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_hot, 0, 0, 0);
                    // header.setCompoundDrawablePadding((int) (RT.application.getResources().getDisplayMetrics().density * 6));
                    // } else
                    // {
                    header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    // }
                } else {
                    view.findViewById(R.id.index).setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                    header.setCompoundDrawables(null, null, null, null);
                }

                if (FOLD_MODE)// 折叠模式
                {
                    View item = view.findViewById(R.id.item_layout);
                    if (header.getVisibility() == View.VISIBLE) {
                        header.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {
                                try {
                                    if (fold_section_list == null)
                                        fold_section_list = new ArrayList<Integer>();
                                    if (fold_section_list.contains(section)) {
                                        fold_section_list.remove((Integer) section);
                                    } else {
                                        fold_section_list.add(section);
                                    }
                                    notifyDataSetChanged();
                                    linkedAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        header.setFocusable(false);
                    }

                    if (fold_section_list == null)
                        fold_section_list = new ArrayList<Integer>();
                    item.setVisibility(fold_section_list.contains(section) ? View.GONE : View.VISIBLE);
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public boolean hasStableIds() {
        return linkedAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return linkedAdapter.isEmpty();
    }

    @Override
    public void registerDataSetObserver(final DataSetObserver observer) {
        linkedAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(final DataSetObserver observer) {
        linkedAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return linkedAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(final int position) {
        return linkedAdapter.isEnabled(getLinkedPosition(position));
    }

    public int getRealPosition(int pos) {
        return pos - 1;
    }

    public synchronized View getTransparentSectionView() {
        if (transparentSectionView == null) {
            // transparentSectionView = createNewSectionView();
        }
        return transparentSectionView;
    }

    protected void sectionClicked(final String section) {
        // do nothing
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (linkedListener != null) {
            linkedListener.onItemClick(parent, view, getLinkedPosition(position), id);
        }

    }

    public void setOnItemClickListener(final OnItemClickListener linkedListener) {
        this.linkedListener = linkedListener;
    }

    @Override
    public int getPinnedHeaderState(int position) {
        int realPosition = position;
        if (mIndexer == null) {
            return PINNED_HEADER_GONE;
        }
        if (realPosition < 0) {
            return PINNED_HEADER_GONE;
        }
        int section = getSectionForPosition(realPosition);
        int nextSectionPosition = getPositionForSection(section + 1);
        if (nextSectionPosition != -1 && realPosition == nextSectionPosition - 1) {
            return PINNED_HEADER_PUSHED_UP;
        }
        return PINNED_HEADER_VISIBLE;
    }

    @Override
    public void configurePinnedHeader(View header, int position, int alpha) {

        int realPosition = position;
        int section = getSectionForPosition(realPosition);
        if (section > mIndexer.getSections().length - 1) {
            return;
        }

        String title = null;
        if (section == -1) {
            title = "";
        } else {
            title = (String) mIndexer.getSections()[section];
        }
        Object obj = getItem(realPosition);
        TextView tv = ((TextView) header.findViewById(R.id.index_title));
        tv.setText(title);
        // if ("热门".equals(title))
        // {
        // tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_hot, 0, 0, 0);
        // tv.setCompoundDrawablePadding((int) (RT.application.getResources().getDisplayMetrics().density * 6));
        // } else
        // {
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        // }
        header.requestLayout();

        if (mOnSectionViewRefreshListener != null)
        {
            mOnSectionViewRefreshListener.onSectionViewRefresh(header, section);
        }
        
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    public String[] getStringSections() {
        if (mIndexer == null) {
            return new String[] { "" };
        } else {
            return (String[]) mIndexer.getSections();
        }
    }

    @Override
    public int getPositionForSection(int section) {
        if (mIndexer == null) {
            return -1;
        }
        return mIndexer.getPositionForSection(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        if (mIndexer == null) {
            return -1;
        }
        return mIndexer.getSectionForPosition(position);
    }

    public int getPositionForSection(String section) {
        if (mIndexer == null) {
            return -1;
        }
        String[] sections = getStringSections();
        for (int i = 0; i < sections.length; i++) {
            if (section.equals(sections[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (view instanceof OnScrollListener) {
            ((OnScrollListener) view).onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
        }
        if (view instanceof OnScrollListener) {
            ((OnScrollListener) view).onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void setInnerAdatper(SectionAdapter adapter) {
        if (linkedAdapter != null) {
            unregisterDataSetObserver(dataSetObserver);
        }
        this.linkedAdapter = adapter;
        linkedAdapter.registerDataSetObserver(dataSetObserver);
        updateTotalCount();
        mIndexer = new SLSectionIndexer(mSections, mCounts);

        fold_section_list = new ArrayList<Integer>();
    }

    private OnSectionViewRefreshListener mOnSectionViewRefreshListener;
    
    public void setOnSectionViewRefreshListener(OnSectionViewRefreshListener mListener)
    {
        this.mOnSectionViewRefreshListener = mListener;
    }
    
    public static interface OnSectionViewRefreshListener
    {
        public void onSectionViewRefresh(View head,int section);
    }
    
}
