package com.epgis.offline.fragment;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by SUZY on 2018/3/20.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    public static final int REQUEST_CODE_INVALID = -1;

    private FragmentManager mFManager;
    private AtomicInteger mAtomicInteger = new AtomicInteger();
    private List<NodeFragment> mFragmentStack = new ArrayList<>();


    public final <T extends NodeFragment> T fragment(Class<T> fragmentClass) {
        return (T) Fragment.instantiate(this, fragmentClass.getName());
    }

    public final <T extends NodeFragment> T fragment(Class<T> fragmentClass, Bundle bundle) {
        return (T) Fragment.instantiate(this, fragmentClass.getName(), bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setStatusBarFullTransparent();
        super.onCreate(savedInstanceState);
        mFManager = getSupportFragmentManager();
        if(savedInstanceState != null){
            reSetFrameStack();
        }
    }

    /**
     * 全透状态栏(文字黑色)
     */
    public void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 全透状态栏(图标，颜色，文字白色)
     */
    public void setStatusBarFullTransparentLight() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    protected void reSetFrameStack(){
        mFragmentStack.clear();
        List<Fragment> fragments = mFManager.getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment instanceof  NodeFragment){
                mFragmentStack.add((NodeFragment) fragment);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Show a fragment.
     *
     * @param clazz fragment class.
     */
    public final <T extends NodeFragment> void startFragment(Class<T> clazz) {
        try {
            NodeFragment targetFragment = clazz.newInstance();
            startFragment(null, targetFragment, true, REQUEST_CODE_INVALID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a fragment.
     *
     * @param clazz       fragment class.
     * @param stickyStack sticky to back stack.
     */
    public final <T extends NodeFragment> void startFragment(Class<T> clazz, boolean stickyStack) {
        try {
            NodeFragment targetFragment = clazz.newInstance();
            startFragment(null, targetFragment, stickyStack, REQUEST_CODE_INVALID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a fragment.
     *
     * @param targetFragment fragment to display.
     * @param <T>            {@link NodeFragment}.
     */
    public final <T extends NodeFragment> void startFragment(T targetFragment) {
        startFragment(null, targetFragment, true, REQUEST_CODE_INVALID);
    }

    /**
     * Show a fragment.
     *
     * @param targetFragment fragment to display.
     * @param stickyStack    sticky back stack.
     * @param <T>            {@link NodeFragment}.
     */
    public final <T extends NodeFragment> void startFragment(T targetFragment, boolean stickyStack) {
        startFragment(null, targetFragment, stickyStack, REQUEST_CODE_INVALID);
    }

    /**
     * Show a fragment for result.
     *
     * @param clazz       fragment to display.
     * @param requestCode requestCode.
     * @param <T>         {@link NodeFragment}.
     * @deprecated use {@link #startFragmentForResult(Class, int)} instead.
     */
    @Deprecated
    public final <T extends NodeFragment> void startFragmentForResquest(Class<T> clazz, int requestCode) {
        startFragmentForResult(clazz, requestCode);
    }

    /**
     * Show a fragment for result.
     *
     * @param targetFragment fragment to display.
     * @param requestCode    requestCode.
     * @param <T>            {@link NodeFragment}.
     * @deprecated use {@link #startFragmentForResult(NodeFragment, int)} instead.
     */
    @Deprecated
    public final <T extends NodeFragment> void startFragmentForResquest(T targetFragment, int requestCode) {
        startFragmentForResult(targetFragment, requestCode);
    }

    /**
     * Show a fragment for result.
     *
     * @param clazz       fragment to display.
     * @param requestCode requestCode.
     * @param <T>         {@link NodeFragment}.
     */
    public final <T extends NodeFragment> void startFragmentForResult(Class<T> clazz, int requestCode) {
        if (requestCode == REQUEST_CODE_INVALID)
            throw new IllegalArgumentException("The requestCode must be positive integer.");
        try {
            NodeFragment targetFragment = clazz.newInstance();
            startFragment(null, targetFragment, true, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a fragment for result.
     *
     * @param targetFragment fragment to display.
     * @param requestCode    requestCode.
     * @param <T>            {@link NodeFragment}.
     */
    public final <T extends NodeFragment> void startFragmentForResult(T targetFragment, int requestCode) {
        if (requestCode == REQUEST_CODE_INVALID)
            throw new IllegalArgumentException("The requestCode must be positive integer.");
        startFragment(null, targetFragment, true, requestCode);
    }


    protected final <T extends NodeFragment> void startFragment(T thisFragment, T thatFragment,
                                                                boolean stickyStack, int requestCode) {
        startFragment(thisFragment, thatFragment, stickyStack, requestCode, null);
    }

    /**
     * Show a fragment.
     *
     * @param thisFragment Now show fragment, can be null.
     * @param thatFragment fragment to display.
     * @param stickyStack  sticky back stack.
     * @param requestCode  requestCode.
     * @param <T>          {@link NodeFragment}.
     */
    protected final <T extends NodeFragment> void startFragment(T thisFragment, T thatFragment,
                                                                boolean stickyStack, int requestCode, Bundle bundle) {

        FragmentTransaction fragmentTransaction = mFManager.beginTransaction();
        if (thisFragment != null) {
            FragmentStackEntity thisStackEntity = thisFragment.getFragmentStackEntity();
            if (thisStackEntity != null) {
                if (thisStackEntity.isSticky) {
                    thisFragment.onPause();
                    thisFragment.onStop();
                    fragmentTransaction.hide(thisFragment);
                } else {
                    fragmentTransaction.remove(thisFragment).commitAllowingStateLoss();
                    fragmentTransaction.commitNow();
                    fragmentTransaction = mFManager.beginTransaction();

                    thisFragment.removeFragmentStackEntity();
                    mFragmentStack.remove(thisFragment);
                }
            }
        }

        String fragmentTag = thatFragment.getClass().getSimpleName() + mAtomicInteger.incrementAndGet();
        if(bundle != null){
            thatFragment.setArguments(bundle);
        }
        fragmentTransaction.add(fragmentLayoutId(), thatFragment, fragmentTag);
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();

        FragmentStackEntity fragmentStackEntity = new FragmentStackEntity();
        fragmentStackEntity.isSticky = stickyStack;
        fragmentStackEntity.requestCode = requestCode;
//        thatFragment.setStackEntity(fragmentStackEntity);
        thatFragment.setFragmentStackEntity(fragmentStackEntity);
//        mFragmentEntityMap.put(thatFragment, fragmentStackEntity);
        thatFragment.setFragmentStackEntity(fragmentStackEntity);

        mFragmentStack.add(thatFragment);

        Log.d("BaseActivity", "startFragment() size= "+mFragmentStack.size()+" , "+thatFragment.toString());
    }

    public final <T extends NodeFragment> void replaceFragment(Class<T> clazz) {
        try {
            NodeFragment targetFragment = clazz.newInstance();
            replaceFragment(targetFragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final <T extends NodeFragment> void replaceFragment(Class<T> clazz, Bundle bundle) {
        try {
            NodeFragment targetFragment = clazz.newInstance();
            replaceFragment(targetFragment, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final <T extends NodeFragment> void replaceFragment(T thatFragment) {
        replaceFragment(null, thatFragment, true,  REQUEST_CODE_INVALID, null);
    }

    public final <T extends NodeFragment> void replaceFragment(T thatFragment, Bundle bundle) {
        replaceFragment(null, thatFragment, true,  REQUEST_CODE_INVALID, bundle);
    }

    public final <T extends NodeFragment> void replaceFragment(T thisFragment, T thatFragment,
                                                                boolean stickyStack, int requestCode, Bundle bundle) {

        FragmentTransaction fragmentTransaction = mFManager.beginTransaction();
        if (thisFragment != null) {
            fragmentTransaction.remove(thisFragment).commitAllowingStateLoss();
            fragmentTransaction.commitNow();
            fragmentTransaction = mFManager.beginTransaction();

//            mFragmentEntityMap.remove(thisFragment);
            thisFragment.removeFragmentStackEntity();
            mFragmentStack.remove(thisFragment);
        }

        String fragmentTag = thatFragment.getClass().getSimpleName() + mAtomicInteger.incrementAndGet();
        if(bundle != null){
            thatFragment.setArguments(bundle);
        }
        fragmentTransaction.add(fragmentLayoutId(), thatFragment, fragmentTag);
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();

        FragmentStackEntity fragmentStackEntity = new FragmentStackEntity();
        fragmentStackEntity.isSticky = stickyStack;
        fragmentStackEntity.requestCode = requestCode;
//        thatFragment.setStackEntity(fragmentStackEntity);
        thatFragment.setFragmentStackEntity(fragmentStackEntity);
//        mFragmentEntityMap.put(thatFragment, fragmentStackEntity);

        mFragmentStack.add(thatFragment);

        Log.d(getClass().getSimpleName(), "replaceFragment() size= "+mFragmentStack.size()+" , "+thatFragment.toString());
    }

    private NodeFragment mOutFragment;

    private NodeFragment mInFragment;


    public void fragmentOnStop(NodeFragment nodeFragment){
        if(nodeFragment == mOutFragment){
            if(mInFragment != null){
                mInFragment.onResume();
                mOutFragment = null;
                mInFragment = null;
            }
        }
    }

        /**
         * When the back off.
         */
    protected final boolean onBackStackFragment() {
        if (mFragmentStack.size() > 1) {
            mFManager.popBackStack();
            NodeFragment inFragment = mFragmentStack.get(mFragmentStack.size() - 2);

            FragmentTransaction fragmentTransaction = mFManager.beginTransaction();
            fragmentTransaction.show(inFragment);
            fragmentTransaction.commitAllowingStateLoss();

            NodeFragment outFragment = mFragmentStack.get(mFragmentStack.size() - 1);
            //增加返回键调用
//            outFragment.onBackPressed();
//            inFragment.onResume();
            mOutFragment = outFragment;
            mInFragment = inFragment;
//            FragmentStackEntity stackEntity = mFragmentEntityMap.get(outFragment);
            FragmentStackEntity stackEntity = outFragment.getFragmentStackEntity();
            mFragmentStack.remove(outFragment);
//            mFragmentEntityMap.remove(outFragment);
            outFragment.removeFragmentStackEntity();
            if (stackEntity != null && stackEntity.requestCode != REQUEST_CODE_INVALID) {
                inFragment.onFragmentResult(stackEntity.requestCode, stackEntity.resultCode, stackEntity.result);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        //onBackPressed先于fragment的操作，如果fragment消费了onBackPressed，则不做后续退出操作
        if(getCurrentFragment() != null){
            if(getCurrentFragment().onBackPressed()){
                return;
            }
        }
//        finishFragment();
        finish();
    }

    public void finishFragment(){
        if (!onBackStackFragment()) {
            exitApp();
        }
    }

    private NodeFragment getCurrentFragment(){
        NodeFragment currentFragment = null;
        if (mFragmentStack.size() > 0) {
            currentFragment = mFragmentStack.get(mFragmentStack.size() - 1);
        }
        return currentFragment;
    }
    /**
     * 双击退出
     */
    private static Boolean isExit = false;
    private void exitApp() {
        Timer tExit = null;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 1500);


        } else {
            finishActivity();
            finish();
            System.exit(0);
        }
    }


    public void finishActivity(){

    }

    public List<Fragment> getFragments(){
        return mFManager.getFragments();
    }

    /**
     * Should be returned to display fragments id of {@link ViewGroup}.
     *
     * @return resource id of {@link ViewGroup}.
     */
    protected abstract
    @IdRes
    int fragmentLayoutId();

    /**
     * 获取fragment栈顶的fragment
     *
     * @return
     */
    public NodeFragment getLastFragment(){
        return mFragmentStack.size() == 0 ? null : mFragmentStack.get(mFragmentStack.size() - 1);
    }

    public void removeFragmentsWithoutRoot(){
        int size = mFragmentStack.size();
        FragmentTransaction fragmentTransaction = mFManager.beginTransaction();
        if(mFragmentStack!=null && size>1){
            while(mFragmentStack.size()>1){
                NodeFragment thisFragment = mFragmentStack.remove(mFragmentStack.size()-1); //mFragmentStack.get(i);
                if (thisFragment != null) {
                    Log.d(TAG, "removeFragmentsWithoutRoot()   "+thisFragment.toString());
                    fragmentTransaction.remove(thisFragment).commitAllowingStateLoss();
                    fragmentTransaction = mFManager.beginTransaction();
                    thisFragment.removeFragmentStackEntity();
                }
            }
        }
    }

    /**
     * 获取fragment栈的所有fragment
     *
     * @return
     */
    public List<NodeFragment> getFragmentList(){
        return mFragmentStack;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
