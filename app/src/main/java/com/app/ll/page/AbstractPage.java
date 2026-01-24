package com.app.ll.page;

import androidx.fragment.app.Fragment;

public abstract class AbstractPage extends Fragment {
    private final String mName;

    public AbstractPage() {
        mName = name();
    }

    protected abstract String name();

    public final String getName() {
        return mName;
    }
}
