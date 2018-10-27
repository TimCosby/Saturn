package utoronto.saturn.app.front_end.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import utoronto.saturn.app.R;
import utoronto.saturn.app.front_end.viewmodels.FollowingViewModel;
import android.arch.lifecycle.ViewModelProviders;

public class FollowingFragment extends Fragment {

    private FollowingViewModel mViewModel;

    public static FollowingFragment newInstance() {
        return new FollowingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_following, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FollowingViewModel.class);
        // TODO: Use the ViewModel
    }

}