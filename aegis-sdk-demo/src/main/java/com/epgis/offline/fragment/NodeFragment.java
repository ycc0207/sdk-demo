/*
 * Copyright © Yan Zhenjie. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.epgis.offline.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SUZY on 2018/3/20.
 */
public class NodeFragment extends Fragment {

    public static final int RESULT_OK = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    public static final int REQUEST_CODE_POI_SEARCH= 100;
    public static final int REQUEST_CODE_COLLECT_SUGGESTION_SEARCH= 200;
    public static final int REQUEST_CODE_COLLECT_SUGGESTION= 201;
    public static final int REQUEST_CODE_TO_MAP= 202;
    public static final int REQUEST_CODE_ROUTE_SUGGESTION_SEARCH = 203;
    private static final int REQUEST_CODE_INVALID = BaseActivity.REQUEST_CODE_INVALID;

    private static final String TAG = "LIFE_CYCLE ";

    private FragmentStackEntity mFragmentStackEntity;

    public enum ResultType {
        NONE, OK, CANCEL
    }

    public void setFragmentStackEntity(FragmentStackEntity fragmentStackEntity){
        mFragmentStackEntity = fragmentStackEntity;
    }

    public FragmentStackEntity getFragmentStackEntity(){
        return mFragmentStackEntity;
    }

    public void removeFragmentStackEntity(){
        mFragmentStackEntity = null;
    }


    /**
     * Create a new instance of a Fragment with the given class name.  This is the same as calling its empty constructor.
     *
     * @param context       context.
     * @param fragmentClass class of fragment.
     * @param <T>           subclass of {@link NodeFragment}.
     * @return new instance.
     * @deprecated In {@code Activity} with {@link BaseActivity#fragment(Class)} instead;
     * in the {@code Fragment} width {@link #fragment(Class)} instead.
     */
    @Deprecated
    public static <T extends NodeFragment> T instantiate(Context context, Class<T> fragmentClass) {
        //noinspection unchecked
        return (T) instantiate(context, fragmentClass.getName(), null);
    }

    /**
     * Create a new instance of a Fragment with the given class name.  This is the same as calling its empty constructor.
     *
     * @param context       context.
     * @param fragmentClass class of fragment.
     * @param bundle        argument.
     * @param <T>           subclass of {@link NodeFragment}.
     * @return new instance.
     * @deprecated In {@code Activity} with {@link BaseActivity#fragment(Class, Bundle)} instead;
     * in the {@code Fragment} width {@link #fragment(Class, Bundle)} instead.
     */
    @Deprecated
    public static <T extends NodeFragment> T instantiate(Context context, Class<T> fragmentClass, Bundle bundle) {
        //noinspection unchecked
        return (T) instantiate(context, fragmentClass.getName(), bundle);
    }

    /**
     * Create a new instance of a Fragment with the given class name.  This is the same as calling its empty constructor.
     *
     * @param fragmentClass class of fragment.
     * @param <T>           subclass of {@link NodeFragment}.
     * @return new instance.
     */
    public final <T extends NodeFragment> T fragment(Class<T> fragmentClass) {
        //noinspection unchecked
        return (T) instantiate(getContext(), fragmentClass.getName(), null);
    }

    /**
     * Create a new instance of a Fragment with the given class name.  This is the same as calling its empty constructor.
     *
     * @param fragmentClass class of fragment.
     * @param bundle        argument.
     * @param <T>           subclass of {@link NodeFragment}.
     * @return new instance.
     */
    public final <T extends NodeFragment> T fragment(Class<T> fragmentClass, Bundle bundle) {
        //noinspection unchecked
        return (T) instantiate(getContext(), fragmentClass.getName(), bundle);
    }

    /**
     * Toolbar.
     */
    private Toolbar mToolbar;

    /**
     * CompatActivity.
     */
    private BaseActivity mActivity;

    /**
     * Get BaseActivity.
     *
     * @return {@link BaseActivity}.
     */
    protected final BaseActivity getCompatActivity() {
        return mActivity;
    }

    /**
     * Start activity.
     *
     * @param clazz class for activity.
     * @param <T>   {@link Activity}.
     */
    protected final <T extends Activity> void startActivity(Class<T> clazz) {
        startActivity(new Intent(mActivity, clazz));
    }

    /**
     * Start activity and finish my parent.
     *
     * @param clazz class for activity.
     * @param <T>   {@link Activity}.
     */
    protected final <T extends Activity> void startActivityFinish(Class<T> clazz) {
        startActivity(new Intent(mActivity, clazz));
        mActivity.finish();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
        Log.d(TAG + getClass().getSimpleName(), " onAttach");
    }

    /**
     * Destroy me.
     */
    public void finish() {
        mActivity.onBackPressed();
//        mActivity.finishFragment();
    }

    /**
     * Set Toolbar.
     *
     * @param toolbar {@link Toolbar}.
     */

    @SuppressLint("RestrictedApi")
    public final void setToolbar(@NonNull Toolbar toolbar) {
        this.mToolbar = toolbar;
        onCreateOptionsMenu(mToolbar.getMenu(), new SupportMenuInflater(mActivity));
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
    }

    /**
     * Display home up button.
     *
     * @param drawableId drawable id.
     */
    public final void displayHomeAsUpEnabled(@DrawableRes int drawableId) {
        displayHomeAsUpEnabled(ContextCompat.getDrawable(mActivity, drawableId));
    }

    /**
     * Display home up button.
     *
     * @param drawable {@link Drawable}.
     */
    public final void displayHomeAsUpEnabled(Drawable drawable) {
        mToolbar.setNavigationIcon(drawable);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!onInterceptToolbarBack())
                    finish();
            }
        });
    }

    /**
     * Override this method, intercept backPressed of ToolBar.
     *
     * @return true, other wise false.
     */
    public boolean onInterceptToolbarBack() {
        return false;
    }

    /**
     * Get Toolbar.
     *
     * @return {@link Toolbar}.
     */
    protected final
    @Nullable
    Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * Set title.
     *
     * @param title title.
     */
    protected void setTitle(CharSequence title) {
        if (mToolbar != null)
            mToolbar.setTitle(title);
    }

    /**
     * Set title.
     *
     * @param titleId string resource id from {@code string.xml}.
     */
    protected void setTitle(int titleId) {
        if (mToolbar != null)
            mToolbar.setTitle(titleId);
    }

    /**
     * Set sub title.
     *
     * @param title sub title.
     */
    protected void setSubtitle(CharSequence title) {
        if (mToolbar != null)
            mToolbar.setSubtitle(title);
    }

    /**
     * Set sub title.
     *
     * @param titleId string resource id from {@code string.xml}.
     */
    protected void setSubtitle(int titleId) {
        if (mToolbar != null)
            mToolbar.setSubtitle(titleId);
    }

    // ------------------------- Stack ------------------------- //

    /**
     * Stack info.
     */

    /**
     * Set result.
     *
     * @param resultCode result code, one of {@link NodeFragment#RESULT_OK}, {@link NodeFragment#RESULT_CANCELED}.
     */
    protected final void setResult(@ResultCode int resultCode) {
        if(mFragmentStackEntity != null){
            mFragmentStackEntity.resultCode = resultCode;
        }
    }

    /**
     * Set result.
     *
     * @param resultCode resultCode, use {@link }.
     * @param result     {@link Bundle}.
     */
    protected final void setResult(@ResultCode int resultCode, @NonNull Bundle result) {
        if(mFragmentStackEntity != null){
            mFragmentStackEntity.resultCode = resultCode;
            mFragmentStackEntity.result = result;
        }
    }


    /**
     * You should override it.
     *
     * @param resultCode resultCode.
     * @param result     {@link Bundle}.
     */
    public void onFragmentResult(int requestCode, @ResultCode int resultCode, @Nullable Bundle result) {
    }

    /**
     * Show a fragment.
     *
     * @param clazz fragment class.
     * @param <T>   {@link NodeFragment}.
     */
    public final <T extends NodeFragment> void startFragment(Class<T> clazz) {
        try {
            NodeFragment targetFragment = clazz.newInstance();
            startFragment(targetFragment, true, REQUEST_CODE_INVALID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a fragment.
     *
     * @param clazz
     * @param bundle
     * @param <T>
     */
    public final <T extends NodeFragment> void startFragment(Class<T> clazz, Bundle bundle) {
        try {
            NodeFragment targetFragment = clazz.newInstance();
            startFragment(targetFragment, true, REQUEST_CODE_INVALID, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a fragment.
     *
     * @param clazz       fragment class.
     * @param stickyStack sticky to back stack.
     * @param <T>         {@link NodeFragment}.
     */
    public final <T extends NodeFragment> void startFragment(Class<T> clazz, boolean stickyStack) {
        try {
            NodeFragment targetFragment = clazz.newInstance();
            startFragment(targetFragment, stickyStack, REQUEST_CODE_INVALID);
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
        startFragment(targetFragment, true, REQUEST_CODE_INVALID);
    }

    /**
     * Show a fragment.
     *
     * @param targetFragment
     * @param bundle
     * @param <T>
     */
    public final <T extends NodeFragment> void startFragment(T targetFragment, Bundle bundle) {
        startFragment(targetFragment, true, REQUEST_CODE_INVALID, bundle);
    }

    /**
     * Show a fragment.
     *
     * @param targetFragment fragment to display.
     * @param stickyStack    sticky back stack.
     * @param <T>            {@link NodeFragment}.
     */
    public final <T extends NodeFragment> void startFragment(T targetFragment, boolean stickyStack) {
        startFragment(targetFragment, stickyStack, REQUEST_CODE_INVALID);
    }

    /**
     * Show a fragment for result.
     *
     * @param clazz       fragment to display.
     * @param requestCode requestCode.
     * @param <T>         {@link NodeFragment}.
     */
    @Deprecated
    public final <T extends NodeFragment> void startFragmentForResult(Class<T> clazz, int requestCode) {
        try {
            NodeFragment targetFragment = clazz.newInstance();
            startFragment(targetFragment, true, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a fragment for result.
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     * @param <T>
     */
    @Deprecated
    public final <T extends NodeFragment> void startFragmentForResult(Class<T> clazz, int requestCode, Bundle bundle) {
        try {
            NodeFragment targetFragment = clazz.newInstance();
            startFragment(targetFragment, true, requestCode, bundle);
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
        startFragment(targetFragment, true, requestCode);
    }

    /**
     * Show a fragment for result.
     *
     * @param targetFragment
     * @param requestCode
     * @param bundle
     * @param <T>
     */
    public final <T extends NodeFragment> void startFragmentForResult(T targetFragment, int requestCode, Bundle bundle) {
        startFragment(targetFragment, true, requestCode, bundle);
    }

    /**
     * Show a fragment.
     *
     * @param targetFragment fragment to display.
     * @param stickyStack    sticky back stack.
     * @param requestCode    requestCode.
     * @param <T>            {@link NodeFragment}.
     */
    private <T extends NodeFragment> void startFragment(T targetFragment, boolean stickyStack, int requestCode) {
        startFragment(targetFragment, stickyStack, requestCode, null);
    }


    /**
     * Show a fragment.
     *
     * @param targetFragment
     * @param stickyStack
     * @param requestCode
     * @param bundle
     * @param <T>
     */
    private <T extends NodeFragment> void startFragment(T targetFragment, boolean stickyStack, int requestCode, Bundle bundle) {
        mActivity.startFragment(this, targetFragment, stickyStack, requestCode, bundle);
    }


    public final <T extends NodeFragment> void replaceFragment(T thisFragment, T thatFragment) {
        mActivity.replaceFragment(thisFragment, thatFragment, true,  REQUEST_CODE_INVALID, null);
    }

    public final <T extends NodeFragment> void replaceFragment(T thisFragment, T thatFragment, Bundle bundle) {
        mActivity.replaceFragment(thisFragment, thatFragment, true,  REQUEST_CODE_INVALID, bundle);
    }

    public final NodeFragment getLastFragment() {
        return mActivity.getLastFragment();
    }

    /**
     * get bundle
     *
     * @return
     */
    public Bundle getBundle(){
        return getArguments();
    }


    private @LayoutRes

    int mLayoutResID;

    private View mView;

    public void setContentView(@LayoutRes int layoutResID) {
        mLayoutResID = layoutResID;
    }


    public void setContentView(View view) {
        mView = view;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG + getClass().getSimpleName(),  " onCreateView");
        if(mLayoutResID > 0){
            return inflater.inflate(mLayoutResID, null);
        }else if(mView != null){
            return mView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public <T extends View> T findViewById(int id){
        if(getView() !=  null){
            return getView().findViewById(id);
        }
        return null;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG + getClass().getSimpleName(),  " onCreate");
        if(savedInstanceState != null){
            mFragmentStackEntity = savedInstanceState.getParcelable(FRAGMENT_STACK_KEY);
        }
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG + getClass().getSimpleName(), " onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isSkipCycleLife()){
            return;
        }
        Log.d(TAG + getClass().getSimpleName(), " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isSkipCycleLife()){
            return;
        }
//        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        Log.d(TAG + getClass().getSimpleName(), " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isSkipCycleLife()){
            return;
        }
//        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        Log.d(TAG + getClass().getSimpleName(), " onPause");
    }

    @Override
    public void onStop() {
        mActivity.fragmentOnStop(this);
        super.onStop();
        if(isSkipCycleLife()){
            return;
        }
        Log.d(TAG + getClass().getSimpleName(), " onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG + getClass().getSimpleName(), " onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG + getClass().getSimpleName(), " onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG + getClass().getSimpleName(), " onDetach");
    }

    /**
     * 默认返回false不拦截，返回true则拦截back操作，fragment将不进行后续的退出操作
     * @return
     */
    public boolean onBackPressed() {
        return false;
    }

    private static String FRAGMENT_STACK_KEY = "fragment_stack_key";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "MainActivity NodeFragment onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelable(FRAGMENT_STACK_KEY, mFragmentStackEntity);
    }


    /**
     * 是否跳过生命周期，子类在onResume onPause onStart onStop里调用，如果返回true则方法直接return不执行
     * 这是因为Activity onPause（比如切后台）时fragment堆栈里的所有fragment都会调用onPause,onStop（onRsume同理）
     * 这是我们不希望看到的，我们只希望fragment栈顶的那个frgment生命周期同步activity，而不是所有fragment，所以用该方法判断
     * 当前fragment是否应该走生命周期。
     *
     * @return
     */
    public boolean isSkipCycleLife(){
        if(!isLastFragment() && isInFragmentList()){
            return true;
        }
        return false;
    }

    /**
     * 当前是否是栈顶的fragment
     * @return
     */
    public boolean isLastFragment(){
        if(this == getLastFragment()){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 当前的fragment是否在fragment堆栈里面
     * @return
     */
    public boolean isInFragmentList(){
        for(NodeFragment nodeFragment:mActivity.getFragmentList()){
            if(this == nodeFragment){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
