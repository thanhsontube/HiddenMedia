package ts.hn.tstest.movie;

import java.util.Collection;
import java.util.Stack;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


abstract public class AbsFragmentActivity extends FragmentActivity implements OnBackStackChangedListener {

    protected static final String FRAGMENT_KEY = "main";
    protected static final String SAVE_KEY_STACK = "tag_stack";

    protected final Stack<String> mFragmentTagStack = new Stack<String>();

    abstract protected Fragment onCreateMainFragment(Bundle savedInstanceState);

    abstract protected int getFragmentContainerId();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(getFragmentContainerId(), onCreateMainFragment(savedInstanceState), FRAGMENT_KEY)
                    .setTransition(FragmentTransaction.TRANSIT_NONE).commit();
        } else {
            mFragmentTagStack.addAll((Collection<String>) savedInstanceState.getSerializable(SAVE_KEY_STACK));
            restoreFragmentsState();
        }
        getFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        restoreFragmentsState();
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fm = getFragmentManager();
        final Fragment f;
        if (mFragmentTagStack.size() > 0) {
            f = fm.findFragmentByTag(mFragmentTagStack.peek());
        } else {
            f = fm.findFragmentByTag(FRAGMENT_KEY);
        }

        if (f instanceof OnBackPressListener) {
            if (((OnBackPressListener) f).onBackPress()) {
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_KEY_STACK, mFragmentTagStack);
    }

    public void showFragment(Fragment f, boolean isTransit) {
        final String tag = String.format("%s:%d", getClass().getName(), mFragmentTagStack.size());
        final FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        if (mFragmentTagStack.size() > 0) {
            final Fragment ff = fm.findFragmentByTag(mFragmentTagStack.peek());
            ft.hide(ff);
        } else {
            final Fragment ff = fm.findFragmentByTag(FRAGMENT_KEY);
            ft.hide(ff);
        }
        if (fm.findFragmentByTag(tag) == null) {
            ft.add(getFragmentContainerId(), f, tag);
            ft.show(f);
        } else {
            ft.replace(getFragmentContainerId(), f, tag);
            ft.show(f);
        }
        if (isTransit) {
            ft.addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        } else {
            ft.addToBackStack(null);
        }
        ft.commit();
        mFragmentTagStack.add(tag);
    }

    @Override
    public void onBackStackChanged() {
        final FragmentManager fm = getFragmentManager();

        if (fm.getBackStackEntryCount() == mFragmentTagStack.size()) {
            return;
        }

        if (mFragmentTagStack.size() > 0) {
            final FragmentTransaction ft = fm.beginTransaction();
            final String tag = mFragmentTagStack.pop();
            if (fm.findFragmentByTag(tag) != null) {
                ft.remove(fm.findFragmentByTag(tag));
            }
            ft.commit();
        }
    }

    protected void restoreFragmentsState() {
        final FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        if (mFragmentTagStack.size() == 0) {
            ft.show(fm.findFragmentByTag(FRAGMENT_KEY));
        } else {
            ft.hide(fm.findFragmentByTag(FRAGMENT_KEY));
            for (int i = 0; i < mFragmentTagStack.size(); i++) {
                String tag = mFragmentTagStack.get(i);
                Fragment f = fm.findFragmentByTag(tag);
                if (i + 1 < mFragmentTagStack.size()) {
                    ft.hide(f);
                } else {
                    ft.show(f);
                }
            }
        }
        ft.commit();
    }

}
