package com.base.widget.sectionlist;

import java.util.Arrays;

import android.widget.SectionIndexer;

public class SLSectionIndexer implements SectionIndexer {

    private final String[] mSections;//
    private final int[] mPositions;
    private final int mCount;

    /**
     * @param sections
     * @param counts
     */
    public SLSectionIndexer(String[] sections, int[] counts) {
        if (sections == null || counts == null) {
            throw new NullPointerException();
        }
        if (sections.length != counts.length) {
            throw new IllegalArgumentException("The sections and counts arrays must have the same length");
        }
        this.mSections = sections;
        mPositions = new int[counts.length];
        int position = 0;
        for (int i = 0; i < counts.length; i++) {
            if (mSections[i] == null) {
                mSections[i] = "";
            } else {
                mSections[i] = mSections[i].trim();
            }

            mPositions[i] = position;
            position += counts[i];
        }

        mCount = position;
    }

    @Override
    public Object[] getSections() {
        // TODO Auto-generated method stub
        return mSections;
    }

    @Override
    public int getPositionForSection(int section) {

        if (section < 0 || section >= mPositions.length) {
            return -1;
        }
        return mPositions[section];
    }

    @Override
    public int getSectionForPosition(int position) {

        if (position < 0 || position >= mCount) {
            return -1;
        }
        int index = Arrays.binarySearch(mPositions, position);
        return index >= 0 ? index : -index - 2;

    }

}