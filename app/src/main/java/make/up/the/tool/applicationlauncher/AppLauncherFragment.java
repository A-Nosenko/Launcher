package make.up.the.tool.applicationlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * @author Anatolii Nosenko
 * @version 02 March 2018
 */

public class AppLauncherFragment extends Fragment {

    private static final String TAG = AppLauncherFragment.class.getName();

    private RecyclerView recyclerView;

    public static AppLauncherFragment newInstance() {
        return new AppLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_app_launcher, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.app_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return view;
    }

    private void setupAdapter() {
        Intent intentStarting = new Intent(Intent.ACTION_MAIN);
        intentStarting.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activitiesToStarting = packageManager
                .queryIntentActivities(intentStarting, 0);
        Collections.sort(activitiesToStarting, (a, b) -> {
            PackageManager pm = getActivity().getPackageManager();
            return String.CASE_INSENSITIVE_ORDER.compare(
                    a.loadLabel(pm).toString(),
                    b.loadLabel(pm).toString());
        });

        Log.i(TAG, "There are " + activitiesToStarting.size() + " activities.");
        recyclerView.setAdapter(new AppAdapter(activitiesToStarting));
    }

    private class AppHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ResolveInfo resolveInfo;

        private ImageView imageView;
        private TextView textView;

        AppHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));

            imageView = itemView.findViewById(R.id.image);
            imageView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.text);
        }

        void bind(ResolveInfo resolveInfo) {
            this.resolveInfo = resolveInfo;
            PackageManager packageManager = getActivity().getPackageManager();
            String marker = this.resolveInfo.loadLabel(packageManager).toString();
            Drawable image = this.resolveInfo.loadIcon(packageManager);
            this.imageView.setImageDrawable(image);
            this.textView.setText(marker);
        }

        @Override
        public void onClick(View view) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;

            Intent intent = new Intent(Intent.ACTION_MAIN).
                    setClassName(activityInfo.applicationInfo.packageName,
                            activityInfo.name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class AppAdapter extends RecyclerView.Adapter<AppHolder> {
        private final List<ResolveInfo> activities;

        AppAdapter(List<ResolveInfo> activities) {
            this.activities = activities;
        }

        @Override
        public AppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new AppHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(AppHolder holder, int position) {
            ResolveInfo resolveInfo = activities.get(position);
            holder.bind(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return activities.size();
        }
    }
}
